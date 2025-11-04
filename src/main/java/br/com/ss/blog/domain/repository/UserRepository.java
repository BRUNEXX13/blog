package br.com.ss.blog.domain.repository;

import br.com.ss.blog.domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserDomain, UUID> {


}
