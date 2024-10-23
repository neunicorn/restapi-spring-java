package com.nasya.restapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nasya.restapi.entity.Contact;
import com.nasya.restapi.entity.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {

    Optional<Contact> findFirstByUserAndId(User user, String id);
}
