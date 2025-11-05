package br.com.ss.blog.domain.exception;

import java.util.UUID;

public class UserPhoneNotFoundException extends RuntimeException {

    public UserPhoneNotFoundException(UUID id) {
        super("User with ID " + id + " not found.");
    }

    public UserPhoneNotFoundException(String phone) {
        super("User with phone '" + phone  + "' not found.");
    }
}