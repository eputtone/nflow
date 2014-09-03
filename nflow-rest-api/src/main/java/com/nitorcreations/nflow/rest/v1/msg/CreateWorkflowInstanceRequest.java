package com.nitorcreations.nflow.rest.v1.msg;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.threeten.bp.ZonedDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@ApiModel(value =
  "Request for submit new workflow instance. Note that if externalId is given, " +
  "type and externalId pair must be unique hence enabling retry-safety.")
@SuppressFBWarnings(value="URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification="jackson reads dto fields")
public class CreateWorkflowInstanceRequest {

  @NotNull
  @Size(max=30)
  @ApiModelProperty(value = "Workflow definition identifier", required=true)
  public String type;

  @Size(max=64)
  @ApiModelProperty(value = "Main business key or identifier for the started workflow instance", required=false)
  public String businessKey;

  @Size(max=64)
  @ApiModelProperty(value = "Start state identifier if other than default initial state set in workflow definition", required=false)
  public String startState;

  @Size(max=64)
  @ApiModelProperty(value = "Unique external identifier under workflow type. Generated by nflow if not given.", required=false)
  public String externalId;

  @ApiModelProperty(value = "JSON document that contains business information", required=false)
  public JsonNode requestData;

  @ApiModelProperty(value = "Start time for workflow execution, if missing 'now'", required=false)
  public ZonedDateTime activationTime;

}
