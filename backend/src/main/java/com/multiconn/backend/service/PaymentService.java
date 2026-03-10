package com.multiconn.backend.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${stripe.api.secretKey}")
    private String secretKey;

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public String createCheckoutSession(BigDecimal amount, String userEmail) throws StripeException {

        // Stripe deals with the smallest coin unity (cents)
        long amountInCents = amount.multiply(new BigDecimal(100)).longValue();

        var productDescription = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName("AI Platform Credits")
                .setDescription("Créditos para uso dos Agentes de IA")
                .build();

        var productToCharge = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmount(amountInCents)
                .setProductData(productDescription)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                // One-time payment
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // Redirect user based on stipe event
                .setSuccessUrl(this.successUrl)
                .setCancelUrl(this.cancelUrl)
                .setCustomerEmail(userEmail)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(productToCharge)
                        .build())
                .build();

        Session session = Session.create(params);

        return session.getUrl();
    }
}
