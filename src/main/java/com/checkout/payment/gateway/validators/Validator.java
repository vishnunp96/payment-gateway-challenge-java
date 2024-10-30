package com.checkout.payment.gateway.validators;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import org.springframework.stereotype.Component;

@Component
public class Validator {

  private static boolean validateCardNumberLength(long cardNumber) {
    String cardNumberString = Long.toString(cardNumber);
    return cardNumberString.length() >= 14 && cardNumberString.length() <= 19;
  }
  public boolean validate(PostPaymentRequest request) {
    return validateCardNumberLength(request.getCardNumber());
  }

}
