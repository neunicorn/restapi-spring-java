package com.nasya.restapi.service;

import java.util.Objects;
import java.util.Set;

import org.aspectj.weaver.bcel.BcelGenericSignatureToTypeXConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nasya.restapi.entity.User;
import com.nasya.restapi.model.RegisterUserRequest;
import com.nasya.restapi.model.UpdateUserRequest;
import com.nasya.restapi.model.UserResponse;
import com.nasya.restapi.repository.UserRepository;
import com.nasya.restapi.security.BCrypt;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void register(RegisterUserRequest request) {

        validationService.validate(request);

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Created");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());

        userRepository.save(user);

    }

    public UserResponse get(User user) {

        return UserResponse.builder().username(user.getUsername()).name(user.getName()).build();
    }

    @Transactional
    public UserResponse update(User user, UpdateUserRequest req) {
        validationService.validate(req);

        if (Objects.nonNull(req.getName())) {
            user.setName(req.getName());
        }

        if (Objects.nonNull(req.getPassword())) {
            user.setPassword(BCrypt.hashpw(req.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);

        return UserResponse.builder().name(user.getName()).username(user.getUsername()).build();
    }

}
