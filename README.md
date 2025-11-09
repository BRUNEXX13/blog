# Blog (Spring Boot)

Small blog API built with Java \& Spring Boot.

## Features
- REST API for posts and users
- JWT-based authentication
- Default user role: `ROLE.USER` for newly created accounts
- Profiles: `application.properties`, `application-prod.properties`, `application-seed.properties`
- Docker support via `docker-compose.yml`

## Prerequisites
- Java 17+ (or the version declared in `pom.xml`)
- Maven (or use the included `./mvnw`)
- Docker (optional)

## Build & Run

Build the artifact:
```bash
./mvnw clean package
# artifact: target/blog-0.0.1-SNAPSHOT.war
