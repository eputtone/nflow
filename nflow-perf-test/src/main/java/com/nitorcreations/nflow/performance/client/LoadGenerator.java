package com.nitorcreations.nflow.performance.client;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.nitorcreations.nflow.performance.workflow.ConstantWorkflow;
import com.nitorcreations.nflow.performance.workflow.ConstantWorkflow.QuickState;
import com.nitorcreations.nflow.rest.v1.msg.CreateWorkflowInstanceResponse;
import com.nitorcreations.nflow.rest.v1.msg.ListWorkflowInstanceResponse;

@Named
public class LoadGenerator {
  private static final Logger logger = LoggerFactory.getLogger(LoadGenerator.class);

  @Inject
  private Client client;

  private List<Integer> generateSomeLoad(int loadCount) {
    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    List<Integer> instanceIds = new LinkedList<>();
    for (int i = 0; i < loadCount; i++) {
      CreateWorkflowInstanceResponse resp = client.createWorkflow(new ConstantWorkflow().getType());
      instanceIds.add(resp.id);
    }
    logger.info("Generating {} items took {} msec", loadCount, stopWatch.getTime());
    return instanceIds;
  }

  private void checkFinished(List<Integer> instanceIds) {
    int endStateCount = 0;
    for (Integer instanceId : instanceIds) {
      ListWorkflowInstanceResponse resp = client.getWorkflowInstance(instanceId, false);
      if(resp != null && resp.state.equals(QuickState.end.name())) {
        endStateCount ++;
      }
    }
    logger.info("Found {} finished", endStateCount);

  }

  public static void main(String[] args) throws Exception {
    logger.info("Starting");

    try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(LoadGeneratorConfiguration.class)) {
      LoadGenerator loadGenerator = ctx.getBean(LoadGenerator.class);
      List<Integer> instanceIds = loadGenerator.generateSomeLoad(2000);
      Thread.sleep(300);
      loadGenerator.checkFinished(instanceIds);
    }
    logger.info("The end");
  }
}
