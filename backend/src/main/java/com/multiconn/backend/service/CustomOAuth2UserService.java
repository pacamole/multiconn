package com.multiconn.backend.service;

import java.math.BigDecimal;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.multiconn.backend.model.User;
import com.multiconn.backend.model.Wallet;
import com.multiconn.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User gUser = super.loadUser(userRequest);

        String googleId = gUser.getAttribute("sub");
        String email = gUser.getAttribute("email");
        String name = gUser.getAttribute("name");

        userRepository.findByGoogleAccountId(googleId).orElseGet(() -> {
            Wallet wallet = new Wallet();
            wallet.setBalance(BigDecimal.ZERO);

            User user = User.builder()
                    .googleAccountId(googleId)
                    .email(email)
                    .name(name)
                    .wallet(wallet)
                    .build();

            return userRepository.save(user);
        });

        return gUser;
    }
}
