//package br.com.ss.blog.infra.seed;
//
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//import org.springframework.boot.CommandLineRunner;
//
///**
// * Seeds the database with a large number of users for local/dev testing.
// *
// * This runner is only active when the "seed" Spring profile is enabled.
// * Example:
// *   mvn spring-boot:run -Dspring-boot.run.profiles=seed
// * or
// *   java -jar app.jar --spring.profiles.active=seed
// */
//@Component
//@Profile("seed")
//public class UserSeeder implements CommandLineRunner {
//
//    private final UserSeedService userSeedService;
//
//    public UserSeeder(UserSeedService userSeedService) {
//        this.userSeedService = userSeedService;
//    }
//
//    @Override
//    public void run(String... args) {
//        // Delegate to the service which contains the actual seeding logic
//        this.userSeedService.seedUsers(null, null);
//    }
//}
