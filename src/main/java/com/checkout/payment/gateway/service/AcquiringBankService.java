package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.client.AcquiringBankClient;
import com.checkout.payment.gateway.model.BankRequest;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AcquiringBankService {
  private static final Logger LOG = LoggerFactory.getLogger(AcquiringBankService.class);
  private final AcquiringBankClient bankClient;

  public AcquiringBankService(AcquiringBankClient bankClient) {
    this.bankClient = bankClient;
  }

  public boolean authorizePaymentRequest(PostPaymentRequest paymentRequest) {
    LOG.debug("Reaching out to bank to process payment request");
    BankRequest bankRequest = BankRequest.fromPaymentRequest(paymentRequest);
    return bankClient.submitPaymentRequest(bankRequest).isAuthorized();
  }

}
