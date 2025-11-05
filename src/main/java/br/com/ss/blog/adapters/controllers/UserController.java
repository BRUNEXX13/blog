package br.com.ss.blog.adapters.controllers;

import br.com.ss.blog.domain.dto.UserDTO;
import br.com.ss.blog.domain.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable UUID id) {
        UserDTO user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/created-at")
    public ResponseEntity<List<UserDTO>> findByCreatedAt(@RequestParam("date") @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<UserDTO> users = userService.findByCreatedAt(date);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/find-email")
    public ResponseEntity<UserDTO> findByEmail(@RequestParam("email") @Email String email) {
        UserDTO user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }
}
