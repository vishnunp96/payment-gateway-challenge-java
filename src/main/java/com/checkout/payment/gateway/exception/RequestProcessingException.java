package com.checkout.payment.gateway.exception;

public class RequestProcessingException extends RuntimeException{
  public RequestProcessingException(String message) {
    super(message);
  }
}
