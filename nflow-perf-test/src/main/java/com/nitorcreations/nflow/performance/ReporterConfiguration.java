package com.nitorcreations.nflow.performance;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

/**
 * Sends metrics data to a aggregation/display server using Graphite protocol.
 * Receiving server can be e.g. https://graphite.readthedocs.org/ or http://riemann.io/.
 */
@Configuration
public class ReporterConfiguration {
  private static final Logger logger = LoggerFactory.getLogger(ReporterConfiguration.class);
  @Inject
  private MetricRegistry metricRegistry;

  @SuppressWarnings("resource")
  @Bean
  public GraphiteReporter graphiteReporter() {
    // TODO configure host,port
    String host = "localhost";
    int port = 2003;
    Graphite graphite = new Graphite(new InetSocketAddress(host, port));
    logger.info("Enabling GraphiteReporter {}:{}", host, port);
    GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry).prefixedWith("nflow").build(graphite);
    reporter.start(5, TimeUnit.SECONDS);
    return reporter;
  }
}
