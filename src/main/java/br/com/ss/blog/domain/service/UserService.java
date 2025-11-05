package br.com.ss.blog.domain.service;

import br.com.ss.blog.adapters.repository.UserRepository;
import br.com.ss.blog.domain.dto.UserDTO;
import br.com.ss.blog.domain.exception.EmailAlreadyExistsException;
import br.com.ss.blog.domain.entity.UserEntity;
import br.com.ss.blog.domain.exception.UserNotFoundException;
import br.com.ss.blog.domain.mapper.UserMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public UserDTO createUser(@Valid @NotNull UserDTO dto) throws EmailAlreadyExistsException {
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
    public UserDTO findById(UUID id) {
        log.debug("Attempting to find user by ID: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new UserNotFoundException(id);
                });
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        log.info("Fetching all users");
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

}