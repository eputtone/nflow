package com.nitorcreations.nflow.performance.workflow;

import static org.joda.time.DateTime.now;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nitorcreations.nflow.engine.workflow.definition.Mutable;
import com.nitorcreations.nflow.engine.workflow.definition.StateExecution;
import com.nitorcreations.nflow.engine.workflow.definition.StateVar;
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
		retryTwiceState("Retries once and goes then goes to scheduleState"),
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
	
	public void start(StateExecution execution) {
		// nothing here
		execution.setVariable(key, 0);
		execution.setNextState(QuickState.quickState, "Time for quickness", now());
	}
	public void quickState(StateExecution execution) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// ignore
		}
		execution.setNextState(QuickState.retryTwiceState, "Go do some retries", now());
	}
	public void retryTwiceState(StateExecution execution) {
		//"Retries once and goes then goes to scheduleState"),
		Integer retryCount = execution.getVariable(key, Integer.class);
		retryCount ++;
		execution.setVariable(key, retryCount);
		if(retryCount > 2) {
			logger.info("Retry count {}. Go to next state", retryCount);
			execution.setNextState(QuickState.scheduleState, "Schedule some action", now());
			return;
		}
		throw new RuntimeException("Retry count " + retryCount + ". Retrying");
	}
	
	public void scheduleState(StateExecution execution) {
		execution.setNextState(QuickState.slowState, "Schedule some action", now().plusSeconds(3));
	}

	public void slowState(StateExecution execution) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// ignore
		}
		execution.setNextState(QuickState.end, "Goto end", now());
	}
	
	public void end(StateExecution execution) {
		logger.info("end reached");
	}
	
	public void error(StateExecution execution) {
		
	}
}
