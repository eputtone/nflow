package com.nitorcreations.nflow.performance.client;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import com.nitorcreations.nflow.tests.config.RestClientConfiguration;

@Configuration
@ComponentScan(basePackageClasses = LoadGeneratorConfiguration.class)
@Import(RestClientConfiguration.class)
public class LoadGeneratorConfiguration {

  @Bean
  public StandardEnvironment env() {
    return new StandardEnvironment();
  }


  @PostConstruct
  public void foo() {
    Map<String, Object> props = new LinkedHashMap<>();
    props.put("nflow.url", "http://localhost:7001/");
    env().getPropertySources().addLast(new MapPropertySource("override", props));
  }

}
