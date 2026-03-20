package com.multiconn.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiconn.backend.model.User;
import com.multiconn.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final UserRepository userRepository;

    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getBalance(@AuthenticationPrincipal OAuth2User principal) {
        try {
            String email = principal.getAttribute("email");

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + email));

            BigDecimal balance = user.getWallet().getBalance();

            return ResponseEntity.ok(Map.of("balance", balance));
        } catch (Exception e) {

            return ResponseEntity.internalServerError().build();
        }
    }

}
