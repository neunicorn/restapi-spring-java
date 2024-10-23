package com.nasya.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.RestController;

import com.nasya.restapi.entity.User;
import com.nasya.restapi.model.ContactResponse;
import com.nasya.restapi.model.CreateContactRequest;
import com.nasya.restapi.model.WebResponse;
import com.nasya.restapi.service.ContactService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping(path = "/api/contacts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request) {
        ContactResponse res = contactService.create(user, request);

        return WebResponse.<ContactResponse>builder().data(res).build();
    }

}
