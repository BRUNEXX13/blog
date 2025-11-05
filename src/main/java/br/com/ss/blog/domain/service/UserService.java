package br.com.ss.blog.domain.service;

import br.com.ss.blog.adapters.repository.UserRepository;
import br.com.ss.blog.domain.dto.UserDTO;
import br.com.ss.blog.domain.dto.UserUpdateDTO;
import br.com.ss.blog.domain.entity.UserEntity;
import br.com.ss.blog.domain.exception.EmailAlreadyExistsException;
import br.com.ss.blog.infra.cache.CacheNames;
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
import java.util.function.Consumer;

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
    @CacheEvict(value = CacheNames.USERS, allEntries = true)
    public UserDTO createUser(@Valid @NotNull UserDTO dto) {
     //   Objects.requireNonNull(dto, "UserDTO must not be null");

        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        UserEntity entity = userMapper.toEntity(dto);
        UserEntity saved = userRepository.save(entity);
        log.info("User created successfully with ID {}", saved.getId());
        return userMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = CacheNames.USERS, allEntries = true) // Invalida todo o cache 'users' para garantir consistência
    public UserDTO updateUser(@NotNull UUID id, @Valid @NotNull UserDTO dto) {
        Objects.requireNonNull(dto, "UserDTO must not be null for update");

        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Verifica se o email está sendo alterado e se o novo email já existe para outro usuário
        if (!existingUser.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        // Atualiza os campos do usuário existente com os dados do DTO
        existingUser.setFirstName(dto.firstName());
        existingUser.setLastName(dto.lastName());
        existingUser.setEmail(dto.email());
        existingUser.setBirthDate(dto.birthDate());
        // id e createdAt não são atualizados via DTO

        UserEntity updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with ID {}", updatedUser.getId());
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    @CacheEvict(value = CacheNames.USERS, allEntries = true)
    public UserDTO partialUpdateUser(@NotNull UUID id, @Valid @NotNull UserUpdateDTO dto) {
        Objects.requireNonNull(dto, "UserUpdateDTO must not be null for partial update");

        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        updateIfValid(dto.firstName(), existingUser::setFirstName);
        updateIfValid(dto.lastName(), existingUser::setLastName);
        updateEmailIfValid(dto.email(), existingUser);
        updateIfNotNull(dto.birthDate(), existingUser::setBirthDate);

        UserEntity updatedUser = userRepository.save(existingUser);
        log.info("User partially updated successfully with ID {}", updatedUser.getId());
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    @CacheEvict(value = CacheNames.USERS, allEntries = true)
    public void deleteUser(@NotNull UUID id) {
        if (!userRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent user with ID: {}", id);
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("User with ID {} deleted successfully", id);
    }




    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.USERS, key = "#id") // Usa o nome versionado
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
    @Cacheable(value = CacheNames.USERS, key = "#pageable") // Usa o nome versionado
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
    @Cacheable(value = CacheNames.USERS, key = "#email") // Usa o nome versionado
    public UserDTO findByEmail(@Email @NotNull String email) {
        log.debug("Attempting to find user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UserNotFoundException(email);
                });
    }

    private void updateIfValid(String field, Consumer<String> updater) {
        if (field != null && !field.isBlank()) {
            updater.accept(field);
        }
    }

    private void updateEmailIfValid(String email, UserEntity user) {
        if (email != null && !email.isBlank() && !user.getEmail().equals(email)) {
            if (userRepository.existsByEmail(email)) {
                throw new EmailAlreadyExistsException(email);
            }
            user.setEmail(email);
        }
    }

    private <T> void updateIfNotNull(T value, Consumer<T> updater) {
        if (value != null) {
            updater.accept(value);
        }
    }
}