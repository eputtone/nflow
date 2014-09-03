package com.nitorcreations.nflow.engine.internal.executor;

import static org.slf4j.LoggerFactory.getLogger;
import static org.threeten.bp.ZonedDateTime.now;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.util.ReflectionUtils;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import com.nitorcreations.nflow.engine.internal.workflow.ObjectStringMapper;
import com.nitorcreations.nflow.engine.internal.workflow.StateExecutionImpl;
import com.nitorcreations.nflow.engine.internal.workflow.WorkflowStateMethod;
import com.nitorcreations.nflow.engine.listener.WorkflowExecutorListener;
import com.nitorcreations.nflow.engine.listener.WorkflowExecutorListener.ListenerContext;
import com.nitorcreations.nflow.engine.service.WorkflowDefinitionService;
import com.nitorcreations.nflow.engine.service.WorkflowInstanceService;
import com.nitorcreations.nflow.engine.workflow.definition.WorkflowDefinition;
import com.nitorcreations.nflow.engine.workflow.definition.WorkflowSettings;
import com.nitorcreations.nflow.engine.workflow.definition.WorkflowState;
import com.nitorcreations.nflow.engine.workflow.instance.WorkflowInstance;
import com.nitorcreations.nflow.engine.workflow.instance.WorkflowInstanceAction;

class WorkflowExecutor implements Runnable {

  private static final Logger logger = getLogger(WorkflowExecutor.class);
  private static final String MDC_KEY = "workflowInstanceId";

  private final int MAX_SUBSEQUENT_STATE_EXECUTIONS = 100;

  private final int instanceId;
  private final WorkflowDefinitionService workflowDefinitions;
  private final WorkflowInstanceService workflowInstances;
  private final ObjectStringMapper objectMapper;
  private final WorkflowExecutorListener[] executorListeners;

  WorkflowExecutor(int instanceId, ObjectStringMapper objectMapper, WorkflowDefinitionService workflowDefinitions,
      WorkflowInstanceService workflowInstances, WorkflowExecutorListener... executorListeners) {
    this.instanceId = instanceId;
    this.objectMapper = objectMapper;
    this.workflowDefinitions = workflowDefinitions;
    this.workflowInstances = workflowInstances;
    this.executorListeners = executorListeners;
  }

  @Override
  public void run() {
    try {
      MDC.put(MDC_KEY, String.valueOf(instanceId));
      runImpl();
    } catch (Throwable ex) {
      logger.error("Totally unexpected failure (e.g. deadlock) occurred.", ex);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }

  private void runImpl() {
    logger.debug("Starting.");
    WorkflowInstance instance = workflowInstances.getWorkflowInstance(instanceId);
    Duration executionLag = Duration.between(instance.nextActivation, ZonedDateTime.now());

    if (executionLag.compareTo(Duration.ofMinutes(1)) > 0) {
      logger.warn("Execution lagging {} seconds.", executionLag.getSeconds());
    }
    WorkflowDefinition<? extends WorkflowState> definition = workflowDefinitions.getWorkflowDefinition(instance.type);
    if (definition == null) {
      unscheduleUnknownWorkflowInstance(instance);
      return;
    }
    WorkflowSettings settings = definition.getSettings();
    int subsequentStateExecutions = 0;
    while (instance.processing) {
      StateExecutionImpl execution = new StateExecutionImpl(instance, objectMapper);
      ListenerContext listenerContext = executorListeners.length == 0 ? null : new ListenerContext(definition, instance, execution);
      WorkflowInstanceAction.Builder actionBuilder = new WorkflowInstanceAction.Builder(instance);
      WorkflowState state = definition.getState(instance.state);
      try {
        processBeforeListeners(listenerContext);
        processState(instance, definition, execution);
        processAfterListeners(listenerContext);
      } catch (Throwable ex) {
        logger.error("Handler threw exception, trying again later", ex);
        execution.setFailure(true);
        execution.setNextState(state);
        execution.setNextStateReason(ex.toString());
        definition.handleRetry(execution);
        processAfterFailureListeners(listenerContext, ex);
      } finally {
        subsequentStateExecutions = busyLoopPrevention(settings, subsequentStateExecutions, execution);
        instance = saveWorkflowInstanceState(execution, instance, definition, actionBuilder);
      }
    }
    logger.debug("Finished.");
  }

  private void unscheduleUnknownWorkflowInstance(WorkflowInstance instance) {
    logger.warn("Workflow type {} not configured to this nflow instance - unscheduling workflow instance", instance.type);
    instance = new WorkflowInstance.Builder(instance).setNextActivation(null)
        .setStateText("Unsupported workflow type").build();
    workflowInstances.updateWorkflowInstance(instance, null);
    logger.debug("Exiting.");
  }

  private int busyLoopPrevention(WorkflowSettings settings,
      int subsequentStateExecutions, StateExecutionImpl execution) {
    if (subsequentStateExecutions++ >= MAX_SUBSEQUENT_STATE_EXECUTIONS && execution.getNextActivation() != null) {
      logger.warn("Executed {} times without delay, forcing short transition delay", MAX_SUBSEQUENT_STATE_EXECUTIONS);
      execution.setNextActivation(execution.getNextActivation().plus(settings.getShortTransitionDelay(), ChronoUnit.MILLIS));
    }
    return subsequentStateExecutions;
  }

  private WorkflowInstance saveWorkflowInstanceState(StateExecutionImpl execution, WorkflowInstance instance,
      WorkflowDefinition<?> definition, WorkflowInstanceAction.Builder actionBuilder) {
    if (execution.getNextState() == null) {
      execution.setNextState(definition.getErrorState(), "No next state defined by state " + execution.getCurrentStateName(), ZonedDateTime.now());
      execution.setNextActivation(null);
    }
    if (definition.getMethod(execution.getNextState()) == null && execution.getNextActivation() != null)  {
      logger.info("No handler method defined for {}, clearing next activation", execution.getNextState());
      execution.setNextActivation(null);
    }
    WorkflowInstance.Builder builder = new WorkflowInstance.Builder(instance)
      .setNextActivation(execution.getNextActivation())
      .setProcessing(isNextActivationImmediately(execution))
      .setStateText(execution.getNextStateReason());
    if (execution.isFailure()) {
      builder.setRetries(execution.getRetries() + 1);
    } else {
      builder.setState(execution.getNextState()).setRetries(0);
    }
    actionBuilder.setExecutionEnd(now()).setStateText(execution.getNextStateReason());
    workflowInstances.updateWorkflowInstance(builder.build(), actionBuilder.build());
    return builder.setOriginalStateVariables(instance.stateVariables).build();
  }

  private boolean isNextActivationImmediately(StateExecutionImpl execution) {
    return execution.getNextActivation() != null && execution.getNextActivation().compareTo(ZonedDateTime.now()) <= 0;
  }

  private void processState(WorkflowInstance instance,
      WorkflowDefinition<?> definition, StateExecutionImpl execution) {
    WorkflowStateMethod method = definition.getMethod(instance.state);
    Object[] args = objectMapper.createArguments(execution, method);
    ReflectionUtils.invokeMethod(method.method, definition, args);
    objectMapper.storeArguments(execution, method, args);
  }

  private void processBeforeListeners(ListenerContext listenerContext) {
    for (WorkflowExecutorListener listener : executorListeners) {
      listener.beforeProcessing(listenerContext);
    }
  }

  private void processAfterListeners(ListenerContext listenerContext) {
    for (WorkflowExecutorListener listener : executorListeners) {
      listener.afterProcessing(listenerContext);
    }
  }

  private void processAfterFailureListeners(ListenerContext listenerContext, Throwable ex) {
    for (WorkflowExecutorListener listener : executorListeners) {
      listener.afterFailure(listenerContext, ex);
    }
  }

}
