package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.exception.RequestProcessingException;
import com.checkout.payment.gateway.model.BankRequest;
import com.checkout.payment.gateway.model.BankResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AcquiringBankClient {

  private final RestTemplate restTemplate;
  private final String acquiringBankUrl;

  public AcquiringBankClient(RestTemplate restTemplate,
      @Value("${bank.endpoint.url}") String acquiringBankUrl) {
    this.restTemplate = restTemplate;
    this.acquiringBankUrl = acquiringBankUrl;
  }

  public BankResponse submitPaymentRequest(BankRequest bankRequest)
      throws RequestProcessingException, RestClientException {
    ResponseEntity<BankResponse> response = restTemplate.postForEntity(acquiringBankUrl,
        bankRequest, BankResponse.class);
    if (!response.getStatusCode().is2xxSuccessful()) {
      return new BankResponse();
    }
    return response.getBody();
  }

}
