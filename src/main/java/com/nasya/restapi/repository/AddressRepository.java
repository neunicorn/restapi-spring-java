package com.nasya.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nasya.restapi.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

}
