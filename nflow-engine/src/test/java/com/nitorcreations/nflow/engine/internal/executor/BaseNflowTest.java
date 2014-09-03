package com.nitorcreations.nflow.engine.internal.executor;

import static java.lang.Boolean.FALSE;

import java.util.LinkedHashMap;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.threeten.bp.ZonedDateTime;

import com.nitorcreations.nflow.engine.workflow.instance.WorkflowInstance;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("serial")
public abstract class BaseNflowTest {

  protected WorkflowInstance.Builder constructWorkflowInstanceBuilder() {
    return new WorkflowInstance.Builder()
      .setType("dummy")
      .setState("CreateLoan")
      .setStateText(null)
      .setNextActivation(ZonedDateTime.now())
      .setProcessing(FALSE)
      .setRetries(0)
      .setOwner("flowInstance1")
      .setStateVariables(new LinkedHashMap<String,String>() {{put("req", "{ \"parameter\": \"abc\" }"); }});
  }

}
