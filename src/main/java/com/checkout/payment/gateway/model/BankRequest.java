package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class BankRequest implements Serializable {

  @JsonProperty("card_number")
  private long cardNumber;
  private String currency;
  private int amount;
  private int cvv;

  @JsonProperty("expiry_date")
  private String expiryDate;

  public static BankRequest fromPaymentRequest(PostPaymentRequest paymentRequest) {
    BankRequest bankRequest = new BankRequest();
    bankRequest.setCardNumber(paymentRequest.getCardNumber());
    bankRequest.setCurrency(paymentRequest.getCurrency());
    bankRequest.setAmount(paymentRequest.getAmount());
    bankRequest.setCvv(paymentRequest.getCvv());
    bankRequest.setExpiryDate(paymentRequest.getExpiryDate());

    return bankRequest;
  }

  public long getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(long cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public int getCvv() {
    return cvv;
  }

  public void setCvv(int cvv) {
    this.cvv = cvv;
  }


  public String getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  @Override
  public String toString() {
    return "BankRequest{" +
        "cardNumber=" + cardNumber +
        ", expiryDate=" + expiryDate +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
