package com.nasya.restapi.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nasya.restapi.entity.Contact;
import com.nasya.restapi.entity.User;
import com.nasya.restapi.model.ContactResponse;
import com.nasya.restapi.model.CreateContactRequest;
import com.nasya.restapi.repository.ContactRepository;

import jakarta.transaction.Transactional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public ContactResponse create(User user, CreateContactRequest request) {
        validationService.validate(request);

        // check
        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setUser(user);

        contactRepository.save(contact);

        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();

    }

}
