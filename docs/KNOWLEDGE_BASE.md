# Knowledge Base — Spring PetClinic REST API

## 1. Architecture Overview

### 1.1 System Purpose

Spring PetClinic REST is a headless RESTful backend for a veterinary clinic management system. It exposes CRUD APIs for managing **owners, pets, visits, veterinarians, specialties, pet types, and users**. There is no server-side UI — it is designed to be consumed by a separate Angular front-end ([spring-petclinic-angular](https://github.com/spring-petclinic/spring-petclinic-angular)).

### 1.2 Technology Stack

| Component | Technology | Version |
|---|---|---|
| Framework | Spring Boot | 4.0.6 |
| Language | Java | 24+ (enforced by maven-enforcer-plugin) |
| Build Tool | Maven (with Maven Wrapper) | 3.9.9 |
| ORM | Spring Data JPA / Hibernate | Inherited from Spring Boot parent |
| API Spec | OpenAPI 3.1 (code-gen) | openapi-generator 7.22.0 |
| DTO Mapping | MapStruct | 1.6.3 |
| Security | Spring Security (Basic Auth, toggleable) | Inherited from Spring Boot parent |
| Documentation | springdoc-openapi (Swagger UI) | 3.0.3 |
| Databases | H2 (default), HSQLDB, MySQL 8.4, PostgreSQL 16.3 | Runtime-selectable via profiles |
| Serialization | Jackson (tools.jackson) | Inherited from Spring Boot parent |
| Containerization | Google Jib | 3.5.1 |
| Test | JUnit 5, Mockito, MockMvc, AssertJ | Inherited from Spring Boot parent |
| Code Quality | Checkstyle, JaCoCo (85% line / 66% branch), SpotBugs | Various |

### 1.3 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     Angular Front-End (separate repo)           │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP (JSON)
┌────────────────────────────▼────────────────────────────────────┐
│  Spring Boot Application  (port 9966, context /petclinic/)      │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────────┐  │
│  │  REST Layer  │  │  Security    │  │  Swagger / OpenAPI    │  │
│  │  (Controllers│  │  (Basic Auth │  │  (springdoc)          │  │
│  │   + DTOs)    │  │   toggleable)│  │                       │  │
│  └──────┬───────┘  └──────────────┘  └───────────────────────┘  │
│         │                                                        │
│  ┌──────▼───────┐  ┌──────────────┐                              │
│  │  MapStruct   │  │  Service     │                              │
│  │  Mappers     │──│  Layer       │                              │
│  │  (Entity↔DTO)│  │  (ClinicSvc) │                              │
│  └──────────────┘  └──────┬───────┘                              │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │  Repository Layer (profile-switchable)                    │   │
│  │  ┌─────────────┐ ┌────────────┐ ┌─────────────────────┐  │   │
│  │  │ Spring Data  │ │ JPA impl   │ │ JDBC impl           │  │   │
│  │  │ JPA (default)│ │            │ │ (NamedParameterJdbc) │  │   │
│  │  └─────────────┘ └────────────┘ └─────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │  Database (H2 in-memory default; MySQL / PostgreSQL opt.) │   │
│  └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
```

### 1.4 Key Design Patterns

| Pattern | Implementation |
|---|---|
| **API-First** | OpenAPI 3.1 spec (`src/main/resources/openapi.yml`) drives code generation of DTOs and API interfaces at build time |
| **Service Facade** | `ClinicService` / `ClinicServiceImpl` — single entry point for all domain operations, annotated with `@Transactional` |
| **Repository Abstraction** | Custom repository interfaces (`OwnerRepository`, etc.) with three implementations selectable by Spring profile: `spring-data-jpa`, `jpa`, `jdbc` |
| **DTO / Entity Separation** | Generated DTOs for the REST boundary; MapStruct mappers handle bidirectional conversion |
| **Role-Based Access Control** | `@PreAuthorize` annotations on every controller method; roles defined in `Roles.java` component |
| **Conditional Security** | Security on/off via `petclinic.security.enable` property; `BasicAuthenticationConfig` vs `DisableSecurityConfig` |
| **Global Exception Handling** | `@ControllerAdvice` (`ExceptionControllerAdvice`) returns RFC 7807 `ProblemDetail` responses |

### 1.5 Package Structure

```
org.springframework.samples.petclinic
├── PetClinicApplication.java          # Boot entry point
├── config/
│   └── SwaggerConfig.java             # OpenAPI / Swagger UI bean config
├── mapper/                            # MapStruct mapper interfaces (7 mappers)
│   ├── OwnerMapper, PetMapper, PetTypeMapper, SpecialtyMapper
│   ├── UserMapper, VetMapper, VisitMapper
├── model/                             # JPA entities (10 classes)
│   ├── BaseEntity, NamedEntity, Person (superclasses)
│   ├── Owner, Pet, PetType, Specialty, Vet, Visit
│   ├── User, Role
├── repository/                        # Repository interfaces (7)
│   ├── OwnerRepository, PetRepository, PetTypeRepository, ...
│   ├── jdbc/       (JDBC implementations, ~10 classes)
│   ├── jpa/        (JPA implementations, ~7 classes)
│   └── springdatajpa/ (Spring Data JPA, ~12 classes)
├── rest/
│   ├── advice/
│   │   └── ExceptionControllerAdvice.java
│   └── controller/                    # REST controllers (8)
│       ├── OwnerRestController, PetRestController, PetTypeRestController
│       ├── SpecialtyRestController, UserRestController, VetRestController
│       ├── VisitRestController, RootRestController
│       └── BindingErrorsResponse.java
├── security/
│   ├── BasicAuthenticationConfig.java
│   ├── DisableSecurityConfig.java
│   └── Roles.java
└── service/
    ├── ClinicService.java (interface)
    ├── ClinicServiceImpl.java
    ├── UserService.java (interface)
    └── UserServiceImpl.java
```

---

## 2. Data Models

### 2.1 Entity Hierarchy

```
BaseEntity (id: Integer, @MappedSuperclass)
├── NamedEntity (name: String, @MappedSuperclass)
│   ├── PetType (@Entity, table: types)
│   ├── Specialty (@Entity, table: specialties)
│   └── Pet (@Entity, table: pets)
├── Person (firstName, lastName: String, @MappedSuperclass)
│   ├── Owner (@Entity, table: owners)
│   └── Vet (@Entity, table: vets)
├── Visit (@Entity, table: visits)
└── Role (@Entity, table: roles)

User (@Entity, table: users) — standalone, PK is username (String)
```

### 2.2 Entity Details

| Entity | Table | Key Fields | Relationships |
|---|---|---|---|
| **Owner** | `owners` | id, firstName, lastName, address, city, telephone | `OneToMany` → Pet (cascade ALL, EAGER) |
| **Pet** | `pets` | id, name, birthDate | `ManyToOne` → Owner, `ManyToOne` → PetType (cascade ALL), `OneToMany` → Visit (cascade ALL, EAGER) |
| **PetType** | `types` | id, name | Referenced by Pet |
| **Visit** | `visits` | id, date (visit_date), description | `ManyToOne` → Pet |
| **Vet** | `vets` | id, firstName, lastName | `ManyToMany` → Specialty (EAGER, join table: vet_specialties) |
| **Specialty** | `specialties` | id, name | Referenced by Vet |
| **User** | `users` | username (PK), password, enabled | `OneToMany` → Role (cascade ALL, EAGER) |
| **Role** | `roles` | id, name (role column), username (FK) | `ManyToOne` → User |

### 2.3 ER Diagram (Tables)

```
┌──────────┐     ┌──────────┐     ┌──────────────────┐
│  types   │     │  owners  │     │     users        │
│──────────│     │──────────│     │──────────────────│
│ id  (PK) │     │ id  (PK) │     │ username   (PK)  │
│ name     │     │ first_name│    │ password         │
└────┬─────┘     │ last_name │     │ enabled          │
     │           │ address   │     └────────┬─────────┘
     │           │ city      │              │
     │           │ telephone │              │
     │           └─────┬─────┘     ┌────────▼─────────┐
     │                 │           │     roles         │
┌────▼─────┐     ┌─────▼─────┐    │──────────────────│
│   pets   │     │  visits   │    │ id       (PK)    │
│──────────│     │───────────│    │ username (FK)    │
│ id  (PK) │     │ id   (PK) │    │ role             │
│ name     │     │ pet_id(FK)│    └──────────────────┘
│ birth_date│    │ visit_date│
│ type_id(FK)│   │ description│
│ owner_id(FK)│  └───────────┘
└──────────┘

┌──────────────┐     ┌──────────────────┐     ┌──────────────┐
│    vets      │     │ vet_specialties  │     │ specialties  │
│──────────────│     │──────────────────│     │──────────────│
│ id      (PK) │◄───│ vet_id      (FK) │     │ id      (PK) │
│ first_name   │     │ specialty_id(FK) │───►│ name         │
│ last_name    │     └──────────────────┘     └──────────────┘
└──────────────┘
```

### 2.4 Validation Rules

| Entity | Field | Constraint |
|---|---|---|
| Person (Owner/Vet) | firstName | `@NotEmpty` |
| Person (Owner/Vet) | lastName | `@NotEmpty` |
| Owner | address | `@NotEmpty` |
| Owner | city | `@NotEmpty` |
| Owner | telephone | `@NotEmpty`, `@Digits(fraction=0, integer=10)`, `@Pattern(regexp="^[0-9]{10}$")` |
| NamedEntity (Pet/PetType/Specialty) | name | `@NotEmpty` |
| Visit | description | `@NotEmpty` |

---

## 3. API Surface Map

**Base URL:** `http://localhost:9966/petclinic/api`

### 3.1 Owner Endpoints

| Method | Path | Auth Role | Request Body | Response | Status Codes |
|---|---|---|---|---|---|
| GET | `/owners` | OWNER_ADMIN | — (query: `lastName`) | `List<OwnerDto>` | 200, 404 |
| GET | `/owners/{ownerId}` | OWNER_ADMIN | — | `OwnerDto` | 200, 404 |
| POST | `/owners` | OWNER_ADMIN | `OwnerFieldsDto` | `OwnerDto` | 201 |
| PUT | `/owners/{ownerId}` | OWNER_ADMIN | `OwnerFieldsDto` | `OwnerDto` | 204, 404 |
| DELETE | `/owners/{ownerId}` | OWNER_ADMIN | — | — | 204, 404 |
| GET | `/owners/{ownerId}/pets/{petId}` | OWNER_ADMIN | — | `PetDto` | 200, 404 |
| POST | `/owners/{ownerId}/pets` | OWNER_ADMIN | `PetFieldsDto` | `PetDto` | 201, 404 |
| PUT | `/owners/{ownerId}/pets/{petId}` | OWNER_ADMIN | `PetFieldsDto` | — | 204, 404 |
| POST | `/owners/{ownerId}/pets/{petId}/visits` | OWNER_ADMIN | `VisitFieldsDto` | `VisitDto` | 201 |

### 3.2 Pet Endpoints

| Method | Path | Auth Role | Request Body | Response | Status Codes |
|---|---|---|---|---|---|
| GET | `/pets` | OWNER_ADMIN | — | `List<PetDto>` | 200, 404 |
| GET | `/pets/{petId}` | OWNER_ADMIN | — | `PetDto` | 200, 404 |
| PUT | `/pets/{petId}` | OWNER_ADMIN | `PetDto` | `PetDto` | 204, 404 |
| DELETE | `/pets/{petId}` | OWNER_ADMIN | — | — | 204, 404 |

### 3.3 Vet Endpoints

| Method | Path | Auth Role | Request Body | Response | Status Codes |
|---|---|---|---|---|---|
| GET | `/vets` | VET_ADMIN | — | `List<VetDto>` | 200, 404 |
| GET | `/vets/{vetId}` | VET_ADMIN | — | `VetDto` | 200, 404 |
| POST | `/vets` | VET_ADMIN | `VetDto` | `VetDto` | 201 |
| PUT | `/vets/{vetId}` | VET_ADMIN | `VetDto` | `VetDto` | 204, 404 |
| DELETE | `/vets/{vetId}` | VET_ADMIN | — | — | 204, 404 |

### 3.4 PetType Endpoints

| Method | Path | Auth Role | Request Body | Response | Status Codes |
|---|---|---|---|---|---|
| GET | `/pettypes` | OWNER_ADMIN or VET_ADMIN | — | `List<PetTypeDto>` | 200, 404 |
| GET | `/pettypes/{petTypeId}` | OWNER_ADMIN or VET_ADMIN | — | `PetTypeDto` | 200, 404 |
| POST | `/pettypes` | VET_ADMIN | `PetTypeFieldsDto` | `PetTypeDto` | 201 |
| PUT | `/pettypes/{petTypeId}` | VET_ADMIN | `PetTypeDto` | `PetTypeDto` | 204, 404 |
| DELETE | `/pettypes/{petTypeId}` | VET_ADMIN | — | — | 204, 404 |

### 3.5 Specialty Endpoints

| Method | Path | Auth Role | Request Body | Response | Status Codes |
|---|---|---|---|---|---|
| GET | `/specialties` | VET_ADMIN | — | `List<SpecialtyDto>` | 200, 404 |
| GET | `/specialties/{specialtyId}` | VET_ADMIN | — | `SpecialtyDto` | 200, 404 |
| POST | `/specialties` | VET_ADMIN | `SpecialtyDto` | `SpecialtyDto` | 201 |
| PUT | `/specialties/{specialtyId}` | VET_ADMIN | `SpecialtyDto` | `SpecialtyDto` | 204, 404 |
| DELETE | `/specialties/{specialtyId}` | VET_ADMIN | — | — | 204, 404 |

### 3.6 Visit Endpoints

| Method | Path | Auth Role | Request Body | Response | Status Codes |
|---|---|---|---|---|---|
| GET | `/visits` | OWNER_ADMIN | — | `List<VisitDto>` | 200, 404 |
| GET | `/visits/{visitId}` | OWNER_ADMIN | — | `VisitDto` | 200, 404 |
| POST | `/visits` | OWNER_ADMIN | `VisitDto` | `VisitDto` | 201 |
| PUT | `/visits/{visitId}` | OWNER_ADMIN | `VisitFieldsDto` | `VisitDto` | 204, 404 |
| DELETE | `/visits/{visitId}` | OWNER_ADMIN | — | — | 204, 404 |

### 3.7 User Endpoints

| Method | Path | Auth Role | Request Body | Response | Status Codes |
|---|---|---|---|---|---|
| POST | `/users` | ADMIN | `UserDto` | `UserDto` | 201 |

### 3.8 Utility Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/` | Redirects to Swagger UI |
| GET | `/oops` | Always throws an exception (for error-handling demo) |
| GET | `/actuator/health` | Spring Actuator health check |
| GET | `/v3/api-docs` | OpenAPI 3.1 JSON spec |
| GET | `/swagger-ui.html` | Swagger UI (redirects to `/swagger-ui/index.html`) |
| GET | `/h2-console` | H2 database console (when H2 profile active) |

---

## 4. Business Logic Inventory

### 4.1 ClinicService (Central Facade)

`ClinicServiceImpl` is the single service facade coordinating all domain logic. Every method is `@Transactional` (read-only for queries, read-write for mutations).

| Operation | Logic Notes |
|---|---|
| `findOwnerByLastName(name)` | Delegates to repository; Spring Data JPA uses `LIKE :lastName%` prefix matching |
| `findOwnerById(id)` | Wraps in `findEntityById()` — catches `ObjectRetrievalFailureException` / `EmptyResultDataAccessException`, returns `null` |
| `savePet(pet)` | Resolves `PetType` by ID before saving: `pet.setType(findPetTypeById(pet.getType().getId()))` |
| `findSpecialtiesByNameIn(names)` | Batch lookup of specialties by name set (used for vet creation/update) |
| All delete operations | Direct delegation to repository `delete()` |

### 4.2 UserService

| Operation | Logic Notes |
|---|---|
| `saveUser(user)` | Validates at least one role; auto-prefixes `ROLE_` if missing; sets bidirectional `role.setUser(user)` link |

### 4.3 Controller-Level Logic

| Pattern | Detail |
|---|---|
| **Null-check guard** | Every GET/PUT/DELETE controller first checks if entity exists; returns `404 NOT_FOUND` if null |
| **Location header on create** | POST endpoints set `Location` header with URI of the created resource |
| **Vet specialty resolution** | `addVet()` and `updateVet()` resolve specialty entities by name from DB before persisting |
| **Owner delete** | Annotated with `@Transactional` at controller level (cascades to pets and visits) |
| **Pet name nullification** | `addPetToOwner()` sets `pet.getType().setName(null)` before saving (avoids accidentally updating the pet type name) |

### 4.4 Exception Handling

`ExceptionControllerAdvice` (`@ControllerAdvice`) handles three exception types:

| Exception | HTTP Status | Detail Message |
|---|---|---|
| `Exception` (catch-all) | 500 Internal Server Error | "An unexpected error occurred while processing your request" |
| `DataIntegrityViolationException` | 404 Not Found | "The requested resource could not be processed due to a data constraint violation" |
| `MethodArgumentNotValidException` | 400 Bad Request | "The request contains invalid or missing parameters" + field-level `schemaValidationErrors` |

All responses use RFC 7807 `ProblemDetail` format with `timestamp` and `schemaValidationErrors` properties.

---

## 5. Integration Points

### 5.1 Database Connectivity

| Profile | Database | Connection |
|---|---|---|
| `h2` (default) | H2 in-memory | `jdbc:h2:mem:petclinic` / user: `sa` / no password |
| `hsqldb` | HSQLDB in-memory | Auto-configured |
| `mysql` | MySQL 8.4 | `jdbc:mysql://localhost:3306/petclinic` / user: `petclinic` |
| `postgres` | PostgreSQL 16.3 | `jdbc:postgresql://localhost:5432/petclinic` / user: `petclinic` |

Each profile has its own `schema.sql` and `data.sql` under `src/main/resources/db/{profile}/`.

### 5.2 Repository Layer Profiles

| Profile | Implementation | Notes |
|---|---|---|
| `spring-data-jpa` (default) | Spring Data JPA interfaces extending `Repository<T, ID>` | Least boilerplate; auto-generated queries |
| `jpa` | Manual JPA implementations using `EntityManager` | Full JPA control |
| `jdbc` | Plain JDBC using `NamedParameterJdbcTemplate`, `SimpleJdbcInsert`, `RowMapper` | Maximum SQL control |

### 5.3 Security Integration

- **Toggle:** `petclinic.security.enable=true|false` in `application.properties`
- **Mechanism:** HTTP Basic Authentication via JDBC-backed `AuthenticationManagerBuilder`
- **User store:** Same DB tables (`users`, `roles`) queried with raw SQL
- **Password encoding:** `BCryptPasswordEncoder`
- **CORS:** `@CrossOrigin(exposedHeaders = "errors, content-type")` on every controller
- **CSRF:** Disabled in both configurations

### 5.4 Monitoring / Actuator

- `spring-boot-starter-actuator` is included
- Health check: `GET /petclinic/actuator/health`
- Build info exposed via `build-info` goal in spring-boot-maven-plugin

### 5.5 External API Documentation

- Swagger UI at `/petclinic/swagger-ui.html`
- OpenAPI 3.1 JSON spec at `/petclinic/v3/api-docs`
- Configured via `SwaggerConfig.java` bean

---

## 6. Build and Deployment Summary

### 6.1 Build Commands

| Command | Purpose |
|---|---|
| `./mvnw clean install` | Full build with tests, code generation, JaCoCo coverage |
| `./mvnw clean install -DskipTests` | Build without tests |
| `./mvnw spring-boot:run` | Run locally (port 9966) |
| `./mvnw test` | Run test suite (227 tests) |
| `./mvnw checkstyle:check` | Static analysis |

### 6.2 Code Generation Pipeline

1. **OpenAPI Generator** reads `src/main/resources/openapi.yml` → generates DTOs (`*Dto.java`) and API interfaces into `target/generated-sources/openapi/`
2. **MapStruct** annotation processor generates mapper implementations into `target/generated-sources/annotations/`
3. **build-helper-maven-plugin** adds generated sources to the compilation classpath

### 6.3 Test Infrastructure

| Test Category | Count | Approach |
|---|---|---|
| Controller tests | 7 test classes | `MockMvc` + `@MockitoBean` for `ClinicService`; `@WithMockUser` for security |
| Service integration tests | 4 variants × 2 services | `AbstractClinicServiceTests` parameterized across all 4 repository profiles (H2-JDBC, HSQL-JDBC, JPA, Spring Data JPA) |
| Model validation tests | 1 class | Bean validation constraint testing |
| Spring config tests | 1 class | Application context loading |
| **Total** | **~227 tests** | All passing |

### 6.4 Quality Gates

| Tool | Threshold | Scope |
|---|---|---|
| **JaCoCo** | 85% line coverage, 66% branch coverage | Excludes generated DTOs and API interfaces |
| **Checkstyle** | Configured via maven plugin | Runs as part of build |
| **SpotBugs** | Available in reporting plugins | Static bug detection |
| **SonarCloud** | Configured (organization: `spring-petclinic-rest`) | Quality gate badge in README |

### 6.5 Containerization

- **Google Jib** builds Docker images without a local Docker daemon
- Image: `springcommunity/spring-petclinic-rest:latest` and `:4.0.2`
- Published to Docker Hub

### 6.6 Docker Compose

`docker-compose.yml` provides MySQL 8.4 and PostgreSQL 16.3 containers for local persistent-database development:
- `docker-compose --profile mysql up`
- `docker-compose --profile postgres up`
