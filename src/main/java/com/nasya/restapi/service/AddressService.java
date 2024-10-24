package com.nasya.restapi.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nasya.restapi.entity.Address;
import com.nasya.restapi.entity.Contact;
import com.nasya.restapi.entity.User;
import com.nasya.restapi.model.AddressResponse;
import com.nasya.restapi.model.CreateAddressRequest;
import com.nasya.restapi.model.UpdateAddressRequest;
import com.nasya.restapi.repository.AddressRepository;
import com.nasya.restapi.repository.ContactRepository;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;

    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }

    @Transactional
    public AddressResponse create(User user, CreateAddressRequest request) {

        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CONTACT NOT FOUND"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setCountry(request.getCountry());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setStreet(request.getStreet());
        address.setPostalCode(request.getPostalCode());

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    @Transactional(readOnly = true)
    public AddressResponse get(User user, String contactId, String addressId) {

        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CONTACT NOT FOUND"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ADDRESS NOT FOUND"));
        return toAddressResponse(address);
    }

    @Transactional
    public AddressResponse update(User user, UpdateAddressRequest req) {

        validationService.validate(req);

        Contact contact = contactRepository.findFirstByUserAndId(user, req.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CONTACT NOT FOUND"));

        Address address = addressRepository.findFirstByContactAndId(contact, req.getAddressId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ADDRESS NOT FOUND"));

        address.setStreet(req.getStreet());
        address.setPostalCode(req.getPostalCode());
        address.setCity(req.getCity());
        address.setProvince(req.getProvince());
        address.setCountry(req.getCountry());

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    @Transactional
    public void delete(User user, String contactId, String addressId) {

        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CONTACT_NOT_FOUND"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ADDRESS_NOT_FOUND"));

        addressRepository.delete(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> list(User user, String contactId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CONTACT_NOT_FOUND"));

        List<Address> addresses = addressRepository.findAllByContact(contact);

        return addresses.stream().map(address -> toAddressResponse(address)).toList();
    }
}
