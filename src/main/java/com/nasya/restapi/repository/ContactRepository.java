package com.nasya.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nasya.restapi.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {

}
