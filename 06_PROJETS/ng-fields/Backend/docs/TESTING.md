# Testing

## Test Overview

| Service | Unit Tests | Integration Tests | Total |
|---------|-----------|------------------|-------|
| intervention-service | 36 | 0 | 36 |
| media-service | 28 | 0 | 28 |
| auth-service | 24 | 0 | 24 |
| client-service | 15 | 0 | 15 |
| gateway-service | 8 | 0 | 8 |
| notification-service | 9 | 0 | 9 |
| report-service | 5 | 0 | 5 |
| **Total** | **125** | **0** | **125** |

## Running Tests

```bash
# All services
mvn test

# Single service
.\mvnw.cmd -f "..\pom.xml" test -pl intervention-service -am

# With coverage
mvn test jacoco:report
```

## Test Patterns

- **Unit tests**: Mockito with `@ExtendWith(MockitoExtension.class)`
- **Controller tests**: `@WebMvcTest` with `MockMvc`
- **No integration tests**: No Testcontainers, no embedded PostgreSQL

## Key Coverage Areas

- State machine transitions (InterventionStatus)
- Sync conflict detection
- File upload security (MIME validation, path traversal, size limits)
- Email retry logic
- Analytics aggregation
