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
import com.nitorcreations.nflow.rest.v1.msg.ListWorkflowInstanceResponse;
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
    CreateWorkflowInstanceRequest request = new CreateWorkflowInstanceRequest();
    request.type = workflowType;
    request.businessKey = UUID.randomUUID().toString();
    request.requestData = (new ObjectMapper()).valueToTree(
            new CreditApplicationWorkflow.CreditApplication("CUST123", new BigDecimal(100l)));
    request.externalId = UUID.randomUUID().toString();
    return makeInstanceRequest(request, CreateWorkflowInstanceResponse.class);
  }

  public ListWorkflowInstanceResponse getWorkflowInstance(int instanceId, boolean fetchActions) {
    WebClient restReq = fromClient(workflowInstanceResource, true).path(Integer.toString(instanceId));
    if(fetchActions) {
      return restReq.query("include", "actions").get(ListWorkflowInstanceResponse.class);
    }
    return restReq.get(ListWorkflowInstanceResponse.class);
  }

  private <T> T makeInstanceRequest(Object request, Class<T> responseClass) {
    return fromClient(workflowInstanceResource, true).put(request, responseClass);
  }
}
