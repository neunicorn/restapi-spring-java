package com.nasya.restapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.RestController;

import com.nasya.restapi.entity.User;
import com.nasya.restapi.model.ContactResponse;
import com.nasya.restapi.model.CreateContactRequest;
import com.nasya.restapi.model.PagingResponse;
import com.nasya.restapi.model.SearchContactRequest;
import com.nasya.restapi.model.UpdateContactRequest;
import com.nasya.restapi.model.WebResponse;
import com.nasya.restapi.service.ContactService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping(path = "/api/contacts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request) {
        ContactResponse res = contactService.create(user, request);

        return WebResponse.<ContactResponse>builder().data(res).build();
    }

    @GetMapping(path = "/api/contacts/{contactId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> get(User user, @PathVariable("contactId") String contactId) {
        ContactResponse response = contactService.get(user, contactId);

        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @PutMapping(path = "/api/contacts/{contactId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> update(User user, @RequestBody UpdateContactRequest request,
            @PathVariable("contactId") String contactId) {
        request.setId(contactId);

        ContactResponse res = contactService.update(user, request);

        return WebResponse.<ContactResponse>builder().data(res).build();
    }

    @DeleteMapping(path = "/api/contacts/{contactId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> delete(User user, @PathVariable("contactId") String contactId) {
        contactService.delete(user, contactId);

        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(path = "/api/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<ContactResponse>> search(User user,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        SearchContactRequest request = SearchContactRequest.builder()
                .page(page)
                .size(size)
                .name(name)
                .email(email)
                .phone(phone)
                .build();

        Page<ContactResponse> contactResponses = contactService.search(user, request);
        return WebResponse.<List<ContactResponse>>builder()
                .data(contactResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(contactResponses.getNumber())
                        .totalPage(contactResponses.getTotalPages())
                        .size(contactResponses.getSize())
                        .build())
                .build();
    }

}
