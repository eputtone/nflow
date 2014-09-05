package com.nitorcreations.nflow.performance.client;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.nitorcreations.nflow.performance.workflow.QuickWorkflow;

@Named
public class LoadGenerator {
  private static final Logger logger = LoggerFactory.getLogger(LoadGenerator.class);
  private final StopWatch stopWatch = new StopWatch();

  @Inject
  private Client client;

  private void generateSomeLoad(int loadCount) {
    stopWatch.start();
    for (int i = 0; i < loadCount; i++) {
      client.createWorkflow(new QuickWorkflow().getType());
    }
    logger.info("Generating {} items took {} msec", loadCount, stopWatch.getTime());
  }

  public static void main(String[] args) {
    logger.info("Starting");

    try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(LoadGeneratorConfiguration.class)) {
      LoadGenerator loadGenerator = ctx.getBean(LoadGenerator.class);
      loadGenerator.generateSomeLoad(20);
    }
    logger.info("The end");
  }
}
