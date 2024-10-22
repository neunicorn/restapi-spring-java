package com.nasya.restapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nasya.restapi.entity.User;
import com.nasya.restapi.model.LoginUserRequest;
import com.nasya.restapi.model.TokenResponse;
import com.nasya.restapi.model.WebResponse;
import com.nasya.restapi.repository.UserRepository;
import com.nasya.restapi.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testLoginFail() throws Exception {
        LoginUserRequest req = new LoginUserRequest();
        req.setUsername("test");
        req.setPassword("testing");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpectAll(status().isUnauthorized()).andDo(result -> {

                    WebResponse<String> res = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    assertNotNull(res.getErrors());
                });
    }

    @Test
    void testLoginFailWrongPassword() throws Exception {

        // create new user direct to database (without using API)
        User user = new User();
        user.setUsername("Baehaq12");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Baehaki");

        userRepository.save(user);

        LoginUserRequest req = new LoginUserRequest();
        req.setUsername("Baehq12");
        req.setPassword("testing");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpectAll(status().isUnauthorized()).andDo(result -> {

                    WebResponse<String> res = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    assertNotNull(res.getErrors());
                });
    }

    @Test
    void testLoginSuccess() throws Exception {

        // create new user direct to database (without using API)
        User user = new User();
        user.setUsername("Baehq12");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Baehaki");

        userRepository.save(user);

        LoginUserRequest req = new LoginUserRequest();
        req.setUsername("Baehq12");
        req.setPassword("rahasia");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpectAll(status().isOk()).andDo(result -> {

                    WebResponse<TokenResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    assertNull(res.getErrors());
                    assertNotNull(res.getData().getToken());
                    assertNotNull(res.getData().getExpiredAt());

                    // get data from DB
                    User userDb = userRepository.findById("Baehq12").orElse(null);
                    assertNotNull(userDb);
                    assertEquals(userDb.getToken(), res.getData().getToken());
                    assertEquals(userDb.getTokenExpiredAt(), res.getData().getExpiredAt());
                });
    }
}
