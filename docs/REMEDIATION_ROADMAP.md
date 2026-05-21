# Remediation Roadmap — Spring PetClinic REST API

Gaps from the [Gap Analysis](./GAP_ANALYSIS.md) are prioritized into three phases:
- **Phase 1 — Quick Wins**: High severity, small effort. Immediate impact with minimal risk.
- **Phase 2 — Important**: High/medium severity, medium effort. Core improvements.
- **Phase 3 — Polish**: Lower severity or larger effort. Long-term quality improvements.

---

## Phase 1 — Quick Wins (1–2 weeks)

### 1.1 Fix `DataIntegrityViolationException` HTTP Status (EH-1)

**Gap:** `DataIntegrityViolationException` is incorrectly mapped to `404 Not Found`. It should be `409 Conflict`.

**Devin Prompt:**
> In `src/main/java/org/springframework/samples/petclinic/rest/advice/ExceptionControllerAdvice.java`, change the `handleDataIntegrityViolationException` method to return `HttpStatus.CONFLICT` (409) instead of `HttpStatus.NOT_FOUND` (404). Update the corresponding test in `OwnerRestControllerTests` (the `testDeleteOwnerError` test) to expect 409. Run `./mvnw test` to verify all tests pass.

---

### 1.2 Fix Empty Collection Returning 404 (A-2)

**Gap:** All list endpoints return `404` for empty results. Should return `200 OK` with `[]`.

**Devin Prompt:**
> In all REST controllers (`OwnerRestController`, `PetRestController`, `VetRestController`, `VisitRestController`, `SpecialtyRestController`, `PetTypeRestController`), update every `list*()` method to return `ResponseEntity.ok(emptyList)` instead of `new ResponseEntity<>(HttpStatus.NOT_FOUND)` when the collection is empty. Update the corresponding controller tests to expect 200 with an empty JSON array `[]` instead of 404. Run `./mvnw test` to verify.

---

### 1.3 Fix 204 Responses That Include Bodies (EH-6 / A-5)

**Gap:** Update methods return `HttpStatus.NO_CONTENT` (204) with a response body, violating HTTP spec.

**Devin Prompt:**
> In all REST controllers, change update methods (e.g., `updateOwner`, `updatePet`, `updateVet`, `updateVisit`, `updateSpecialty`, `updatePetType`) to return `HttpStatus.OK` (200) with the response body, or `HttpStatus.NO_CONTENT` (204) without a body. Choose `200 OK` with the updated DTO for consistency. Update the controller tests accordingly. Run `./mvnw test`.

---

### 1.4 Enable Security by Default (S-1)

**Gap:** `petclinic.security.enable=false` means security is off unless explicitly turned on.

**Devin Prompt:**
> In `src/main/resources/application.properties`, change `petclinic.security.enable=false` to `petclinic.security.enable=true`. Create a new file `src/main/resources/application-dev.properties` with `petclinic.security.enable=false` so developers can still run without auth using `-Dspring.profiles.active=h2,spring-data-jpa,dev`. Update the README.md security section to document this change. Run `./mvnw test` and fix any test failures by adding security context where needed.

---

### 1.5 Fix `CascadeType.ALL` on Pet→PetType (R-7)

**Gap:** `Pet` has `@ManyToOne(cascade = CascadeType.ALL)` on `PetType`. Deleting a pet could cascade-delete the shared `PetType`.

**Devin Prompt:**
> In `src/main/java/org/springframework/samples/petclinic/model/Pet.java`, change the `@ManyToOne(cascade = CascadeType.ALL)` annotation on the `type` field to `@ManyToOne` (no cascade). The `PetType` lifecycle should be managed independently. Run `./mvnw test` to verify no regressions.

---

### 1.6 Add Structured JSON Logging (O-1)

**Gap:** Logback uses plain-text format. Structured JSON is needed for log aggregation.

**Devin Prompt:**
> Add `net.logstash.logback:logstash-logback-encoder` dependency to `pom.xml`. Create a new logback profile in `src/main/resources/logback-spring.xml` that uses `LogstashEncoder` for JSON output when the `production` Spring profile is active, and keeps the existing plain-text format for local dev. Remove or rename the existing `logback.xml` to avoid conflicts. Run `./mvnw clean package -DskipTests` to verify the build.

---

### 1.7 Add Request Correlation IDs (O-2)

**Gap:** No MDC-based correlation IDs for request tracing.

**Devin Prompt:**
> Create a `src/main/java/org/springframework/samples/petclinic/config/CorrelationIdFilter.java` servlet filter that generates a UUID correlation ID for each request, stores it in MDC as `correlationId`, and adds it to the response as an `X-Correlation-Id` header. Register it as a `@Component` with `@Order(Ordered.HIGHEST_PRECEDENCE)`. Update the logback pattern to include `%X{correlationId}`. Run `./mvnw test`.

---

### 1.8 Fix `BindingErrorsResponse.toJSON()` Stack Trace (EH-3)

**Gap:** `e.printStackTrace()` in `BindingErrorsResponse` writes to stdout instead of using a logger.

**Devin Prompt:**
> In `src/main/java/org/springframework/samples/petclinic/rest/controller/BindingErrorsResponse.java`, replace `e.printStackTrace()` with proper SLF4J logging: add `private static final Logger logger = LoggerFactory.getLogger(BindingErrorsResponse.class);` and use `logger.error("Failed to serialize binding errors", e);`. Run `./mvnw test`.

---

### 1.9 Restrict CORS Origins (S-5)

**Gap:** `@CrossOrigin` is wide open with no origin restriction.

**Devin Prompt:**
> Create a `src/main/java/org/springframework/samples/petclinic/config/CorsConfig.java` centralized CORS configuration class that registers allowed origins via a configurable property `petclinic.cors.allowed-origins` (default: `http://localhost:4200`). Remove the per-controller `@CrossOrigin` annotations from all REST controllers. Add the property to `application.properties`. Run `./mvnw test`.

---

### 1.10 Fix `UserServiceImpl` Field Injection (CO-5)

**Gap:** Uses `@Autowired` field injection instead of constructor injection.

**Devin Prompt:**
> In `src/main/java/org/springframework/samples/petclinic/service/UserServiceImpl.java`, replace the `@Autowired private UserRepository userRepository;` field injection with constructor injection: add a constructor that takes `UserRepository` as a parameter and assigns it to a `private final` field. Remove the `@Autowired` annotation. Run `./mvnw test`.

---

### 1.11 Add Graceful Shutdown (R-5)

**Gap:** No graceful shutdown configuration.

**Devin Prompt:**
> Add `server.shutdown=graceful` and `spring.lifecycle.timeout-per-shutdown-phase=30s` to `src/main/resources/application.properties`. Run `./mvnw spring-boot:run` and verify the application shuts down gracefully with `kill -TERM <pid>`.

---

### 1.12 Disable H2 Console in Non-Dev Profiles (S-6)

**Gap:** H2 console is accessible when running with the H2 profile, including production.

**Devin Prompt:**
> In `src/main/resources/application-h2.properties`, set `spring.h2.console.enabled=false`. Create `src/main/resources/application-dev.properties` (if not already created) and add `spring.h2.console.enabled=true` so the console is only available during development. Update README to note developers should activate the `dev` profile for H2 console access.

---

### 1.13 Add OWASP Dependency-Check (S-8)

**Gap:** No dependency vulnerability scanning in the build.

**Devin Prompt:**
> Add the `org.owasp:dependency-check-maven` plugin to the `<build><plugins>` section in `pom.xml`. Configure it with `<failBuildOnCVSS>7</failBuildOnCVSS>` to fail the build on high-severity vulnerabilities. Add a `<suppressionFile>` pointing to `src/main/resources/owasp-suppressions.xml`. Create the suppression file with an empty `<suppressions/>` root. Run `./mvnw dependency-check:check` to verify.

---

## Phase 2 — Important (2–4 weeks)

### 2.1 Add Pagination to List Endpoints (A-1)

**Gap:** All list endpoints return unbounded results.

**Devin Prompt:**
> Add pagination support to all list endpoints. In the OpenAPI spec `src/main/resources/openapi.yml`, add `page` (default 0), `size` (default 20), and `sort` query parameters to all list operations. Update the `ClinicService` interface and `ClinicServiceImpl` to accept `Pageable` parameters. Update Spring Data JPA repositories to extend `PagingAndSortingRepository`. Update controllers to return `Page<T>` wrapped in a response envelope with `totalElements`, `totalPages`, `page`, and `size` metadata. Update tests. Run `./mvnw test`.

---

### 2.2 Add Custom NotFoundException (EH-2)

**Gap:** Service layer silently returns null; controllers manually check for null.

**Devin Prompt:**
> Create `src/main/java/org/springframework/samples/petclinic/rest/advice/NotFoundException.java` extending `RuntimeException`. In `ClinicServiceImpl`, change `findEntityById()` to throw `NotFoundException` instead of returning `null`. Add an `@ExceptionHandler(NotFoundException.class)` method in `ExceptionControllerAdvice` that returns 404 with a ProblemDetail. Remove the null-check-and-404 boilerplate from all controllers. Update controller tests to expect 404 from the exception handler. Run `./mvnw test`.

---

### 2.3 Add Security Exception Handlers (EH-5)

**Gap:** `AccessDeniedException` and `AuthenticationException` bypass `@ControllerAdvice`.

**Devin Prompt:**
> Add `@ExceptionHandler(AccessDeniedException.class)` returning 403 and `@ExceptionHandler(AuthenticationException.class)` returning 401 to `ExceptionControllerAdvice`, both using ProblemDetail format. Also configure a custom `AuthenticationEntryPoint` in `BasicAuthenticationConfig` that returns ProblemDetail JSON for unauthenticated requests. Add tests for both scenarios. Run `./mvnw test`.

---

### 2.4 Add Handler for Malformed JSON (EH-4)

**Gap:** Malformed JSON returns Spring's default error format, not ProblemDetail.

**Devin Prompt:**
> Add an `@ExceptionHandler(HttpMessageNotReadableException.class)` method to `ExceptionControllerAdvice` that returns 400 Bad Request with a ProblemDetail containing a helpful message about the JSON parsing error. Add a controller test that sends malformed JSON and verifies the ProblemDetail response. Run `./mvnw test`.

---

### 2.5 Add Micrometer Metrics (O-3)

**Gap:** No custom metrics or Prometheus endpoint.

**Devin Prompt:**
> Add `spring-boot-starter-actuator` is already present. Add `io.micrometer:micrometer-registry-prometheus` to `pom.xml`. Expose the `prometheus`, `health`, `info`, and `metrics` actuator endpoints in `application.properties` via `management.endpoints.web.exposure.include=health,info,metrics,prometheus`. Add custom `@Timed` annotations on key service methods. Run `./mvnw spring-boot:run` and verify `http://localhost:9966/petclinic/actuator/prometheus` returns metrics.

---

### 2.6 Add Request Timeout Configuration (R-4)

**Gap:** No server-side request timeouts.

**Devin Prompt:**
> Add the following to `application.properties`: `spring.mvc.async.request-timeout=30000` for async timeout, and `server.tomcat.connection-timeout=5s` for connection timeout. Add `spring.datasource.hikari.connection-timeout=5000` and `spring.datasource.hikari.maximum-pool-size=20` for HikariCP tuning (also addresses R-1). Run `./mvnw test`.

---

### 2.7 Add Retry Logic for Transient DB Errors (R-2)

**Gap:** No retry mechanism for transient database failures.

**Devin Prompt:**
> Add `spring-retry` and `spring-boot-starter-aop` dependencies to `pom.xml`. Add `@EnableRetry` to `PetClinicApplication`. Annotate `ClinicServiceImpl` methods that interact with the database with `@Retryable(retryFor = {DataAccessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))`. Add `@Recover` methods that throw a service-level exception after retries are exhausted. Run `./mvnw test`.

---

### 2.8 Add API Versioning (A-3)

**Gap:** No API versioning strategy.

**Devin Prompt:**
> Add a URL-based versioning prefix. Update the `server` section in `openapi.yml` to use `/petclinic/api/v1`. Update all controller `@RequestMapping` annotations from `api` to `api/v1`. Update test URLs accordingly. Add a note to README.md about versioning strategy. Run `./mvnw test`.

---

### 2.9 Add Full-Stack Integration Tests (T-1)

**Gap:** No tests with real HTTP that exercise the full stack.

**Devin Prompt:**
> Create `src/test/java/org/springframework/samples/petclinic/rest/integration/OwnerIntegrationTest.java` using `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)` with `TestRestTemplate`. Write tests that create, read, update, and delete an owner through real HTTP. Set `petclinic.security.enable=false` via `@TestPropertySource`. Cover at least owners, pets, and visits. Run `./mvnw test`.

---

### 2.10 Add Filtering Support (A-4)

**Gap:** Only `GET /owners?lastName=` supports filtering.

**Devin Prompt:**
> Add filtering query parameters to key list endpoints in `openapi.yml`: `GET /pets?type={typeName}`, `GET /visits?petId={petId}&dateFrom={date}&dateTo={date}`, `GET /vets?specialty={specialtyName}`. Update the generated API interfaces, implement the filtering in controllers/service, and add repository query methods. Update tests. Run `./mvnw test`.

---

### 2.11 Automate Postman/Newman Tests in CI (T-5)

**Gap:** Postman API tests exist but are manual.

**Devin Prompt:**
> Create a GitHub Actions workflow `.github/workflows/api-tests.yml` that: (1) builds and starts the app with `./mvnw spring-boot:run &`, (2) waits for health check, (3) runs `npx newman run src/test/postman/*.json --reporters cli,htmlextra`. Add Newman as a devDependency or install it in the workflow. Ensure the workflow runs on PRs.

---

### 2.12 Add Input Sanitization (S-7)

**Gap:** No XSS protection beyond bean validation.

**Devin Prompt:**
> Add a custom `@SanitizeInput` annotation and corresponding AOP aspect in `src/main/java/org/springframework/samples/petclinic/config/InputSanitizer.java` that strips HTML tags from string fields in request DTOs. Apply it to all controller POST/PUT methods. Alternatively, add `jsoup` dependency and create a Jackson deserializer that sanitizes strings. Add tests with HTML payloads. Run `./mvnw test`.

---

## Phase 3 — Polish (4–8 weeks)

### 3.1 Consolidate Repository Implementations (CO-1)

**Gap:** Three parallel repository implementations add maintenance burden.

**Devin Prompt:**
> Deprecate the `jdbc` and `jpa` repository implementations. Add `@Deprecated` annotations to all classes in `repository/jdbc/` and `repository/jpa/`. Update README to indicate only `spring-data-jpa` is the recommended profile. In a follow-up PR, remove the deprecated implementations and their associated test classes. Run `./mvnw test` after each step.

---

### 3.2 Add OpenTelemetry Distributed Tracing (O-4)

**Gap:** No distributed tracing.

**Devin Prompt:**
> Add `io.opentelemetry:opentelemetry-api` and `io.micrometer:micrometer-tracing-bridge-otel` dependencies to `pom.xml`. Configure OpenTelemetry exporter in `application.properties` with `management.tracing.sampling.probability=1.0` for dev. Add `management.otlp.tracing.endpoint` property. Verify trace propagation headers in integration tests. Run `./mvnw test`.

---

### 3.3 Add Consumer-Driven Contract Tests (T-2)

**Gap:** No contract tests for API compatibility with the Angular front-end.

**Devin Prompt:**
> Add Spring Cloud Contract dependency to `pom.xml`. Create contract definitions in `src/test/resources/contracts/` for the key endpoints (owners CRUD, pets CRUD). Generate contract verification tests and stubs. Document how the Angular front-end team can consume the stubs. Run `./mvnw test`.

---

### 3.4 Add HATEOAS Support (A-6)

**Gap:** No hypermedia links in responses.

**Devin Prompt:**
> Add `spring-boot-starter-hateoas` to `pom.xml`. Create `RepresentationModelAssembler` implementations for each entity (e.g., `OwnerModelAssembler`). Update controllers to return `EntityModel<T>` or `CollectionModel<T>` with self/related links. Update the OpenAPI spec to include `_links` in response schemas. Update tests. Run `./mvnw test`.

---

### 3.5 Add Circuit Breaker (R-3)

**Gap:** No circuit breaker for database failures.

**Devin Prompt:**
> Add `io.github.resilience4j:resilience4j-spring-boot3` dependency to `pom.xml`. Annotate `ClinicServiceImpl` methods with `@CircuitBreaker(name = "clinicService", fallbackMethod = "fallback")`. Implement fallback methods that return cached data or throw a `ServiceUnavailableException`. Configure circuit breaker thresholds in `application.properties`. Add tests. Run `./mvnw test`.

---

### 3.6 Add ETag / Conditional Request Support (A-7)

**Gap:** No cache control headers.

**Devin Prompt:**
> Add a `ShallowEtagHeaderFilter` bean to `SwaggerConfig.java` (or a new `WebConfig.java`). This automatically handles `ETag` generation and `304 Not Modified` responses. For more control, add `@GetMapping` methods that use `EntityManager` version fields and return `ResponseEntity` with `ETag` and `Last-Modified` headers. Run `./mvnw test`.

---

### 3.7 Add MapStruct Mapper Unit Tests (T-3)

**Gap:** Mapper edge cases not tested directly.

**Devin Prompt:**
> Create `src/test/java/org/springframework/samples/petclinic/mapper/OwnerMapperTest.java` (and similar for other mappers). Test null inputs, empty collections, nested object mapping (Owner → OwnerDto with pets, Pet → PetDto with visits). Use `@SpringBootTest` or manual `Mappers.getMapper()`. Run `./mvnw test`.

---

### 3.8 Add Exception Handler Tests (T-4)

**Gap:** `ExceptionControllerAdvice` not directly tested.

**Devin Prompt:**
> Create `src/test/java/org/springframework/samples/petclinic/rest/advice/ExceptionControllerAdviceTest.java`. Use `MockMvc` with a test controller that throws each exception type. Verify ProblemDetail fields (status, title, detail, timestamp, schemaValidationErrors). Run `./mvnw test`.

---

### 3.9 Add Request/Response Logging Interceptor (O-5)

**Gap:** No HTTP-level logging.

**Devin Prompt:**
> Create `src/main/java/org/springframework/samples/petclinic/config/RequestLoggingConfig.java` that registers a `CommonsRequestLoggingFilter` bean with `setIncludeQueryString(true)`, `setIncludePayload(true)`, `setMaxPayloadLength(10000)`, and `setIncludeHeaders(true)`. Enable it only when `logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG`. Run `./mvnw spring-boot:run` and verify request logging.

---

### 3.10 Refactor Shared Null-Check Pattern (CO-4)

**Gap:** Null-check-then-404 pattern duplicated across controllers.

**Devin Prompt:**
> This is addressed by Phase 2 item 2.2 (custom NotFoundException). After that is merged, verify all controllers no longer have manual null checks. If any remain, refactor them to rely on the service layer throwing `NotFoundException`. Run `./mvnw test`.

---

## Summary

| Phase | Items | Combined Effort | Key Outcomes |
|---|---|---|---|
| **Phase 1** | 13 | ~1–2 weeks | Fix HTTP status bugs, enable security, structured logging, correlation IDs, CORS hardening |
| **Phase 2** | 12 | ~2–4 weeks | Pagination, better error handling, metrics, retries, API versioning, integration tests |
| **Phase 3** | 10 | ~4–8 weeks | Repo consolidation, distributed tracing, contract tests, HATEOAS, circuit breakers |
