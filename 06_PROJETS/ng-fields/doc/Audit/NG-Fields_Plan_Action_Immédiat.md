# NG-Fields Backend — Plan d'Action Immédiat
> **Semaines 1-2 : Passer de POC (42% couvert) à MVP Production (90% couvert)**

---

## 📌 Objectif global

Faire passer le backend de **4.6/10** (sketch) à **7.5/10** (MVP production) en **2 semaines**.

**Résultat visible:**
- ✅ 5 services 100% complets
- ✅ 80% couverture tests
- ✅ CI/CD GitHub Actions
- ✅ Docs complètes
- ✅ Monitoring (Sentry + logs)

---

# Semaine 1️⃣ : Stabilité technique

## Jour 1 (lundi)
### 🎯 Tema: Setup infra + Docs fondations

**Responsable:** DevOps + Tech Lead  
**Durée:** 8h

### ✅ Tasks

#### 1. Créer `.env.example` centralisé (30 min)
```bash
# Backend/.env.example
SPRING_PROFILES_ACTIVE=dev
JAVA_OPTS=-Xmx512m

POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=ng_fields
POSTGRES_USER=ng_user
POSTGRES_PASSWORD=changeme_dev

KEYCLOAK_URL=http://localhost:8088
KEYCLOAK_REALM=ng-fields
KEYCLOAK_CLIENT_ID=backend-gateway
KEYCLOAK_CLIENT_SECRET=secret123

REDIS_HOST=localhost
REDIS_PORT=6379

SENTRY_DSN=https://YOUR_KEY@sentry.io/YOUR_PROJECT
SENTRY_ENV=development

MEDIA_UPLOAD_DIR=/tmp/ng-fields-media
MEDIA_MAX_SIZE_MB=50
```

**Validation:** `cp .env.example .env && docker compose up -d` fonctionne

#### 2. Ajouter shared-lib base classes (1h)
**Fichier:** `Backend/shared-lib/src/main/java/tg/ngstars/common/`

```java
// dto/StandardErrorResponse.java
public record StandardErrorResponse(
  String code,
  String message,
  LocalDateTime timestamp,
  String path,
  @JsonInclude(JsonInclude.Include.NON_NULL)
  Map<String, String> details
) {}

// exception/BusinessException.java
public class BusinessException extends RuntimeException {
  private final String code;
  
  public BusinessException(String code, String message) {
    super(message);
    this.code = code;
  }
  
  public String getCode() { return code; }
}

// entity/AuditableEntity.java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {
  @CreationTimestamp
  protected LocalDateTime createdAt;
  
  @UpdateTimestamp
  protected LocalDateTime updatedAt;
  
  @Column(nullable = false)
  protected String createdBy;
  
  protected String updatedBy;
}
```

**Validation:** `./mvnw clean compile -pl shared-lib` réussit

#### 3. Créer docs/backend structure (1h)
```bash
mkdir -p docs/backend/04-SERVICES
touch docs/backend/{00-README,01-ARCHITECTURE,02-SETUP-LOCAL,03-STACK,06-SECURITY,07-DATABASE,08-TESTING,09-DEPLOYMENT,10-MONITORING}.md
```

**Validation:** `ls docs/backend/*.md` affiche 9 fichiers

#### 4. Remplir README index (1.5h)
**Utiliser template:** `NG-Fields_Templates_Documentation_Backend.md` section 1.1

Contenu clé:
- Links to 13 docs
- Quick start (5 min)
- Role-based navigation

**Validation:** README explique comment accéder à Setup + API + Security

#### 5. Remplir SETUP-LOCAL.md (1.5h)
**Utiliser template:** `NG-Fields_Templates_Documentation_Backend.md` section 1.2

Contenu:
- Prérequis check
- 5 étapes install
- Verify "it works" checklist
- Troubleshooting courant

**Validation:** Nouveau dev peut clone + lancer en 10 min

#### 6. Ajouter Sentry au tous les services (1h)
**Chaque pom.xml:**
```xml
<dependency>
  <groupId>io.sentry</groupId>
  <artifactId>sentry-spring-boot-starter</artifactId>
  <version>7.0.0</version>
</dependency>
```

**Chaque application.yml:**
```yaml
sentry:
  dsn: ${SENTRY_DSN}
  environment: ${SENTRY_ENV:development}
  traces-sample-rate: ${SENTRY_TRACES_SAMPLE_RATE:0.1}
  enabled: true
```

**Validation:** 
```bash
./mvnw clean compile
# Pas de erreur
```

---

## Jour 2 (mardi)
### 🎯 Tema: Tests foundation + Error handling

**Responsable:** Dev 1 + QA  
**Durée:** 8h

#### 1. Créer GlobalExceptionHandler commun (2h)
**Créer:** `gateway-service/src/main/java/tg/ngstars/gateway/config/GlobalExceptionHandler.java`

```java
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<StandardErrorResponse> handleBusiness(
      BusinessException ex, HttpServletRequest request) {
    
    return ResponseEntity
      .status(BAD_REQUEST)
      .body(new StandardErrorResponse(
        ex.getCode(),
        ex.getMessage(),
        LocalDateTime.now(),
        request.getRequestURI(),
        Map.of()
      ));
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<StandardErrorResponse> handleForbidden(
      ForbiddenException ex, HttpServletRequest request) {
    
    return ResponseEntity
      .status(FORBIDDEN)
      .body(new StandardErrorResponse(
        "ERR_FORBIDDEN",
        ex.getMessage(),
        LocalDateTime.now(),
        request.getRequestURI(),
        Map.of()
      ));
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<StandardErrorResponse> handleNotFound(
      NotFoundException ex, HttpServletRequest request) {
    
    return ResponseEntity
      .status(NOT_FOUND)
      .body(new StandardErrorResponse(
        "ERR_NOT_FOUND",
        ex.getMessage(),
        LocalDateTime.now(),
        request.getRequestURI(),
        Map.of()
      ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<StandardErrorResponse> handleGeneric(
      Exception ex, HttpServletRequest request) {
    
    log.error("Unhandled exception", ex);
    return ResponseEntity
      .status(INTERNAL_SERVER_ERROR)
      .body(new StandardErrorResponse(
        "ERR_INTERNAL",
        "Une erreur interne s'est produite",
        LocalDateTime.now(),
        request.getRequestURI(),
        Map.of()
      ));
  }
}
```

**Validation:**
- API retourne `StandardErrorResponse` format pour toutes erreurs
- Tests: 401 → JSON error, 404 → JSON error, 500 → JSON error

#### 2. Ajouter LoggingInterceptor (1h)
**Créer:** `gateway-service/src/main/java/tg/ngstars/gateway/config/LoggingInterceptor.java`

```java
@Component
public class LoggingInterceptor implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new HandlerInterceptor() {
      @Override
      public boolean preHandle(HttpServletRequest req, HttpServletResponse res, 
                                Object handler) {
        String traceId = req.getHeader("X-Trace-ID");
        if (traceId == null) {
          traceId = UUID.randomUUID().toString();
        }
        MDC.put("traceId", traceId);
        res.addHeader("X-Trace-ID", traceId);
        
        log.info("{} {} - TraceId: {}", req.getMethod(), req.getRequestURI(), traceId);
        return true;
      }
      
      @Override
      public void afterCompletion(HttpServletRequest req, HttpServletResponse res, 
                                   Object handler, Exception ex) {
        MDC.clear();
      }
    });
  }
}
```

**Validation:** 
- Chaque requête a unique `X-Trace-ID`
- Logs incluent traceId
- Même traceId traverse tous les services

#### 3. Activer actuator endpoints (1h)
**Ajouter à chaque `application.yml`:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

**Validation:**
```bash
curl http://localhost:8080/actuator/health
# {"status":"UP"}

curl http://localhost:8080/actuator/metrics
# Liste des métriques
```

#### 4. Créer 10 tests core (2h)
**Fichier:** `Backend/auth-service/src/test/java/tg/ngstars/auth/AuthServiceIntegrationTest.java`

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
class AuthServiceIntegrationTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void testLoginFlow() {
    // 1. Créer user
    CreateUserRequest req = new CreateUserRequest(
      "test@example.com", "John", "Doe", List.of("ROLE_USER")
    );
    
    // 2. Vérifier création
    // 3. Login avec credentials
    // 4. Vérifier JWT obtenu
    // 5. Utiliser JWT pour appel API
  }

  @Test
  void testUnauthorizedAccess() {
    webTestClient.get()
      .uri("/api/users")
      .exchange()
      .expectStatus().isUnauthorized();
  }

  @Test
  void testForbiddenAccess() {
    // Tech veut voir tous les users (besoin ADMIN)
    webTestClient.get()
      .uri("/api/users")
      .header("Authorization", "Bearer " + techToken)
      .exchange()
      .expectStatus().isForbidden();
  }
  
  // 7 tests supplémentaires...
}
```

**Validation:**
```bash
./mvnw test -pl auth-service
# ✅ 10 tests passed
```

#### 5. Créer GitHub Actions workflow (2h)
**Créer:** `.github/workflows/backend.yml`

```yaml
name: Backend Build & Test

on: [push, pull_request]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16
        env:
          POSTGRES_PASSWORD: postgres
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v4
      
      - uses: actions/setup-java@v4
        with:
          java-version: '25'
          distribution: 'temurin'
          cache: maven

      - name: Build Backend
        run: cd Backend && ./mvnw clean verify -DskipTests

      - name: Run Tests
        run: cd Backend && ./mvnw test

      - name: Generate Coverage
        run: cd Backend && ./mvnw jacoco:report

      - name: Upload Coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          file: ./Backend/target/site/jacoco/jacoco.xml
          fail_ci_if_error: false
```

**Validation:**
- Push à `feature/setup-phase1`
- GitHub Actions execute automatiquement
- Build + tests réussissent

---

## Jour 3 (mercredi)
### 🎯 Tema: Intervention-service core + Media-service security

**Responsable:** Dev 2  
**Durée:** 8h

#### 1. Compléter SyncController 50% (2.5h)
**Fichier:** `Backend/intervention-service/src/main/java/tg/ngstars/interv/controller/SyncController.java`

```java
@RestController
@RequestMapping("/api/interventions/sync")
public class SyncController {
  
  private final InterventionService interventionService;
  
  /**
   * Sync offline interventions avec le serveur.
   * 
   * Chaque intervention a:
   * - localId (généré localement, UUID4)
   * - serverId (généré au sync, NULL avant)
   * 
   * Mapping:
   * localId=abc123 → CREATE → serverId=def456
   * localId=def456 + modifications → UPDATE
   */
  @PostMapping
  public ResponseEntity<SyncResponse> sync(
      @RequestBody SyncRequest req,
      @AuthenticationPrincipal Jwt jwt) {
    
    String userId = jwt.getSubject();
    List<SyncedIntervention> synced = new ArrayList<>();
    List<SyncError> errors = new ArrayList<>();
    
    // Pour chaque intervention en cache local
    for (InterventionData intervention : req.interventions()) {
      try {
        // 1. Vérifier si localId existe déjà
        Optional<Intervention> existing = interventionService
          .findByLocalId(intervention.localId(), userId);
        
        if (existing.isPresent()) {
          // UPDATE
          Intervention updated = interventionService.update(
            existing.get().getId(),
            intervention,
            userId
          );
          synced.add(new SyncedIntervention(
            intervention.localId(),
            updated.getId(),  // serverId
            UPDATED
          ));
        } else {
          // CREATE
          Intervention created = interventionService.create(
            intervention,
            userId
          );
          synced.add(new SyncedIntervention(
            intervention.localId(),
            created.getId(),  // serverId
            CREATED
          ));
        }
      } catch (Exception e) {
        errors.add(new SyncError(
          intervention.localId(),
          e.getMessage()
        ));
      }
    }
    
    return ResponseEntity.ok(new SyncResponse(synced, errors));
  }
}

// DTOs
public record SyncRequest(
  List<InterventionData> interventions,
  LocalDateTime lastSync
) {}

public record InterventionData(
  String localId,           // UUID4 client-side
  String clientId,
  String description,
  LocalDateTime scheduledStart,
  List<PhotoData> photos,
  String signatureBase64    // Optionnel
) {}

public record SyncResponse(
  List<SyncedIntervention> synced,
  List<SyncError> errors
) {}

public record SyncedIntervention(
  String localId,
  String serverId,          // NULL si erreur, sinon UUID
  SyncStatus status         // CREATED / UPDATED / ERROR
) {}

enum SyncStatus { CREATED, UPDATED, ERROR }
```

**Validation:**
```bash
# Flutter app peut maintenant:
# 1. Créer intervention offline
# 2. Uploader localId + data au serveur
# 3. Recevoir serverId en retour
# 4. Continuer avec photos + signature utilisant serverId
```

#### 2. Ajouter image thumbnail generation (1.5h)
**Fichier:** `Backend/media-service/src/main/java/tg/ngstars/media/service/ImageService.java`

```java
@Service
public class ImageService {
  
  private static final int THUMBNAIL_WIDTH = 200;
  private static final int THUMBNAIL_HEIGHT = 200;
  
  /**
   * Génère thumbnail 200x200 pour chaque image uploadée.
   * Utilise BufferedImage (intégré Java, pas de dépendance externe)
   */
  public byte[] generateThumbnail(byte[] imageData) throws IOException {
    BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageData));
    
    // Resize
    BufferedImage thumbnail = new BufferedImage(
      THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, BufferedImage.TYPE_INT_RGB
    );
    Graphics2D g2d = thumbnail.createGraphics();
    g2d.drawImage(original, 0, 0, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, null);
    g2d.dispose();
    
    // Encode to JPEG
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(thumbnail, "jpg", baos);
    return baos.toByteArray();
  }
}

// Usage dans FileController
@PostMapping("/upload")
public ResponseEntity<FileResponse> upload(
    @RequestParam MultipartFile file) throws IOException {
  
  byte[] original = file.getBytes();
  byte[] thumbnail = imageService.generateThumbnail(original);
  
  // Sauvegarder both
  String originalPath = storageService.save("original", file.getOriginalFilename(), original);
  String thumbnailPath = storageService.save("thumbnails", file.getOriginalFilename(), thumbnail);
  
  return ResponseEntity.ok(new FileResponse(originalPath, thumbnailPath));
}
```

**Validation:**
```bash
# Upload photo → Reçoit 2 URLs
# {
#   "originalUrl": "/files/photos/abc123.jpg",
#   "thumbnailUrl": "/files/photos/thumbnails/abc123.jpg"
# }

# Thumbnail = 200x200, optimisé
```

#### 3. Implémenter cleanup policy (1.5h)
**Fichier:** `Backend/media-service/src/main/java/tg/ngstars/media/service/FileCleanupService.java`

```java
@Service
public class FileCleanupService {
  
  private final FileRepository fileRepository;
  private final StorageService storageService;
  
  @Scheduled(cron = "0 2 * * *")  // 2h du matin chaque jour
  public void cleanupOrphanedFiles() {
    LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
    
    // Trouver fichiers non liés + créés avant cutoff
    List<File> orphaned = fileRepository
      .findOrphanedFilesBefore(cutoff);
    
    for (File file : orphaned) {
      try {
        storageService.delete(file.getPath());
        fileRepository.delete(file);
        log.info("Deleted orphaned file: {}", file.getPath());
      } catch (Exception e) {
        log.error("Failed to delete orphaned file: {}", file.getPath(), e);
      }
    }
  }
}
```

**Configuration:**
```yaml
# application.yml
app:
  media:
    cleanup:
      enabled: true
      days-to-keep: 30
      schedule: "0 2 * * *"  # 2h du matin
```

**Validation:**
```bash
# Fichiers orphanes (non liés à intervention) 
# créés 30+ jours ago → supprimés automatiquement
```

#### 4. Ajouter file validation (1.5h)
**Créer:** `Backend/media-service/src/main/java/tg/ngstars/media/validator/FileValidator.java`

```java
@Component
public class FileValidator {
  
  private static final Set<String> ALLOWED_TYPES = Set.of(
    "image/jpeg", "image/png", "application/pdf"
  );
  
  private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
    "jpg", "jpeg", "png", "pdf"
  );
  
  private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;  // 50MB
  
  public void validate(MultipartFile file) {
    // Vérifier type MIME
    if (!ALLOWED_TYPES.contains(file.getContentType())) {
      throw new ValidationException(
        "ERR_FILE_TYPE_NOT_ALLOWED",
        "Type MIME " + file.getContentType() + " non autorisé"
      );
    }
    
    // Vérifier extension
    String filename = file.getOriginalFilename();
    String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    if (!ALLOWED_EXTENSIONS.contains(ext)) {
      throw new ValidationException(
        "ERR_FILE_EXTENSION_NOT_ALLOWED",
        "Extension ." + ext + " non autorisée"
      );
    }
    
    // Vérifier taille
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new ValidationException(
        "ERR_FILE_TOO_LARGE",
        "Fichier > 50MB"
      );
    }
    
    // Vérifier signature fichier (magic bytes)
    validateMagicBytes(file);
  }
  
  private void validateMagicBytes(MultipartFile file) throws IOException {
    byte[] bytes = new byte[4];
    file.getInputStream().read(bytes);
    
    // JPEG: FF D8 FF
    // PNG: 89 50 4E 47
    // PDF: 25 50 44 46
    
    if (!isValidMagic(bytes)) {
      throw new ValidationException(
        "ERR_INVALID_FILE",
        "Fichier corrompu ou format invalide"
      );
    }
  }
}

// Usage
@PostMapping("/upload")
public ResponseEntity<FileResponse> upload(@RequestParam MultipartFile file) {
  fileValidator.validate(file);
  // ... traiter fichier
}
```

**Validation:**
```bash
# Upload .exe → 415 Unsupported Media Type
# Upload 100MB → 413 Payload Too Large
# Upload photo valide → 201 Created
```

#### 5. Créer tests media-service (1h)
```bash
./mvnw test -pl media-service
# 0 → 8 tests
```

---

## Jour 4 (jeudi)
### 🎯 Tema: Notification-service skeleton → EmailService

**Responsable:** Dev 1  
**Durée:** 8h

#### 1. Implémenter EmailService (2.5h)
**Fichier:** `Backend/notification-service/src/main/java/tg/ngstars/notification/service/EmailService.java`

```java
@Service
public class EmailService {
  
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;
  private final RetryTemplate retryTemplate;
  
  /**
   * Envoyer email avec retry (max 3 attempts, backoff exponentiel)
   */
  public void sendWelcomeEmail(String email, String username) {
    String subject = "Bienvenue sur NG-Fields!";
    String body = templateEngine.process("welcome-email", Map.of(
      "username", username,
      "loginUrl", "https://ng-fields.ng-stars.tg/login"
    ));
    
    try {
      retryTemplate.execute(context -> {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("noreply@ng-stars.tg");
        
        mailSender.send(message);
        log.info("Welcome email sent to {}", email);
        return null;
      });
    } catch (Exception e) {
      log.error("Failed to send welcome email to {}", email, e);
      throw new EmailServiceException("ERR_EMAIL_SEND_FAILED", e.getMessage());
    }
  }
  
  public void sendInterventionAssignedEmail(
      String technicianEmail,
      String technicianName,
      Intervention intervention) {
    
    String subject = "Nouvelle intervention assignée: " + intervention.getReference();
    String body = templateEngine.process("intervention-assigned", Map.of(
      "technicianName", technicianName,
      "interventionRef", intervention.getReference(),
      "clientName", intervention.getClientName(),
      "scheduledStart", intervention.getScheduledStart(),
      "dashboardUrl", "https://ng-fields.ng-stars.tg/interventions/" + intervention.getId()
    ));
    
    sendEmailWithRetry(technicianEmail, subject, body);
  }
  
  public void sendInterventionCompletedEmail(
      String clientEmail,
      String clientName,
      Intervention intervention) {
    
    String subject = "Intervention complétée: " + intervention.getReference();
    String body = templateEngine.process("intervention-completed", Map.of(
      "clientName", clientName,
      "interventionRef", intervention.getReference(),
      "completedDate", LocalDate.now(),
      "portalUrl", "https://ng-fields.ng-stars.tg/my-interventions"
    ));
    
    sendEmailWithRetry(clientEmail, subject, body);
  }
  
  private void sendEmailWithRetry(String email, String subject, String body) {
    try {
      retryTemplate.execute(context -> {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("noreply@ng-stars.tg");
        mailSender.send(message);
        return null;
      });
    } catch (Exception e) {
      log.error("Failed to send email to {}", email, e);
      throw new EmailServiceException("ERR_EMAIL_SEND_FAILED", e.getMessage());
    }
  }
}
```

#### 2. Créer templates email Thymeleaf (1.5h)
**Créer:** `Backend/notification-service/src/main/resources/templates/`

```html
<!-- welcome-email.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <style>
    body { font-family: Arial, sans-serif; color: #333; }
    .header { background-color: #4CAF50; color: white; padding: 20px; }
    .content { padding: 20px; }
    .button { 
      background-color: #4CAF50; 
      color: white; 
      padding: 10px 20px; 
      text-decoration: none; 
      border-radius: 5px; 
    }
  </style>
</head>
<body>
  <div class="header">
    <h1>Bienvenue sur NG-Fields!</h1>
  </div>
  <div class="content">
    <p>Bonjour <strong th:text="${username}"></strong>,</p>
    
    <p>Votre compte NG-Fields a été créé avec succès.</p>
    
    <p>
      <a th:href="${loginUrl}" class="button">Se connecter</a>
    </p>
    
    <p>Si vous n'avez pas demandé cela, ignorez ce message.</p>
    
    <p>Cordialement,<br>L'équipe NG-Fields</p>
  </div>
</body>
</html>

<!-- intervention-assigned.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head><meta charset="UTF-8"></head>
<body>
  <h2>Nouvelle intervention assignée</h2>
  <p>Bonjour <strong th:text="${technicianName}"></strong>,</p>
  
  <p>Vous avez une nouvelle intervention assignée:</p>
  
  <table>
    <tr>
      <td><strong>Référence:</strong></td>
      <td th:text="${interventionRef}"></td>
    </tr>
    <tr>
      <td><strong>Client:</strong></td>
      <td th:text="${clientName}"></td>
    </tr>
    <tr>
      <td><strong>Date prévue:</strong></td>
      <td th:text="${#calendars.format(scheduledStart, 'dd/MM/yyyy HH:mm')}"></td>
    </tr>
  </table>
  
  <p>
    <a th:href="${dashboardUrl}">Voir l'intervention</a>
  </p>
</body>
</html>
```

#### 3. Configuration Spring Mail (1h)
**Ajouter à `notification-service/pom.xml`:**
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

**Configuration `application.yml`:**
```yaml
spring:
  mail:
    host: ${MAIL_HOST:smtp.gmail.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000

app:
  notification:
    from-address: noreply@ng-stars.tg
    retry:
      max-attempts: 3
      backoff-delay-ms: 1000
```

#### 4. Ajouter retry logic (1h)
**Créer:** `Backend/notification-service/src/main/java/tg/ngstars/notification/config/RetryConfig.java`

```java
@Configuration
public class RetryConfig {
  
  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();
    
    FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
    backOffPolicy.setBackOffPeriod(1000);  // 1 second
    retryTemplate.setBackOffPolicy(backOffPolicy);
    
    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(3);
    retryTemplate.setRetryPolicy(retryPolicy);
    
    return retryTemplate;
  }
}
```

#### 5. Créer tests email (1h)
```java
@SpringBootTest
class EmailServiceTest {
  
  @MockBean
  private JavaMailSender mailSender;
  
  @Autowired
  private EmailService emailService;
  
  @Test
  void testSendWelcomeEmail() {
    emailService.sendWelcomeEmail("test@example.com", "John");
    
    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
  }
  
  @Test
  void testRetryOnFailure() {
    doThrow(new RuntimeException("Connection failed"))
      .doNothing()
      .when(mailSender).send(any(SimpleMailMessage.class));
    
    emailService.sendWelcomeEmail("test@example.com", "John");
    
    // Doit retry et réussir à la 2e tentative
    verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
  }
}
```

---

## Jour 5 (vendredi)
### 🎯 Tema: Tests + Documentation + Review

**Responsable:** QA + Tech Lead  
**Durée:** 8h

#### 1. Remplir Security.md (1.5h)
**Utiliser template:** `NG-Fields_Templates_Documentation_Backend.md` section 1.4

Contenu clé:
- OAuth2 flow diagram
- JWT claims
- Role mapping
- Error codes
- Test commands

#### 2. Remplir Database.md (1.5h)

Contenu:
- ERD diagram (mermaid)
- Flyway migration process
- Schema per service
- Query examples

#### 3. Remplir TESTING.md (1.5h)

Contenu:
- Unit vs intégration
- TDD approach
- Coverage goals (80%)
- Test examples (copy from code)

#### 4. Ajouter 20+ tests (2h)
```bash
# Distributed: chaque dev ajoute tests pour son service
./mvnw test
# 40 → 60+ tests
```

#### 5. Mettre à jour CI/CD (1h)

Push à main → GitHub Actions:
```
✅ Build
✅ Test (60+ tests)
✅ Coverage upload (codecov.io)
✅ Slack notification
```

---

# Semaine 2️⃣ : Complétude services

## Jour 6 (lundi) - report-service
### Implémenter analytics queries (4h)

```java
// Interventions par technicien
SELECT u.id, u.preferred_username, COUNT(*) as count,
  AVG(EXTRACT(EPOCH FROM (i.end_date - i.start_date))) as avg_duration_sec,
  AVG(i.rating) as avg_rating
FROM intervention i
JOIN "user" u ON i.technician_id = u.id
WHERE i.status = 'COMPLETED'
GROUP BY u.id
ORDER BY count DESC;

// Revenue par client
SELECT c.id, c.name, SUM(i.billing_amount) as total
FROM client c
LEFT JOIN intervention i ON c.id = i.client_id
GROUP BY c.id
ORDER BY total DESC;
```

### Générer PDF rapports (2h)

Utiliser OpenPDF + templates

### Tests (1h)

8 tests report-service

---

## Jour 7 (mardi) - auth-service completion
### Password reset flow (3h)
### 2FA (Totp) (2h)
### Tests (2h)

---

## Jour 8 (mercredi) - client-service refactor
### Contacts as separate JPA entity (2h)
### Soft delete (1h)
### Versioning audit (2h)
### Tests (2h)

---

## Jour 9 (jeudi) - Coverage push
### Tous services: 80%+ coverage (8h)

```bash
./mvnw clean verify
# → Jacoco report

# Coverage par service:
# gateway: 85%
# auth: 82%
# client: 81%
# intervention: 80%
# media: 78%
# notification: 75%
# report: 76%
# shared-lib: 70%
```

---

## Jour 10 (vendredi) - Doc completion + Deployment
### Docs 100% (4h)
### Deployment guide (2h)
### QA sign-off (2h)

---

# 📊 Metriques finales (Day 10)

| Métrique | Jour 1 | Jour 10 | Cible |
|----------|--------|---------|-------|
| Services prod-ready | 3/8 | 8/8 | ✅ |
| Couverture tests | 42% | 80% | ✅ |
| Docs pages | 1 | 13 | ✅ |
| CI/CD | 0% | 100% | ✅ |
| Observabilité | 0% | 80% | ✅ |
| Architecture score | 4.6/10 | 7.5/10 | ✅ |

---

# 🚀 Déploiement après week 2

### Pré-prod (Day 11-12)
- Deploy Docker to staging
- Load testing
- Security audit

### Prod (Day 13)
- Blue-green deployment
- Monitoring alert setup
- Incident response plan

---

# 📋 Checklist Semaine 1

- [ ] `.env.example` créé
- [ ] shared-lib base classes
- [ ] docs/backend structure
- [ ] README + SETUP-LOCAL
- [ ] Sentry in all services
- [ ] GlobalExceptionHandler
- [ ] LoggingInterceptor
- [ ] Actuator endpoints
- [ ] 10 integration tests
- [ ] GitHub Actions workflow
- [ ] SyncController 50%
- [ ] Image thumbnails
- [ ] Cleanup policy
- [ ] File validation
- [ ] EmailService complete
- [ ] Email templates
- [ ] Security.md complete
- [ ] Database.md complete
- [ ] TESTING.md complete
- [ ] 60+ tests total

---

> **En cas de blocage:** Slack tech-lead (Nom Lead) ou ouvrir issue GitHub `label:blocked`

