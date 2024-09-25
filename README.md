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

## Requests and Responses Payload

### Requests

#### Register Request

```sh
{
  "username": "testing",
  "password": "testing"
}
```

#### Login Request

```sh
{
  "username": "testing",
  "password": "testing"
}
```

#### Validate Token Request

```sh
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0aW5nIiwiaWF0IjoxNzI3MjMxODcyLCJleHAiOjE3MjczMTgyNzJ9.W7Qxu9bJ0DCWn95QYO5WGU0z3CnIz1EjuFbVVPdNB-A"
}
```

#### Refresh Token Request

```sh
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0aW5nIiwiaWF0IjoxNzI2NzExMDExLCJleHAiOjE3MjY3OTc0MTF9.kTtuiQu-N9GeudQMXtDWeNz40ZQWr08PDRWDyPGNXLQ"
}
```

### Responses

#### Register Response

```sh
{
    "username": "testing",
    "password": "$2a$10$iM9gtIwfP.P7paowmviXuO4H3ke.WIumb7xTSjYVFmT8aT7fK03X6",
    "token": null,
    "role": "USER"
}
```

#### Login Response

```sh
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0aW5nIiwiaWF0IjoxNzI3MjMxODcyLCJleHAiOjE3MjczMTgyNzJ9.W7Qxu9bJ0DCWn95QYO5WGU0z3CnIz1EjuFbVVPdNB-A",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0aW5nIiwiaWF0IjoxNzI3MjMxODczLCJleHAiOjE3Mjc2NjM4NzN9.-MJNZfgAEcgUKYlUyq3ANDY0E0kRvMizKWCk-pcLYiE"
}
```

#### Validate Response

```sh
true
```

#### Refresh Token Response

```sh
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0aW5nIiwiaWF0IjoxNzI3MjMyMDEzLCJleHAiOjE3MjczMTg0MTN9.VomRuvevvxmRL87u3DiqbKzQTG2ndx2J7HOAwThKOGE",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0aW5nIiwiaWF0IjoxNzI3MjMxODcyLCJleHAiOjE3MjczMTgyNzJ9.W7Qxu9bJ0DCWn95QYO5WGU0z3CnIz1EjuFbVVPdNB-A"
}
```
