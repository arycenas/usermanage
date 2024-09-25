# Global Loyalty Indonesia Academy Spring Boot Microservices Project

## Table of Contents

- [Introduction](#introduction)
- [Architecture Overview](#architecture-overview)
- [Services](#services)
  - [User Management Service](#user-management-service)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
  - [Endpoints](#endpoints)
    - [Register a New User](#register-a-new-user)
    - [User Login](#user-login)
    - [Refresh JWT Token](#refresh-jwt-token)
    - [Validate Token](#validate-token)

## Introduction

This project is composed of two microservices:

1. **User Management Service**: Handles user authentication and authorization using Redis for session management and JWT tokens.

## Architecture Overview

- **Microservices**: Two independent services communicating via REST API.
- **Authentication & Authorization**: JWT-based token authentication managed by Redis.
- **Persistence**: User sessions are stored in Redis, and asteroid data is stored in PostgreSQL.
- **External API Integration**: Asteroid data is fetched from NASA's NeoWs API.

## Services

### User Management Service

This service is responsible for:

- User registration and login.
- Authentication via JWT.
- Storing user sessions in Redis.
- Verifying token validity when accessing other services.

#### Endpoints

| Endpoint             | Description             | Method | Request Body        | Response Body        |
| -------------------- | ----------------------- | ------ | ------------------- | -------------------- |
| `/auth/register`     | Register a new user     | POST   | RegisterRequest     | UserRedis            |
| `/auth/login`        | Login and get JWT token | POST   | LoginRequest        | JwtResponse          |
| `/auth/refreshToken` | Refresh JWT token       | POST   | RefreshTokenRequest | JwtResponse          |
| `/auth/validate`     | Validate JWT token      | POST   | TokenRequest        | Boolean (true/false) |

---

## Technologies Used

- **Spring Boot**: Core framework for developing both services.
- **Redis**: For storing user data in `User Management Service`.
- **Docker**: Used to run Redis, PostgreSQL, and both services containers.
- **JWT**: Token-based authentication.

---

## Installation

### Prerequisites

- Docker installed on your system.
- Java 17+ and Maven.

### Running the Services

1. Clone the repository.
2. Navigate to the project folder.
3. Build the services using Maven:

   ```bash
   mvn clean install
   ```

4. Run Docker containers for Redis, PostgreSQL, and services using Docker Compose:

```bash
 docker-compose up
```
