package com.multiconn.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiconn.backend.service.PaymentService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

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

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) throws Exception {

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            if ("checkout.session.completed".equals(event.getType())) {

                // deserializeUnsafe() para ignorar conflito de versões da API
                Session session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
                String sessionId = session.getId();

                String customerEmail = "Usuario de Teste CLI";
                if (session.getCustomerDetails() != null && session.getCustomerDetails().getEmail() != null) {
                    customerEmail = session.getCustomerDetails().getEmail();
                }

                Long amountTotalCents = session.getAmountTotal();
                BigDecimal amountPaid = BigDecimal.valueOf(amountTotalCents).divide(new BigDecimal(100));

                paymentService.processSuccessfulPayment(customerEmail, amountPaid, sessionId);

                System.out.println("!! Pagamento com $10 confirmado pelo o usuário: " + customerEmail);
                return ResponseEntity.ok("Webhook (Stripe) 'completed' processado com sucesso");
            }

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().get();

                String customerEmail = session.getCustomerDetails().getEmail();

                System.out.println("!! Pagamento com $10 confirmado pelo o usuário: " + customerEmail);
                return ResponseEntity.ok("Webhook (Stripe) 'completed' processado com sucesso");

            }
        } catch (Exception e) {
            System.err.println("Alerta de erro na conclusão do pagamento" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro de validação" + e.getMessage());
        }
        return null;
    }

}
