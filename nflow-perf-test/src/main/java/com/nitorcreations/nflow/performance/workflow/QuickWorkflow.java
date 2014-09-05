package com.nitorcreations.nflow.performance.workflow;

import static com.nitorcreations.nflow.engine.workflow.definition.NextAction.moveToState;
import static com.nitorcreations.nflow.engine.workflow.definition.NextAction.moveToStateAfter;
import static org.joda.time.DateTime.now;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nitorcreations.nflow.engine.workflow.definition.NextAction;
import com.nitorcreations.nflow.engine.workflow.definition.StateExecution;
import com.nitorcreations.nflow.engine.workflow.definition.WorkflowDefinition;
import com.nitorcreations.nflow.engine.workflow.definition.WorkflowSettings;
import com.nitorcreations.nflow.engine.workflow.definition.WorkflowState;
import com.nitorcreations.nflow.engine.workflow.definition.WorkflowStateType;

/**
 * Deterministic workflow that executes quickly.
 */
public class QuickWorkflow extends WorkflowDefinition<QuickWorkflow.QuickState>{
	private static final Logger logger = LoggerFactory.getLogger(QuickWorkflow.class);
	private final String key = "retries";
	public static enum QuickState implements WorkflowState {
		start(WorkflowStateType.start, "Start"),
		quickState("This executes fast then goes to retryTwice"),
		retryTwiceState("Retries twice and goes then goes to scheduleState"),
		scheduleState("Goes to slowState, in 3 sec"),
		slowState("This executes bit slower. Goes to end"),
		end(WorkflowStateType.end, "End"),
		error("Error. Should not be used.");
		private final WorkflowStateType type;
		private final String description;

		private QuickState(String description) {
			this(WorkflowStateType.normal, description);
		}

		private QuickState(WorkflowStateType type, String description) {
			this.type = type;
			this.description = description;
		}

		@Override
		public WorkflowStateType getType() {
			return type;
		}

		@Override
		public String getName() {
			return name();
		}

		@Override
		public String getDescription() {
			return description;
		}
	}

	public QuickWorkflow() {
		super(QuickWorkflow.class.getSimpleName(), QuickState.start, QuickState.error, new WorkflowSettings(null) {
			@Override
			public int getErrorTransitionDelay() {
				return 5000;
			}
		});
	}

	public NextAction start(StateExecution execution) {
		// nothing here
		execution.setVariable(key, 0);
		return moveToState(QuickState.quickState, "Time for quickness");
	}
	public NextAction quickState(StateExecution execution) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// ignore
		}
		return moveToState(QuickState.retryTwiceState, "Go do some retries");
	}
	public NextAction retryTwiceState(StateExecution execution) {
		// Retries once and goes then goes to scheduleState
		Integer retryCount = execution.getVariable(key, Integer.class);
		retryCount ++;
		execution.setVariable(key, retryCount);
		if(retryCount > 2) {
			logger.info("Retry count {}. Go to next state", retryCount);
			return moveToState(QuickState.scheduleState, "Schedule some action");

		}
		throw new RuntimeException("Retry count " + retryCount + ". Retrying");
	}

	public NextAction scheduleState(StateExecution execution) {
		return moveToStateAfter(QuickState.slowState, now().plusSeconds(3), "Schedule some action");
	}

	public NextAction slowState(StateExecution execution) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// ignore
		}
		return NextAction.stopInState(QuickState.end, "Goto end");
	}

	public NextAction error(StateExecution execution) {
		logger.error("should not happen");
		return null;
	}
}
