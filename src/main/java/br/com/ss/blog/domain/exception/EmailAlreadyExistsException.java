package br.com.ss.blog.domain.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("A user with email '" + email + "' already exists.");
    }
}
