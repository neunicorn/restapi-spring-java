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
import com.nasya.restapi.entity.Contact;
import com.nasya.restapi.entity.User;
import com.nasya.restapi.model.AddressResponse;
import com.nasya.restapi.model.CreateAddressRequest;
import com.nasya.restapi.model.WebResponse;
import com.nasya.restapi.repository.AddressRepository;
import com.nasya.restapi.repository.ContactRepository;
import com.nasya.restapi.repository.UserRepository;
import com.nasya.restapi.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    @BeforeEach
    void setUp() {

        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setName("Testing");
        user.setPassword(BCrypt.hashpw("Testing", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setId("test");
        contact.setFirstName("testFirstName");
        contact.setLastName("testLastName");
        contact.setEmail("test@example.com");
        contact.setPhone("09864388076");
        contactRepository.save(contact);

    }

    @Test
    void testCerateAddressSuccess() throws Exception {
        CreateAddressRequest req = new CreateAddressRequest();
        req.setCountry("Indonesia");
        req.setProvince("Jawa Barat");
        req.setCity("Bogor");
        req.setPostalCode("16161");
        req.setStreet("Jalan Ahmad Yani");

        mockMvc.perform(post("/api/contacts/test/addresses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
                .content(objectMapper.writeValueAsString(req))).andExpectAll(status().isOk()).andDo(result -> {
                    WebResponse<AddressResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });

                    assertNull(response.getErrors());
                    assertEquals(req.getCountry(), response.getData().getCountry());
                    assertEquals(req.getCity(), response.getData().getCity());
                    assertEquals(req.getStreet(), response.getData().getStreet());
                    assertEquals(req.getPostalCode(), response.getData().getPostalCode());

                    assertTrue(addressRepository.existsById(response.getData().getId()));

                });
    }

    @Test
    void testCreateAddressBadRequest() throws Exception {

        CreateAddressRequest req = new CreateAddressRequest();
        req.setCountry("");
        req.setProvince("Jawa Barat");

        mockMvc.perform(post("/api/contacts/test/addresses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
                .content(objectMapper.writeValueAsString(req))).andExpectAll(status().isBadRequest()).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });

                    assertNotNull(response.getErrors());
                });
    }
}
