package com.nasya.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nasya.restapi.model.LoginUserRequest;
import com.nasya.restapi.model.TokenResponse;
import com.nasya.restapi.model.WebResponse;
import com.nasya.restapi.service.AuthService;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(path = "/api/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest req) {
        TokenResponse tokenResponse = authService.login(req);
        return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
    }

}
