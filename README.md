# BNPL (Buy Now, Pay Later) System - Technical Test

## Overview
This project implements a RESTful API for managing customers and loans in a BNPL (Buy Now, Pay Later) system. It provides core functionality for credit line management, loan creation, and payment tracking.

## Key Features
- **Customer Management**
    - Customer registration with credit line assignment
    - Customer information retrieval
- **Loan Operations**
    - Loan creation with automatic credit line deduction
    - Loan status tracking (Active, Late, Completed)
    - Installment-based payment plans
- **Security**
    - JWT-based authentication
    - Role-based access control
- **Documentation**
    - OpenAPI 3.0 specification
    - Interactive Swagger UI

## Technology Stack
### Core
- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **Spring Security**
- **Spring Doc OpenAPI**

### Database
- **PostgreSQL**
- **Flyway** (Database migrations)

### Testing
- **JUnit 5**
- **Testcontainers** (Integration tests)
- **Mockito**

### Utilities
- **Lombok** (Boilerplate reduction)
- **MapStruct** (DTO mapping)
- **SLF4j** (Logging)

## API Documentation
The API follows the OpenAPI 3.0 specification. You can access the documentation in two ways:

1. **OpenAPI Specification**
   ```bash
   http://localhost:8080/api-docs
   ```
2. **Swagger UI**
    ```bash
    http://localhost:8080/api-docs
    ```
   
## Getting started
### Prerequisites

- **Java 17 JDK**
- **Docker and Docker Compose**
- **Gradle 7+**

### Installation

1. **Clone the repository**

```bash
   git clone https://github.com/RubenOAlvarado/aplazo-backend-challenge.git
   cd aplazo-backend-challenge
```

2. **Start the application**
```bash
    docker-compose up -d
```

3. **Access the application**
    - **API:** ```bash http://localhost:8080/api/v1```
    - **Swagger UI:** ```bash http://localhost:8080/swagger-ui.html```