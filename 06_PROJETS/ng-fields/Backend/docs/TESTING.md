# Testing

## État actuel

Les tests unitaires et d'intégration sont à implémenter. Les tests existants ont été supprimés et seront réécrits par l'équipe.

La vérification fonctionnelle est assurée par :
- **Collection Postman** (`Doc/docs/tests/postman-collection.json`) — 32 requêtes couvrant auth, clients, media
- **Tests manuels** via Swagger UI (`http://localhost:8080/swagger-ui.html`)

## Running Build

```bash
# All services
cd Backend
mvn clean install

# Single service
.\mvnw.cmd -f "..\pom.xml" install -pl intervention-service -am
```

## Planifié

- Tests unitaires avec JUnit 5 + Mockito
- Tests d'intégration avec Testcontainers (PostgreSQL en mémoire)
- Tests de sécurité (rôles, permissions)
- Tests bout-en-bout via le gateway
