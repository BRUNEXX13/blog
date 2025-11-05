package br.com.ss.blog.domain.service;

import br.com.ss.blog.adapters.repository.UserRepository;
import br.com.ss.blog.domain.dto.UserDTO;
import br.com.ss.blog.domain.entity.UserEntity;
import br.com.ss.blog.domain.exception.EmailAlreadyExistsException;
import br.com.ss.blog.domain.exception.UserNotFoundException;
import br.com.ss.blog.domain.mapper.UserMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO createUser(@Valid @NotNull UserDTO dto) {
        Objects.requireNonNull(dto, "UserDTO must not be null");

        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        UserEntity entity = userMapper.toEntity(dto);
        UserEntity saved = userRepository.save(entity);
        log.info("User created successfully with ID {}", saved.getId());
        return userMapper.toDto(saved);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserDTO findById(@NotNull UUID id) {
        log.debug("Attempting to find user by ID: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new UserNotFoundException(id);
                });
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#pageable")
    public Page<UserDTO> findAll(@NotNull Pageable pageable) {
        log.info("Fetching users with pagination: {}", pageable);
        Page<UserEntity> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findByCreatedAt(LocalDate date) {
        Objects.requireNonNull(date, "Date must not be null for searching users by creation date.");

        // Define the start and end of the day in UTC
        Instant startOfDay = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfDay = date.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);

        log.debug("Attempting to find users created between {} and {}", startOfDay, endOfDay);

        List<UserEntity> users = userRepository.findByCreatedAtBetween(startOfDay, endOfDay);
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#email")
    public UserDTO findByEmail(@Email @NotNull String email) {
        log.debug("Attempting to find user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UserNotFoundException(email);
                });
    }
}