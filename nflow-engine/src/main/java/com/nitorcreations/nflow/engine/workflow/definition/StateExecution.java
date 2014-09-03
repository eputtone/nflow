package com.nitorcreations.nflow.engine.workflow.definition;

import org.threeten.bp.ZonedDateTime;

public interface StateExecution {

  String getBusinessKey();

  int getRetries();
  boolean isFailure();

  String getVariable(String name);
  <T> T getVariable(String name, Class<T> type);
  String getVariable(String name, String defaultValue);
  void setVariable(String name, String value);
  void setVariable(String name, Object value);

  void setNextActivation(ZonedDateTime time);
  void setNextState(WorkflowState state);
  void setNextStateReason(String stateText);
  void setNextState(WorkflowState state, String stateText, ZonedDateTime time);
  void setFailure(boolean failure);
  void setSaveTrace(boolean saveTrace);

}
