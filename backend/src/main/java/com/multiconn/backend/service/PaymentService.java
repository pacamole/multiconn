package com.multiconn.backend.service;

import com.multiconn.backend.model.PaymentHistory;
import com.multiconn.backend.model.User;
import com.multiconn.backend.model.Wallet;
import com.multiconn.backend.repository.PaymentHistoryRepository;
import com.multiconn.backend.repository.UserRepository;
import com.multiconn.backend.repository.WalletRepository;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

        private final UserRepository userRepository;

        private final PaymentHistoryRepository paymentHistoryRepository;
        private final WalletRepository walletRepository;

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

        @Transactional
        public void processSuccessfulPayment(String email, BigDecimal amountPaid, String stripeSessionId) {
                if (paymentHistoryRepository.existsByStripeSessionId(stripeSessionId)) {
                        throw new IllegalArgumentException(
                                        "ID da Sessão de pagamento já processada: " + stripeSessionId);
                }

                User user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));

                Wallet wallet = user.getWallet();

                wallet.setBalance(wallet.getBalance().add(amountPaid));

                PaymentHistory receipt = new PaymentHistory();
                receipt.setStripeSessionId(stripeSessionId);
                receipt.setAmount(amountPaid);
                receipt.setUser(user);

                walletRepository.save(wallet);
                paymentHistoryRepository.save(receipt);

                return;
        }
}
