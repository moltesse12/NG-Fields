# 03 — Sécuriser Spring Boot avec Keycloak (OAuth2 Resource Server)

**Objectif :** Spring Boot valide les JWT Keycloak, mappe les rôles, protège les endpoints
**Temps estimé :** 20 minutes
**Dépend de :** [02-configure-realm.md](02-configure-realm.md) (realm `ng-fields` + client `ng-fields-backend` créés)

---

## Prérequis

- [x] Keycloak tourne sur `http://localhost:8080` ✅ 2026-06-02
- [x] Realm `ng-fields` créé avec clients et rôles ✅ 2026-06-02
- [x] Le projet Spring Boot Initializr existe dans `apps/ng-fields-api/` ✅ 2026-06-02
- [x] Java 25+ (vérifier avec `java -version`) ✅ 2026-06-02
- [x] `mvnw.cmd` utilisable (si besoin : `cd apps\ng-fields-api; .\mvnw.cmd`) ✅ 2026-06-02

## Étapes

### Étape 1 : Vérifier les dépendances dans `pom.xml`

Ouvrir `apps/ng-fields-api/pom.xml`. Vérifier que ces dépendances sont présentes (Spring Initializr les a déjà générées) :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
```

Ajouter **keycloak-admin-client** (pour la création de comptes via Keycloak Admin API) :

```xml
<dependency>
    <groupId>org.keycloak</groupId>
    <artifactId>keycloak-admin-client</artifactId>
    <version>26.6.4</version>
</dependency>
```

> Spring Boot 4.1.0 utilise `spring-boot-starter-webmvc` (renommage de `spring-boot-starter-web`).
> Le pom généré par Initializr contient aussi `spring-boot-h2console` (obsolète depuis le passage à PostgreSQL) et des dépendances de test supplémentaires.

### Étape 2 : Créer `application.yaml`

Le fichier généré par Initializr est déjà `src/main/resources/application.yaml`. Le remplacer par :

**Fichier :** `src/main/resources/application.yaml`

```yaml
spring:
  application:
    name: ng-fields-api

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/ng-fields
          audiences: ng-fields-backend

  datasource:
    url: jdbc:postgresql://localhost:5432/ng_fields
    driver-class-name: org.postgresql.Driver
    username: ng_fields_user
    password: ${DB_PASSWORD:Pg_ng-fields1234}

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

  jackson:
    default-property-inclusion: non_null
    serialization:
      write-dates-as-timestamps: false

server:
  port: 8081

keycloak:
  auth-server-url: http://localhost:8080
  realm: ng-fields
  admin-client-id: ng-fields-backend
  admin-client-secret: ${KEYCLOAK_CLIENT_SECRET:}

logging:
  level:
    org.springframework.security: INFO
    org.keycloak: INFO
```

> **Note :** La base de données cible est PostgreSQL (`ng_fields`). L'utilisateur et le mot de passe sont paramétrables via la variable d'environnement `DB_PASSWORD`.

### Étape 3 : Créer les packages Java

```powershell
cd F:\03_Pro_IT\07_Clients\NG-STARs\06_PROJETS\Projet_NG-Fields\apps\ng-fields-api

# Créer la hiérarchie de dossiers
New-Item -ItemType Directory -Path "src\main\java\tg\ngstars\ng_fields_api" -Force
New-Item -ItemType Directory -Path "src\main\java\tg\ngstars\ng_fields_api\config" -Force
New-Item -ItemType Directory -Path "src\main\java\tg\ngstars\ng_fields_api\controller" -Force
New-Item -ItemType Directory -Path "src\main\java\tg\ngstars\ng_fields_api\exception" -Force
New-Item -ItemType Directory -Path "src\test\java\tg\ngstars\ng_fields_api" -Force
```

### Étape 4 : Créer SecurityConfig

**Fichier :** `src/main/java/tg/ngstars/ng_fields_api/config/SecurityConfig.java`

```java
package tg.ngstars.ng_fields_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(new KeycloakRoleConverter())
                )
            );
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:4200",
            "http://localhost:5173",
            "http://localhost"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("X-Total-Count"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
```

### Étape 5 : Créer KeycloakRoleConverter

**Fichier :** `src/main/java/tg/ngstars/ng_fields_api/config/KeycloakRoleConverter.java`

```java
package tg.ngstars.ng_fields_api.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;

public class KeycloakRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");
            if (roles != null) {
                roles.stream()
                    .filter(role -> !role.equals("offline_access") && !role.equals("uma_authorization"))
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .forEach(authorities::add);
            }
        }

        return new JwtAuthenticationToken(jwt, authorities, jwt.getClaimAsString("preferred_username"));
    }

}
```

### Étape 6 : Créer GlobalExceptionHandler

**Fichier :** `src/main/java/tg/ngstars/ng_fields_api/exception/GlobalExceptionHandler.java`

```java
package tg.ngstars.ng_fields_api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(JwtException.class)
    public ProblemDetail handleJwtException(JwtException ex) {
        log.warn("JWT validation failed: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Token invalide ou expiré");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Accès refusé : rôle insuffisant");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur");
    }

}
```

### Étape 7 : Créer HealthController

**Fichier :** `src/main/java/tg/ngstars/ng_fields_api/controller/HealthController.java`

```java
package tg.ngstars.ng_fields_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "ng-fields-api",
            "timestamp", Instant.now().toString()
        ));
    }

}
```

### Étape 8 : Supprimer les fichiers Initializr par défaut

```powershell
Remove-Item -Recurse -Force "src\main\java\tg\ngstars\ng_fields_api\*" -Exclude "NgFieldsApiApplication.java"
Remove-Item -Recurse -Force "src\test\java\tg\ngstars\ng_fields_api\*" -Exclude "*Test.java"
```

### Étape 9 : Lancer Spring Boot

```powershell
cd F:\03_Pro_IT\07_Clients\NG-STARs\06_PROJETS\Projet_NG-Fields\apps\ng-fields-api
.\mvnw.cmd spring-boot:run
```

**Résultat attendu dans la console :**
```
2026-06-01 17:40:00,000 INFO  [tg.ngstars.ng_fields_api.NgFieldsApiApplication] (main) Started NgFieldsApiApplication in 3.5 seconds
```

### Étape 11 : Tester les endpoints

```powershell
# Test 1 : Health public
curl http://localhost:8081/api/public/health
# Réponse : {"status":"UP","service":"ng-fields-api","timestamp":"..."}

# Test 2 : API protégée SANS token → 401
curl http://localhost:8081/api/admin/users
# Réponse : {"type":"about:blank","title":"Unauthorized","status":401,...}

# Test 3 : Obtenir un token Keycloak
$tokenResponse = curl -X POST http://localhost:8080/realms/ng-fields/protocol/openid-connect/token `
  -H "Content-Type: application/x-www-form-urlencoded" `
  -d "client_id=ng-fields-backend" `
  -d "client_secret=LE_SECRET_DU_CLIENT" `
  -d "grant_type=client_credentials" | ConvertFrom-Json
$accessToken = $tokenResponse.access_token

# Test 4 : API protégée AVEC token → 403 (role insuffisant) ou 200
curl -H "Authorization: Bearer $accessToken" http://localhost:8081/api/admin/users
```

---

## Structure finale des fichiers

```
apps/ng-fields-api/src/
├── main/
│   ├── java/tg/ngstars/ng_fields_api/
│   │   ├── NgFieldsApiApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   └── KeycloakRoleConverter.java
│   │   ├── controller/
│   │   │   └── HealthController.java
│   │   └── exception/
│   │       └── GlobalExceptionHandler.java
│   └── resources/
│       └── application.yaml
└── test/
    └── java/tg/ngstars/ng_fields_api/
```

## Tests de validation

| Test | Commande | Résultat attendu |
|------|----------|------------------|
| Health check | `curl localhost:8081/api/public/health` | `200 {"status":"UP"}` |
| Sans token | `curl localhost:8081/api/admin/users` | `401 Unauthorized` |
| Avec token | `curl -H "Authorization: Bearer \$TOKEN" localhost:8081/api/admin/users` | `200` ou `403` |
| Mauvais token | `curl -H "Authorization: Bearer invalid" localhost:8081/api/admin/users` | `401` |

## Prochaine étape

Spring Boot sécurise les endpoints. Passer à la création de compte :

➡️ [04-user-registration.md](04-user-registration.md) 🔴 **PRIORITAIRE**
