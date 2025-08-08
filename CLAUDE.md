# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot video processing application that provides video generation, board management, and export functionality. The project uses Hexagonal Architecture patterns with clear separation between controllers, services, and infrastructure layers.

## Development Commands

### Build and Run
- `./gradlew bootRun` - Run the application locally (starts on port 8090)
- `./gradlew build` - Build the application
- `./gradlew test` - Run tests
- `./gradlew clean` - Clean build artifacts
- `./gradlew bootJar` - Create executable JAR
- `./gradlew bootBuildImage` - Build OCI container image

### Testing
- `./gradlew test` - Run all tests using JUnit Platform
- Individual test files are located in `src/test/java/`

## Architecture

### Package Structure
```
app.video.download/
├── DownloadApplication.java          # Main Spring Boot application
├── boardExport/                      # Board export domain
│   ├── controller/                   # REST endpoints and DTOs
│   ├── domain/                       # Core business entities
│   ├── infrastructure/               # JPA repositories and entities
│   └── service/                      # Business logic implementation
├── config/                           # Configuration classes
│   ├── security/                     # OAuth2 and security configuration
│   ├── ClientConfig.java
│   ├── CorsConfig.java
│   ├── RabbitMQConfig.java
│   └── R2Config.java
├── global/                           # Shared domain and infrastructure
│   ├── controller/                   # Global REST endpoints
│   ├── domain/                       # Shared entities (ResolutionProfile, Status)
│   ├── dto/                         # Global response objects
│   ├── error/                       # Error handling
│   ├── infrastructure/              # Shared services (S3, Redis, Video processing)
│   └── port/                        # Interface definitions for services
└── lastFrameVideo/                   # Video processing queues and consumers
```

### Key Architectural Patterns
- **Hexagonal Architecture**: Clear separation between domain, application, and infrastructure layers
- **Port-Adapter Pattern**: Interfaces in `port/` packages, implementations in `infrastructure/` or `service/`
- **Domain-Driven Design**: Each feature has its own domain package with entities and business logic

### Core Technologies
- **Spring Boot 3.5.4** with Java 21
- **Spring Security** with OAuth2 (Google authentication)
- **MySQL** database with JPA/Hibernate
- **RabbitMQ** for message queuing
- **Redis** for caching
- **AWS S3** for file storage
- **Prometheus** metrics enabled

### Key Features
1. **Board Export**: Video concatenation and export functionality
2. **Resolution Management**: Multiple video resolution profiles (SD/HD, various aspect ratios)
3. **Video Processing**: Integration with external video processing services
4. **Authentication**: OAuth2 with Google
5. **Message Queuing**: RabbitMQ for video processing workflows

### Configuration
- Main config: `src/main/resources/application.yml`
- Secret config: `src/main/resources/application-secret.yml` (imported automatically)
- Production config: `src/main/resources/application-prod.yml`

### Important Implementation Notes
- The application runs on port 8090 by default
- Database connection is configured for AWS RDS MySQL
- File upload limits: 10MB per file, 20MB per request
- External API integration with ModelsLab for AI services
- Webhook integration support for external services