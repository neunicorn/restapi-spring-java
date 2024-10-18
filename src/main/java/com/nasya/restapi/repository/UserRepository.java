package com.nasya.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nasya.restapi.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
