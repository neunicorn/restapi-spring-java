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
import com.nasya.restapi.model.RegisterUserRequest;
import com.nasya.restapi.model.TokenResponse;
import com.nasya.restapi.model.UserResponse;
import com.nasya.restapi.model.WebResponse;
import com.nasya.restapi.repository.UserRepository;
import com.nasya.restapi.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hibernate.jdbc.Expectations;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

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
        void testRegisterSuccess() throws Exception {
                RegisterUserRequest req = new RegisterUserRequest();
                req.setUsername("Baehaq12");
                req.setPassword("janganjajanmelulu");
                req.setName("Muhamad Zulfan Taqiyudin Baehaki");

                mockMvc.perform(
                                post("/api/users")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(req)))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });

                                        assertEquals("OK", response.getData());
                                });
        }

        @Test
        void testRegisterBadRequest() throws Exception {
                RegisterUserRequest req = new RegisterUserRequest();
                req.setUsername("");
                req.setPassword("");
                req.setName("");

                mockMvc.perform(
                                post("/api/users")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(req)))
                                .andExpectAll(
                                                status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });

                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void testRegisterDuplicate() throws Exception {

                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Testing");
                userRepository.save(user);

                RegisterUserRequest req = new RegisterUserRequest();
                req.setUsername("test");
                req.setPassword("test");
                req.setName("Testing");

                mockMvc.perform(
                                post("/api/users")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(req)))
                                .andExpectAll(
                                                status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });

                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void getUserUnAuthorized() throws Exception {
                mockMvc.perform(
                                get("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "notfound"))
                                .andExpectAll(status().isUnauthorized())
                                .andDo(result -> {

                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });

                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void getUserUnAuthorizedTokenNotSent() throws Exception {
                mockMvc.perform(
                                get("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpectAll(status().isUnauthorized())
                                .andDo(result -> {

                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });

                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void getUserSuccess() throws Exception {
                User user = new User();
                user.setName("Dapa");
                user.setUsername("Dapa42");
                user.setPassword("Testing");
                user.setToken("kadjkn4n3998nbaadf");
                user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);

                userRepository.save(user);

                mockMvc.perform(
                                get("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "kadjkn4n3998nbaadf"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {

                                        WebResponse<UserResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<UserResponse>>() {
                                                        });

                                        assertNull(response.getErrors());
                                        assertEquals("Dapa42", response.getData().getUsername());
                                        assertEquals("Dapa", response.getData().getName());
                                });
        }

        @Test
        void getUserFailedTokenExpired() throws Exception {
                User user = new User();
                user.setName("Dapa");
                user.setUsername("Dapa42");
                user.setPassword("Testing");
                user.setToken("kadjkn4n3998nbaadf");
                user.setTokenExpiredAt(System.currentTimeMillis() - 1000000000L);

                userRepository.save(user);

                mockMvc.perform(
                                get("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "kadjkn4n3998nbaadf"))
                                .andExpectAll(status().isUnauthorized())
                                .andDo(result -> {

                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });

                                        assertNotNull(response.getErrors());
                                });
        }
}
