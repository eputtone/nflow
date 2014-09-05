package com.nitorcreations.nflow.performance.client;

import static org.apache.cxf.jaxrs.client.WebClient.fromClient;

import java.math.BigDecimal;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.cxf.jaxrs.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nitorcreations.nflow.engine.workflow.definition.WorkflowDefinition;
import com.nitorcreations.nflow.rest.v1.msg.CreateWorkflowInstanceRequest;
import com.nitorcreations.nflow.rest.v1.msg.CreateWorkflowInstanceResponse;
import com.nitorcreations.nflow.tests.demo.CreditApplicationWorkflow;

@Named
public class Client {

  @Inject
  @Named("workflowInstance")
  private WebClient workflowInstanceResource;

  public CreateWorkflowInstanceResponse createWorkflow(WorkflowDefinition<?> def) {
    return createWorkflow(def.getType());
  }

  public CreateWorkflowInstanceResponse createWorkflow(String workflowType) {
    CreateWorkflowInstanceRequest req = new CreateWorkflowInstanceRequest();
    req.type = workflowType;
    req.businessKey = UUID.randomUUID().toString();
    req.requestData = (new ObjectMapper()).valueToTree(
            new CreditApplicationWorkflow.CreditApplication("CUST123", new BigDecimal(100l)));
    req.externalId = UUID.randomUUID().toString();
    return fromClient(workflowInstanceResource, true).put(req, CreateWorkflowInstanceResponse.class);
  }
}
