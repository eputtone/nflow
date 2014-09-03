package com.nitorcreations.nflow.engine.workflow.instance;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.threeten.bp.ZonedDateTime;

import com.nitorcreations.nflow.engine.internal.workflow.ObjectStringMapper;

public class WorkflowInstance {

  public final Integer id;
  public final String type;
  public final String businessKey;
  public final String externalId;
  public final String state;
  public final String stateText;
  public final ZonedDateTime nextActivation;
  public final boolean processing;
  public final Map<String, String> stateVariables;
  public final Map<String, String> originalStateVariables;
  public final List<WorkflowInstanceAction> actions;
  public final int retries;
  public final ZonedDateTime created;
  public final ZonedDateTime modified;
  public final String owner;

  WorkflowInstance(Builder builder) {
    this.id = builder.id;
    this.type = builder.type;
    this.businessKey = builder.businessKey;
    this.externalId = builder.externalId;
    this.state = builder.state;
    this.stateText = builder.stateText;
    this.nextActivation = builder.nextActivation;
    this.processing = builder.processing;
    this.originalStateVariables = builder.originalStateVariables;
    this.stateVariables = builder.stateVariables;
    this.actions = builder.actions;
    this.retries = builder.retries;
    this.created = builder.created;
    this.modified = builder.modified;
    this.owner = builder.owner;
  }

  public static class Builder {

    Integer id;
    String type;
    String businessKey;
    String externalId;
    String state;
    String stateText;
    ZonedDateTime nextActivation;
    boolean processing;
    final Map<String, String> originalStateVariables = new LinkedHashMap<>();
    final Map<String, String> stateVariables = new LinkedHashMap<>();
    List<WorkflowInstanceAction> actions;
    int retries;
    ZonedDateTime created;
    ZonedDateTime modified;
    String owner;

    private ObjectStringMapper mapper;

    public Builder() {
    }

    public Builder(ObjectStringMapper objectMapper) {
      this.mapper = objectMapper;
    }

    public Builder(WorkflowInstance copy) {
      this.id = copy.id;
      this.type = copy.type;
      this.businessKey = copy.businessKey;
      this.externalId = copy.externalId;
      this.state = copy.state;
      this.stateText = copy.stateText;
      this.nextActivation = copy.nextActivation;
      this.processing = copy.processing;
      this.originalStateVariables.putAll(copy.originalStateVariables);
      this.stateVariables.putAll(copy.stateVariables);
      this.retries = copy.retries;
      this.created = copy.created;
      this.modified = copy.modified;
      this.owner = copy.owner;
    }

    public Builder setId(Integer id) {
      this.id = id;
      return this;
    }

    public Builder setType(String type) {
      this.type = type;
      return this;
    }

    public Builder setBusinessKey(String businessKey) {
      this.businessKey = businessKey;
      return this;
    }

    public Builder setExternalId(String externalId) {
      this.externalId = externalId;
      return this;
    }

    public Builder setState(String state) {
      this.state = state;
      return this;
    }

    public Builder setStateText(String stateText) {
      this.stateText = stateText;
      return this;
   }

    public Builder setNextActivation(ZonedDateTime nextActivation) {
      this.nextActivation = nextActivation;
      return this;
    }

    public Builder setProcessing(boolean processing) {
      this.processing = processing;
      return this;
    }

    public Builder setOriginalStateVariables(Map<String, String> originalStateVariables) {
      this.originalStateVariables.clear();
      this.originalStateVariables.putAll(originalStateVariables);
      return this;
    }

    public Builder setStateVariables(Map<String, String> stateVariables) {
      this.stateVariables.clear();
      this.stateVariables.putAll(stateVariables);
      return this;
    }

    public Builder putStateVariable(String key, String value) {
      this.stateVariables.put(key, value);
      return this;
    }

    public Builder putStateVariable(String key, Object value) {
      if (mapper == null) {
        throw new IllegalStateException("WorkflowInstance.Builder must be created using WorkflowInstanceFactory.newWorkflowInstanceBuilder()");
      }
      this.stateVariables.put(key, mapper.convertFromObject(key, value));
      return this;
    }

    public Builder setActions(List<WorkflowInstanceAction> actions) {
      this.actions = actions;
      return this;
    }

    public Builder setRetries(int retries) {
      this.retries = retries;
      return this;
    }

    public Builder setCreated(ZonedDateTime created) {
      this.created = created;
      return this;
    }

    public Builder setModified(ZonedDateTime modified) {
      this.modified = modified;
      return this;
    }

    public Builder setOwner(String owner) {
      this.owner = owner;
      return this;
    }

    public WorkflowInstance build() {
      return new WorkflowInstance(this);
    }

  }

}
