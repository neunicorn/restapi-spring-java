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
import static org.mockito.ArgumentMatchers.argThat;
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

        /***
         * Setup repository
         */
        @Test
        @BeforeEach
        void setUp() {
                userRepository.deleteAll();
        }

        /***
         * Testing for Login End Point
         * 
         * @throws Exception
         */
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

                                        WebResponse<String> res = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
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

                                        WebResponse<String> res = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
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

                                        WebResponse<TokenResponse> res = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
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

        /***
         * Testing for Logout End Point
         * 
         * @throws Exception
         */
        @Test
        void testLogoutFailed() throws Exception {
                mockMvc.perform(
                                delete("/api/auth/logout")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpectAll(
                                                status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<String> rersponse = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });
                                        assertNotNull(rersponse.getErrors());
                                });
        }

        @Test
        void testLogoutSuccess() throws Exception {
                User user = new User();
                user.setName("Test");
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("Testing", BCrypt.gensalt()));
                user.setToken("Test");
                user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);

                userRepository.save(user);

                mockMvc.perform(
                                delete("/api/auth/logout")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "Test"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<String> rersponse = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });
                                        assertNull(rersponse.getErrors());
                                        assertEquals("OK", rersponse.getData());

                                        User userDb = userRepository.findById("test").orElse(null);
                                        assertNotNull(userDb);
                                        assertNull(userDb.getToken());
                                        assertNull(userDb.getTokenExpiredAt());
                                });
        }
}
