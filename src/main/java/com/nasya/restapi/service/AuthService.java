package com.nasya.restapi.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nasya.restapi.entity.User;
import com.nasya.restapi.model.LoginUserRequest;
import com.nasya.restapi.model.TokenResponse;
import com.nasya.restapi.repository.UserRepository;
import com.nasya.restapi.security.BCrypt;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    private Long next30Days() {
        return System.currentTimeMillis() + 1000 * 60 * 24 * 30;
    }

    @Transactional
    public TokenResponse login(LoginUserRequest req) {

        validationService.validate(req);

        User user = userRepository.findById(req.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Passwordr wrong"));

        if (BCrypt.checkpw(req.getPassword(), user.getPassword())) {
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            return TokenResponse.builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

    }

    @Transactional
    public void logout(User user) {
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }
}
