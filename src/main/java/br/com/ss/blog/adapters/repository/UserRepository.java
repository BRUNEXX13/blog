package br.com.ss.blog.adapters.repository;

import br.com.ss.blog.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByEmail(String email);

    List<UserEntity> findByCreatedAtBetween(Instant start, Instant end);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findFirstByPhone(String phone);

    Optional<UserEntity> findById(UUID id);
}
