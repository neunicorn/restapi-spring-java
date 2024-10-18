CREATE DATABASE belajar_spring_restful_api;

show databases;

USE belajar_spring_restful_api;


Create TABLE users (
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    token VARCHAR(255),
    expired_at BIGINT,
    PRIMARY KEY (username),
    UNIQUE (token)
)ENGINE=InnoDB;

DESC users;

CREATE TABLE contacts (
    id VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    phone VARCHAR(100),
    email VARCHAR(100),
    PRIMARY KEY (id),
    FOREIGN KEY fk_user_contacts (username) REFERENCES users (username)
) ENGINE=InnoDB;

DESC contacts;

CREATE TABLE addresses(
    id VARCHAR(100) NOT NULL,
    contact_id VARCHAR(100) NOT NULL,
    street VARCHAR(255),
    city VARCHAR(100),
    province VARCHAR(100),
    country VARCHAR(100)  NOT NULL,
    postal_code VARCHAR(10),
    PRIMARY KEY(id),
    FOREIGN KEY fk_contacts_addresses (contact_id) REFERENCES contacts(id)
)ENGINE=InnoDB;