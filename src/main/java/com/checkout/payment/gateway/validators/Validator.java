package com.checkout.payment.gateway.validators;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Validator {

  /**
   * Any bigger and this can be switched out for a chain of responsibility pattern.
   * Could refactor to inject different YearMonth as current date.
   * */

  private final Set<String> isoCurrencyCodes;
  public Validator(List<String> isoCurrencyCodes) {
    this.isoCurrencyCodes = new HashSet<>(isoCurrencyCodes);
  }

  private static boolean validateCardNumberLength(long cardNumber) {
    String cardNumberString = Long.toString(cardNumber);
    return cardNumberString.length() >= 14 && cardNumberString.length() <= 19;
  }

  private static boolean validateYearWithMonth(int expiryYear, int expiryMonth) {
    try {
      YearMonth expiryTime = YearMonth.of(expiryYear, expiryMonth);
      return expiryTime.isAfter(YearMonth.now());
    } catch (DateTimeException ex) {
      return false;
    }
  }

  private static boolean validateCurrencyCodeLength(String currencyCode) {
    return currencyCode.length() == 3;
  }

  private boolean validateCurrencyCodeIso(String currencyCode) {
    return isoCurrencyCodes.contains(currencyCode);
  }

  private static boolean validateCvvLength(int cvv) {
    String cvvString = Integer.toString(cvv);
    return cvvString.length() <= 4;
  }

  private static boolean validateNonNegativeAmount(int amount) {
    return amount >= 0;
  }

  public boolean validate(PostPaymentRequest request) {
    return validateCardNumberLength(request.getCardNumber()) &&
        validateYearWithMonth(request.getExpiryYear(), request.getExpiryMonth()) &&
        validateCurrencyCodeLength(request.getCurrency()) &&
        validateCurrencyCodeIso(request.getCurrency()) &&
        validateCvvLength(request.getCvv()) &&
        validateNonNegativeAmount(request.getAmount())
        ;
  }

}
