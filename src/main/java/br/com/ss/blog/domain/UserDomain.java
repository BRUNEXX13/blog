package br.com.ss.blog.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Table
@Entity(name = "users")
public class UserDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public UserDomain(UUID id, String firstName, String lastName, String email, Instant createdAt, LocalDate birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.createdAt = createdAt;
        this.birthDate = birthDate;
    }

    public UserDomain() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }
}
