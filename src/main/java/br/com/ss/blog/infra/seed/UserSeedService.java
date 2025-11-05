//package br.com.ss.blog.infra.seed;
//
//import br.com.ss.blog.adapters.repository.UserRepository;
//import br.com.ss.blog.domain.entity.UserEntity;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Contains the actual seeding logic for creating many UserEntity rows.
// * Active only when the "seed" Spring profile is enabled.
// */
//@Component
//@Profile("seed")
//public class UserSeedService {
//
//    private final UserRepository userRepository;
//
//    @Value("${seed.users.count:50000}")
//    private int defaultTotalUsers;
//
//    @Value("${seed.users.batch-size:1000}")
//    private int defaultBatchSize;
//
//    public UserSeedService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    /**
//     * Seed users. If overrides are null, defaults from configuration are used.
//     * This method is idempotent with respect to the configured target size: if the
//     * current count is already >= target, it will skip.
//     */
//    public void seedUsers(Integer totalUsersOverride, Integer batchSizeOverride) {
//        int totalUsers = totalUsersOverride != null ? totalUsersOverride : defaultTotalUsers;
//        int batchSize = batchSizeOverride != null ? batchSizeOverride : defaultBatchSize;
//
//        long existing = userRepository.count();
//        if (existing >= totalUsers) {
//            System.out.println("[DEBUG_LOG] Skipping seeding: existing users=" + existing + ", target=" + totalUsers);
//            return;
//        }
//
//        System.out.println("[DEBUG_LOG] Seeding users: target=" + totalUsers + ", batchSize=" + batchSize);
//
//        List<UserEntity> buffer = new ArrayList<>(batchSize);
//
//        // Continue from current count to keep emails/phones unique across runs
//        int startIndex = (int) existing;
//        int endIndex = totalUsers;
//
//        for (int i = startIndex; i < endIndex; i++) {
//            String first = "First" + i;
//            String last = "Last" + i;
//            String email = "user" + i + "@example.com"; // unique by construction
//            String phone = String.format("%011d", i); // zero-padded numeric string, unique
//            LocalDate birthDate = LocalDate.of(1970, 1, 1).plusDays(i % 20000);
//
//            buffer.add(new UserEntity(first, last, email, phone email, phone, birthDate));
//
//            if (buffer.size() == batchSize) {
//                userRepository.saveAll(buffer);
//                userRepository.flush();
//                buffer.clear();
//            }
//        }
//
//        if (!buffer.isEmpty()) {
//            userRepository.saveAll(buffer);
//            userRepository.flush();
//            buffer.clear();
//
//        }
//
//        long finalCount = userRepository.count();
//        System.out.println("[DEBUG_LOG] Seeding completed. New users total: " + finalCount);
//
//    }
//}
