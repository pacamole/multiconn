package com.multiconn.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiconn.backend.service.PaymentService;
import com.stripe.exception.StripeException;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> createCheckout(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal OAuth2User principal) {

        try {
            BigDecimal amount = new BigDecimal(request.get("amount"));

            String userEmail = principal.getAttribute("email");

            String checkoutUrl = paymentService.createCheckoutSession(amount, userEmail);
            return ResponseEntity.ok(Map.of("url", checkoutUrl));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }

    }

}
