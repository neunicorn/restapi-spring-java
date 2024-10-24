package com.nasya.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nasya.restapi.entity.User;
import com.nasya.restapi.model.AddressResponse;
import com.nasya.restapi.model.CreateAddressRequest;
import com.nasya.restapi.model.WebResponse;
import com.nasya.restapi.service.AddressService;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping(path = "/api/contacts/{contactId}/addresses", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> cerate(User user,
            @RequestBody CreateAddressRequest request,
            @PathVariable("contactId") String contactId) {

        request.setId(contactId);

        AddressResponse response = addressService.create(user, request);

        return WebResponse.<AddressResponse>builder().data(response).build();
    }

}
