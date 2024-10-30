package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    PostPaymentResponse payment = new PostPaymentResponse();
    payment.setId(UUID.randomUUID());
    payment.setAmount(10);
    payment.setCurrency("USD");
    payment.setStatus(PaymentStatus.AUTHORIZED);
    payment.setExpiryMonth(12);
    payment.setExpiryYear(2024);
    payment.setCardNumberLastFour(4321);

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("/payment/" + payment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }

  private static Stream<Arguments> goodPaymentExamples() {
    return Stream.of(
//        Arguments.of(100, "GBP", 4, 2025, 2222405343248877L, 123,
//            8877, PaymentStatus.AUTHORIZED),
        Arguments.of(60000, "USD", 1, 2026, 2222405343248112L, 456,
            8112, PaymentStatus.DECLINED)
    );
  }
  @ParameterizedTest
  @MethodSource("goodPaymentExamples")
  void whenPostedPaymentThen201IsReturned(int amount, String currency,
      int expiryMonth, int expiryYear, long cardNumber, int cvv,
      int cardNumberLastFour, PaymentStatus expectedResponse) throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setAmount(amount);
    request.setCurrency(currency);
    request.setExpiryMonth(expiryMonth);
    request.setExpiryYear(expiryYear);
    request.setCardNumber(cardNumber);
    request.setCvv(cvv);

    String requestJson = new ObjectMapper().writeValueAsString(request);

    mvc.perform(MockMvcRequestBuilders.post("/payment/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.status").value(expectedResponse.getName()))
        .andExpect(jsonPath("$.amount").value(amount))
        .andExpect(jsonPath("$.cardNumberLastFour").value(cardNumberLastFour))
        .andExpect(jsonPath("$.expiryMonth").value(expiryMonth))
        .andExpect(jsonPath("$.expiryYear").value(expiryYear))
        .andExpect(jsonPath("$.currency").value(currency));
  }



  private static Stream<Arguments> badPaymentExamples() {
    return Stream.of(
        Arguments.of(10, "USD", 1, 2025, 123L, 890)
        , Arguments.of(10, "USD", 1, 2024, 5678567856781234L, 890)
        , Arguments.of(10, "INP", 1, 2025, 5678567856781234L, 890)
        , Arguments.of(10, "USD", 1, 2025, 5678567856781234L, 55778)
        , Arguments.of(-1, "USD", 1, 2025, 5678567856781234L, 890)
    );
  }
  @ParameterizedTest
  @MethodSource("badPaymentExamples")
  void whenPostedBadPaymentThen422IsReturned(
      int amount, String currency, int expiryMonth, int expiryYear,
      long cardNumber, int cvv) throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setAmount(amount);
    request.setCurrency(currency);
    request.setExpiryMonth(expiryMonth);
    request.setExpiryYear(expiryYear);
    request.setCardNumber(cardNumber);
    request.setCvv(cvv);

    String requestJson = new ObjectMapper().writeValueAsString(request);

    mvc.perform(MockMvcRequestBuilders.post("/payment/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").isEmpty())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()));
  }
}
