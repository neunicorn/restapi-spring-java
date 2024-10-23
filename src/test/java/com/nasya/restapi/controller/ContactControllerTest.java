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
import com.nasya.restapi.model.ContactResponse;
import com.nasya.restapi.model.CreateContactRequest;
import com.nasya.restapi.model.WebResponse;
import com.nasya.restapi.repository.ContactRepository;
import com.nasya.restapi.repository.UserRepository;
import com.nasya.restapi.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    @Test
    void setUp() {

        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setName("Testing");
        user.setPassword(BCrypt.hashpw("Testing", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
        userRepository.save(user);
    }

    @Test
    void testCreateContactBadRequest() throws Exception {
        CreateContactRequest req = new CreateContactRequest();
        req.setFirstName("");
        req.setEmail("wrong format");

        mockMvc.perform(post("/api/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .header("X-API-TOKEN", "test")).andExpectAll(status().isBadRequest()).andDo(result -> {
                    WebResponse<String> res = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            });

                    assertNotNull(res.getErrors());

                });

    }

    @Test
    void testCreateContactUnAuthorized() throws Exception {
        CreateContactRequest req = new CreateContactRequest();
        req.setFirstName("");
        req.setEmail("wrong format");

        mockMvc.perform(post("/api/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .header("X-API-TOKEN", "wrongToken")).andExpectAll(status().isUnauthorized()).andDo(result -> {
                    WebResponse<String> res = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            });

                    assertNotNull(res.getErrors());

                });

    }

    @Test
    void createContactSuccess() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("Eko");
        request.setLastName("Khannedy");
        request.setEmail("eko@example.com");
        request.setPhone("42342342344");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test"))
                .andExpectAll(
                        status().isOk())
                .andDo(result -> {
                    WebResponse<ContactResponse> response = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });
                    assertNull(response.getErrors());
                    assertEquals("Eko", response.getData().getFirstName());
                    assertEquals("Khannedy", response.getData().getLastName());
                    assertEquals("eko@example.com", response.getData().getEmail());
                    assertEquals("42342342344", response.getData().getPhone());

                    assertTrue(contactRepository.existsById(response.getData().getId()));
                });
    }
}
