# Cameras Repository

A distributed camera streaming and metadata management system built with Spring Boot. This multi-module project provides secure video streaming capabilities and a RESTful API for camera metadata management.

## Features

- **Video Streaming** - Reactive streaming using WebFlux with efficient non-blocking I/O
- **Camera Metadata Management** - Full CRUD operations for camera information
- **Dual Authentication** - Supports both Basic Auth and OAuth2 JWT (Azure AD)
- **Role-Based Access Control** - Admin and User roles with granular permissions
- **GPS Coordinate Support** - Store and retrieve camera location data
- **Health Monitoring** - Built-in health check endpoint

## Architecture

This project consists of two complementary Spring Boot applications:

| Module | Version | Port | Description |
|--------|---------|------|-------------|
| `cameras` | 1.1.0 | 443 | Video streaming service |
| `cameras_api` | 1.2.0 | 8443 | RESTful metadata API |

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│   Client App    │────────▶│    cameras      │────────▶│  Media Server   │
│                 │         │  (streaming)    │         │                 │
└─────────────────┘         └────────┬────────┘         └─────────────────┘
                                     │
                                     │ OAuth2
                                     ▼
                            ┌─────────────────┐         ┌─────────────────┐
                            │  cameras_api    │────────▶│   H2 Database   │
                            │  (metadata)     │         │                 │
                            └─────────────────┘         └─────────────────┘
```

## Prerequisites

- Java 17 or higher
- Maven 3.8.7+ (or use included Maven Wrapper)
- Azure AD tenant (for OAuth2 authentication)
- Access to media server (for streaming functionality)

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/ninius82/cameras-repo.git
cd cameras-repo
```

### Configuration

Both modules require configuration before running. Copy the placeholder files and update with your values:

**cameras module:**
```bash
cp cameras/src/main/resources/application-placeholder.yml \
   cameras/src/main/resources/application.yml
```

**cameras_api module:**
```bash
cp cameras_api/src/main/resources/application-placeholder.yml \
   cameras_api/src/main/resources/application.yml
```

#### Required Configuration Properties

```yaml
# Azure AD Configuration
spring:
  security:
    oauth2:
      client:
        registration:
          azure:
            client-id: <your-client-id>
            client-secret: <your-client-secret>
        provider:
          azure:
            token-uri: https://login.microsoftonline.com/<tenant-id>/oauth2/v2.0/token

# Media Server (cameras module)
media-server:
  hostname: https://your-media-server.com
  user: <media-user>
  password: <media-password>

# SSL Configuration
server:
  ssl:
    key-store: <path-to-keystore>
    key-store-password: <keystore-password>
```

### Build

```bash
# Build all modules
./mvnw clean package

# Build without tests
./mvnw clean package -DskipTests
```

### Run

```bash
# Run cameras_api (start this first)
cd cameras_api && ../mvnw spring-boot:run

# Run cameras (in a separate terminal)
cd cameras && ../mvnw spring-boot:run
```

## API Reference

### Cameras API Endpoints

#### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/monit` | Health check |

#### Basic Auth Endpoints (`/cameras/*`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/cameras/{id}` | User | Get camera by ID |
| GET | `/cameras/all` | User | List all cameras |
| POST | `/cameras/private/add` | Admin | Create new camera |
| PUT | `/cameras/private/update/{id}` | Admin | Update camera |
| DELETE | `/cameras/private/delete/{id}` | Admin | Delete camera |

#### OAuth2 Endpoints (`/oauth2/cameras/*`)

Same endpoints as above, accessible via JWT token with appropriate scopes (`Camera.User`, `Camera.Admin`).

### Cameras Streaming Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/streams/{camera}/` | Stream video for camera |
| GET | `/streams/{camera}/{media}` | Stream specific media path |
| GET | `/info/{camera}` | Get camera metadata |
| GET | `/info/all` | Get all camera metadata |

### Camera Object Schema

```json
{
  "id": "CAM001",
  "tratta": "A4-Milano-Torino",
  "km": "125",
  "direzione": "Nord",
  "lon": 9.1234,
  "lat": 45.5678,
  "descrizione": "Svincolo Milano Est"
}
```

## Development

### Project Structure

```
cameras-repo/
├── cameras/                    # Streaming module
│   ├── src/main/java/it/serravalle/cameras/
│   │   ├── controllers/        # REST controllers
│   │   ├── config/             # WebClient configurations
│   │   └── model/              # Data models
│   └── src/test/
│
├── cameras_api/                # API module
│   ├── src/main/java/it/serravalle/cameras_api/
│   │   ├── controller/         # REST controllers
│   │   ├── data/
│   │   │   ├── model/          # JPA entities
│   │   │   └── repository/     # Spring Data repositories
│   │   ├── service/            # Business logic
│   │   ├── security/           # Auth configuration
│   │   └── exception/          # Custom exceptions
│   └── src/test/
│
└── CLAUDE.md                   # AI assistant guide
```

### Running Tests

```bash
# Run all tests
./mvnw test

# Run tests for specific module
./mvnw -pl cameras test
./mvnw -pl cameras_api test
```

### Database Console

When running in development mode, the H2 console is available at:
```
http://localhost:8443/h2-console
```

Connection settings:
- JDBC URL: `jdbc:h2:file:./h2db/cameras_api`
- Username: `sa`
- Password: (check your configuration)

### Code Style

- **Lombok** - Used extensively for boilerplate reduction
- **Constructor Injection** - Preferred over field injection
- **Interface-first Services** - All services have interfaces
- **Custom Exceptions** - `CameraNotFoundException`, `CameraSavingException`

## Tech Stack

- **Framework:** Spring Boot 3.1.11
- **Language:** Java 17
- **Build:** Maven 3.8.7
- **Database:** H2 (file-based)
- **Security:** Spring Security with OAuth2
- **Reactive:** Spring WebFlux
- **ORM:** Spring Data JPA / Hibernate

## License

Copyright (c) Serravalle. All rights reserved.
