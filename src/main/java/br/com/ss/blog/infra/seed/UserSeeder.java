package br.com.ss.blog.infra.seed;

import br.com.ss.blog.adapters.repository.UserRepository;
import br.com.ss.blog.domain.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the database with a large number of users for local/dev testing.
 *
 * This runner is only active when the "seed" Spring profile is enabled.
 * Example:
 *   mvn spring-boot:run -Dspring-boot.run.profiles=seed
 * or
 *   java -jar app.jar --spring.profiles.active=seed
 */
@Component
@Profile("seed")
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Value("${seed.users.count:50000}")
    private int totalUsers;

    @Value("${seed.users.batch-size:1000}")
    private int batchSize;

    public UserSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // If there are already >= totalUsers in DB, skip to avoid duplicates on re-run
        long existing = userRepository.count();
        if (existing >= totalUsers) {
            System.out.println("[DEBUG_LOG] Skipping seeding: existing users=" + existing + ", target=" + totalUsers);
            return;
        }

        System.out.println("[DEBUG_LOG] Seeding users: target=" + totalUsers + ", batchSize=" + batchSize);

        List<UserEntity> buffer = new ArrayList<>(batchSize);

        // Generate deterministic unique data to satisfy unique constraints on email
        int startIndex = (int) existing; // continue from current count to keep emails unique on repeated runs
        int endIndex = totalUsers;

        for (int i = startIndex; i < endIndex; i++) {
            String first = "First" + i;
            String last = "Last" + i;
            String email = "user" + i + "@example.com"; // unique by construction
            String phone = String.format("%011d", i); // zero-padded numeric string, unique
            LocalDate birthDate = LocalDate.of(1970, 1, 1).plusDays(i % 20000);

            buffer.add(new UserEntity(first, last, email, phone, birthDate));

            if (buffer.size() == batchSize) {
                userRepository.saveAll(buffer);
                userRepository.flush();
                buffer.clear();
            }
        }

        if (!buffer.isEmpty()) {
            userRepository.saveAll(buffer);
            userRepository.flush();
            buffer.clear();
        }

        long finalCount = userRepository.count();
        System.out.println("[DEBUG_LOG] Seeding completed. New users total: " + finalCount);
    }
}
