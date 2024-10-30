package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.exception.RequestProcessingException;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import com.checkout.payment.gateway.validators.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final Validator validator;
  private final AcquiringBankService bankService;

  public PaymentGatewayService(PaymentsRepository paymentsRepository,
      Validator validator, AcquiringBankService bankService) {
    this.paymentsRepository = paymentsRepository;
    this.validator = validator;
    this.bankService = bankService;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public UUID processPayment(PostPaymentRequest paymentRequest) {
    LOG.debug("Processing new payment request");
    if (!validator.validate(paymentRequest)){
      throw new RequestProcessingException("Payment request details invalid");
    }

    UUID newPaymentId = UUID.randomUUID();
    PostPaymentResponse response = PostPaymentResponse.fromPaymentRequest(paymentRequest);
    response.setId(newPaymentId);

    response.setStatus(PaymentStatus.DECLINED);
    if (bankService.authorizePaymentRequest(paymentRequest)){
      response.setStatus(PaymentStatus.AUTHORIZED);
    }

    paymentsRepository.add(response);
    return newPaymentId;
  }
}
