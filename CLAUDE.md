# CLAUDE.md - AI Assistant Guide for Cameras Repository

## Project Overview

This is a **multi-module Maven project** containing two complementary Spring Boot applications for distributed camera streaming and metadata management.

| Module | Version | Purpose |
|--------|---------|---------|
| `cameras` | 1.1.0 | Video streaming service with Basic Auth |
| `cameras_api` | 1.2.0 | RESTful API for camera metadata management |

**Organization:** it.serravalle
**Java Version:** 17
**Spring Boot Version:** 3.1.11
**Build Tool:** Maven 3.8.7 (via Maven Wrapper)

## Repository Structure

```
cameras-repo/
├── cameras/                    # Streaming module
│   ├── pom.xml
│   ├── .mvn/wrapper/
│   └── src/
│       ├── main/java/it/serravalle/cameras/
│       │   ├── CamerasApplication.java      # Entry point with ThreadPoolTaskExecutor
│       │   ├── ServletInitializer.java      # WAR deployment support
│       │   ├── controllers/
│       │   │   ├── MediaController.java     # /streams/* - Video streaming
│       │   │   └── MetadataController.java  # /info/* - Camera metadata proxy
│       │   ├── config/
│       │   │   ├── MediaServer.java         # Media server properties
│       │   │   ├── MediaClient.java         # WebClient for media server
│       │   │   └── MetadataClient.java      # OAuth2 WebClient for API
│       │   └── model/
│       │       └── Camera.java              # Camera POJO
│       └── main/resources/
│           ├── application-placeholder.yml  # Config template
│           └── static/index.html            # Camera viewer UI
│
├── cameras_api/                # API module
│   ├── pom.xml
│   └── src/
│       ├── main/java/it/serravalle/cameras_api/
│       │   ├── CamerasApiApplication.java   # Entry point
│       │   ├── controller/
│       │   │   ├── CameraController.java    # /cameras/* & /oauth2/cameras/*
│       │   │   └── MonitController.java     # /monit health endpoint
│       │   ├── data/
│       │   │   ├── model/                   # JPA entities: Camera, User, Role
│       │   │   └── repository/              # Spring Data JPA repositories
│       │   ├── service/                     # Business logic interfaces & impls
│       │   ├── security/                    # Dual auth configuration
│       │   └── exception/                   # Custom exceptions
│       └── main/resources/
│           ├── application-placeholder.yml  # Config template
│           └── static/index.html
│
└── .gitignore
```

## Build Commands

```bash
# Build all modules
./mvnw clean package

# Build specific module
./mvnw -pl cameras clean package
./mvnw -pl cameras_api clean package

# Run tests
./mvnw test

# Run specific module tests
./mvnw -pl cameras test
./mvnw -pl cameras_api test

# Skip tests during build
./mvnw clean package -DskipTests

# Run application (from module directory)
cd cameras && ../mvnw spring-boot:run
cd cameras_api && ../mvnw spring-boot:run
```

## Architecture

### Cameras Module (Streaming Service)

**Purpose:** Streams video from external media servers and proxies metadata requests.

**Key Endpoints:**
- `GET /streams/{camera}/` - Stream video for camera
- `GET /streams/{camera}/{media:.+}` - Stream specific media path
- `GET /info/{camera}` - Get camera metadata (proxied from cameras_api)
- `GET /info/all` - Get all camera metadata

**Technical Details:**
- Uses WebFlux reactive streaming with `Flux<DataBuffer>`
- OAuth2 client credentials flow for API communication
- Basic auth for media server access
- ThreadPoolTaskExecutor: 4 core, 8 max pool size

### Cameras API Module (Data Management)

**Purpose:** Provides CRUD operations for camera metadata with dual authentication.

**Key Endpoints:**

| Path | Auth | Description |
|------|------|-------------|
| `GET /cameras/{id}` | Basic | Get camera by ID |
| `GET /cameras/all` | Basic | List all cameras |
| `POST /cameras/private/add` | Basic (Admin) | Add camera |
| `PUT /cameras/private/update/{id}` | Basic (Admin) | Update camera |
| `DELETE /cameras/private/delete/{id}` | Basic (Admin) | Delete camera |
| `GET /oauth2/cameras/*` | JWT | Same endpoints via OAuth2 |
| `GET /monit` | Public | Health check |

**Database:** H2 (file-based at `./h2db/cameras_api`)

**Security Configuration:**
- Basic Auth chain for `/cameras/**` (role-based: Camera.User, Camera.Admin)
- OAuth2 JWT chain for `/oauth2/cameras/**` (scope-based)
- BCrypt password encoding
- Stateless session management

## Data Models

### Camera Entity
```java
@Entity
public class Camera {
    @Id String id;           // Primary key
    String tratta;           // Route/section identifier
    String km;               // Kilometer marker
    String direzione;        // Direction
    Double lon, lat;         // GPS coordinates
    String descrizione;      // Description
}
```

### User Entity (cameras_api only)
```java
@Entity
public class User {
    @Id @GeneratedValue Long id;
    @Column(unique=true) String username;
    String password;         // BCrypt encoded
    @ManyToMany(fetch=LAZY) Collection<Role> roles;
}
```

## Code Conventions

### Lombok Usage
- `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` for POJOs
- `@RequiredArgsConstructor` for constructor injection
- `@Slf4j` for logging

### Dependency Injection
- **Prefer constructor injection** over field injection
- Use `@Qualifier` when multiple beans of same type exist

### Service Layer Pattern
```java
// Interface
public interface CameraService {
    Camera findById(String id);
    List<Camera> findAll();
}

// Implementation
@Service
@RequiredArgsConstructor
public class CameraServiceImpl implements CameraService {
    private final CameraRepository cameraRepository;
    // ...
}
```

### Exception Handling
- Custom runtime exceptions: `CameraNotFoundException`, `CameraSavingException`
- Use `ResponseStatusException` for REST error responses
- Always log exceptions before throwing

### Logging
- Use `@Slf4j` annotation (Lombok)
- Log at appropriate levels: DEBUG for flow, INFO for important events, ERROR for exceptions

## Configuration

### Application Properties
Both modules use `application-placeholder.yml` as a template. Key properties:

```yaml
# Azure AD Configuration
spring.security.oauth2.client:
  registration.azure:
    client-id: ${AZURE_CLIENT_ID}
    client-secret: ${AZURE_CLIENT_SECRET}
  provider.azure:
    token-uri: https://login.microsoftonline.com/${TENANT_ID}/oauth2/v2.0/token

# Media Server
media-server:
  hostname: https://ede.serravalle.it
  user: ${MEDIA_USER}
  password: ${MEDIA_PASSWORD}

# Database (cameras_api)
spring.datasource.url: jdbc:h2:file:./h2db/cameras_api
spring.jpa.hibernate.ddl-auto: update
```

### SSL/TLS
- cameras: Port 443
- cameras_api: Port 8443
- Requires keystore configuration

## Testing

**Framework:** JUnit 5 (Jupiter) with Spring Boot Test

**Test Location:**
- `cameras/src/test/java/it/serravalle/cameras/`
- `cameras_api/src/test/java/it/serravalle/cameras_api/`

**Current Tests:**
- Context load tests (`@SpringBootTest`)
- Security testing support available via `spring-security-test`

**Running Tests:**
```bash
./mvnw test                    # All tests
./mvnw -pl cameras test        # cameras module only
./mvnw -pl cameras_api test    # cameras_api module only
```

## External Integrations

### Azure Active Directory
- OAuth2 client credentials flow for service-to-service auth
- JWT validation using Azure's JWKS endpoint
- Tenant-based configuration

### Media Server (ede.serravalle.it)
- Basic authentication
- HTTPS connection
- Video streaming source

## Important Notes for AI Assistants

### When Modifying Code
1. **Maintain Lombok annotations** - Don't expand to verbose getters/setters
2. **Use constructor injection** - Avoid `@Autowired` field injection
3. **Follow existing package structure** - controllers, services, repositories
4. **Add proper exception handling** - Use custom exceptions with logging
5. **Keep reactive patterns** - MediaController uses Flux streaming

### When Adding New Features
1. **Create interface first** for new services
2. **Add to appropriate module** - streaming logic in `cameras`, CRUD in `cameras_api`
3. **Consider security** - Determine if endpoint needs Basic Auth, OAuth2, or public access
4. **Update both auth chains** if adding new secured endpoints

### When Fixing Bugs
1. **Check both modules** - Changes may affect inter-service communication
2. **Verify WebClient configurations** - Connection pooling and timeouts matter
3. **Test with both auth methods** - Basic and OAuth2 may behave differently

### Performance Considerations
- WebClient beans are singletons for connection pooling
- Use `Flux` for streaming, avoid blocking operations
- Entity fetch types: LAZY by default for collections
- Use Stream API over forEach loops

## Git Workflow

```bash
# Create feature branch
git checkout -b feature/description

# Commit with descriptive message
git commit -m "Add/Fix/Update: description of change"

# Push changes
git push -u origin feature/description
```

## Quick Reference

| Task | Command |
|------|---------|
| Build all | `./mvnw clean package` |
| Run tests | `./mvnw test` |
| Run cameras | `cd cameras && ../mvnw spring-boot:run` |
| Run cameras_api | `cd cameras_api && ../mvnw spring-boot:run` |
| Access H2 console | `http://localhost:8443/h2-console` |
| Health check | `GET /monit` |
