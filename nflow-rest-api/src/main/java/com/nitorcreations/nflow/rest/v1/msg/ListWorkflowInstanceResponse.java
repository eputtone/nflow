package com.nitorcreations.nflow.rest.v1.msg;

import java.util.List;

import org.threeten.bp.ZonedDateTime;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@ApiModel(value = "Basic information of workflow instance")
@SuppressFBWarnings(value="URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification="jackson reads dto fields")
public class ListWorkflowInstanceResponse {

  @ApiModelProperty(value = "Identifier of the new workflow instance", required=true)
  public int id;

  @ApiModelProperty(value = "Workflow definition identifier", required=true)
  public String type;

  @ApiModelProperty(value = "Main business key or identifier for the started workflow instance", required=false)
  public String businessKey;

  @ApiModelProperty(value = "State of the workflow instance", required=true)
  public String state;

  @ApiModelProperty(value = "Text of describing the reason for state (free text)", required=false)
  public String stateText;

  @ApiModelProperty(value = "Next activation time for workflow instance processing", required=false)
  public ZonedDateTime nextActivation;

  @ApiModelProperty(value = "State change attempts. One instance for each processing attempt.", required=false)
  public List<Action> actions;



}
