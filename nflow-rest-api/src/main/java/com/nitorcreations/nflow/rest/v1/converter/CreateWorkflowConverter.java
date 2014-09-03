package com.nitorcreations.nflow.rest.v1.converter;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.threeten.bp.ZonedDateTime;

import com.nitorcreations.nflow.engine.workflow.instance.WorkflowInstance;
import com.nitorcreations.nflow.engine.workflow.instance.WorkflowInstanceFactory;
import com.nitorcreations.nflow.rest.v1.msg.CreateWorkflowInstanceRequest;
import com.nitorcreations.nflow.rest.v1.msg.CreateWorkflowInstanceResponse;

@Component
public class CreateWorkflowConverter {
  private final WorkflowInstanceFactory factory;

  @Inject
  public CreateWorkflowConverter(WorkflowInstanceFactory factory) {
    this.factory = factory;
  }

  public WorkflowInstance convertAndValidate(CreateWorkflowInstanceRequest req) {
    WorkflowInstance.Builder builder = factory.newWorkflowInstanceBuilder().setType(req.type)
        .setBusinessKey(req.businessKey).setExternalId(req.externalId);
    if (req.activationTime == null) {
      builder.setNextActivation(ZonedDateTime.now());
    } else {
      builder.setNextActivation(req.activationTime);
    }
    if (StringUtils.isNotEmpty(req.startState)) {
      builder.setState(req.startState);
    }
    if (req.requestData != null) {
      builder.putStateVariable("req", req.requestData);
    }
    return builder.build();
  }

  public CreateWorkflowInstanceResponse convert(WorkflowInstance instance) {
    CreateWorkflowInstanceResponse resp = new CreateWorkflowInstanceResponse();
    resp.id = instance.id;
    resp.type = instance.type;
    resp.businessKey = instance.businessKey;
    resp.externalId = instance.externalId;
    return resp;
  }

}
