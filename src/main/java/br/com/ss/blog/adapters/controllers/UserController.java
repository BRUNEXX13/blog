package br.com.ss.blog.adapters.controllers;

import br.com.ss.blog.domain.dto.UserDTO;
import br.com.ss.blog.domain.dto.UserUpdateDTO;
import br.com.ss.blog.infra.pageable.PaginatedResponse;
import br.com.ss.blog.domain.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> partialUpdateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO updatedUser = userService.partialUpdateUser(id, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable UUID id) {
        UserDTO user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<UserDTO>> findAll(Pageable pageable) {
        Page<UserDTO> usersPage = userService.findAll(pageable);
        PaginatedResponse<UserDTO> response = new PaginatedResponse<>(
                usersPage.getContent(),
                usersPage.getNumber(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/created-at")
    public ResponseEntity<List<UserDTO>> findByCreatedAt(@RequestParam("date") @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<UserDTO> users = userService.findByCreatedAt(date);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<UserDTO> findByEmail(@RequestParam(name = "email") @Email String email) {
        UserDTO user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }


    @GetMapping("/search/phone")
    public ResponseEntity<UserDTO> findByPhone(@RequestParam(name = "phone") String phone) {
        UserDTO user = userService.findByPhone(phone);
        return ResponseEntity.ok(user);
    }

}
