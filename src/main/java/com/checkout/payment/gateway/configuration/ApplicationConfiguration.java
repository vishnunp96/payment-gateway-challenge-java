package com.checkout.payment.gateway.configuration;

import java.time.Duration;
import java.util.List;
import com.checkout.payment.gateway.validators.Validator;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfiguration {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .setConnectTimeout(Duration.ofMillis(10000))
        .setReadTimeout(Duration.ofMillis(10000))
        .build();
  }

  @Bean
  public Validator validator() {
    return new Validator(List.of("USD","CNY","GBP"));
  }
}
