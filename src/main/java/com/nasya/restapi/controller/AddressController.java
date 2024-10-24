package com.nasya.restapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nasya.restapi.entity.User;
import com.nasya.restapi.model.AddressResponse;
import com.nasya.restapi.model.CreateAddressRequest;
import com.nasya.restapi.model.UpdateAddressRequest;
import com.nasya.restapi.model.WebResponse;
import com.nasya.restapi.service.AddressService;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping(path = "/api/contacts/{contactId}/addresses", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> create(User user,
            @RequestBody CreateAddressRequest request,
            @PathVariable("contactId") String contactId) {

        request.setId(contactId);

        AddressResponse response = addressService.create(user, request);

        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @GetMapping(path = "/api/contacts/{contactId}/addresses/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> get(User user, @PathVariable("contact Id") String contactId,
            @PathVariable("addressId") String addrerssId) {

        AddressResponse res = addressService.get(user, contactId, addrerssId);

        return WebResponse.<AddressResponse>builder().data(res).build();
    }

    @PutMapping(path = "/api/contacts/{contactId}/addresses/{addressId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> create(User user,
            @RequestBody UpdateAddressRequest request,
            @PathVariable("contactId") String contactId,
            @PathVariable("addressId") String addressId) {

        request.setContactId(contactId);
        request.setAddressId(addressId);

        AddressResponse response = addressService.update(user, request);

        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @DeleteMapping(path = "/api/contacts/{contactId}/addresses/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> delete(User user, @PathVariable("contactId") String contactId,
            @PathVariable("addressId") String addressId) {

        addressService.delete(user, contactId, addressId);
        return WebResponse.<String>builder().data("address deleted").build();
    }

    @GetMapping(path = "/api/contacts/{contactId}/addresses", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<AddressResponse>> get(User user, @PathVariable("contact Id") String contactId) {

        List<AddressResponse> res = addressService.list(user, contactId);

        return WebResponse.<List<AddressResponse>>builder().data(res).build();
    }
}
