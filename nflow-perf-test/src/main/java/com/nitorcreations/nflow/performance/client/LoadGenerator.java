package com.nitorcreations.nflow.performance.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.nitorcreations.nflow.performance.workflow.QuickWorkflow;

public class LoadGenerator {
  private static final Logger logger = LoggerFactory.getLogger(LoadGenerator.class);


  public static void main(String[] args) {
    logger.info("Starting");

    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(LoadGeneratorConfiguration.class);
    Client client = ctx.getBean(Client.class);
    client.createWorkflow(new QuickWorkflow().getType());
    logger.info("JEE");

    ctx.close();
    logger.info("The end");
  }
}
