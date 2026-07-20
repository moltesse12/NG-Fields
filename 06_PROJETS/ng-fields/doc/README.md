# NG-Fields

Digitalisation de la gestion des interventions terrain pour NG-STARs.

**Stack :** Spring Boot 4.0.6 / Java 25 + Flutter/Dart + Angular 21 / TypeScript + Tailwind CSS 4 + Vitest + Keycloak 26.6.2 + PostgreSQL/Supabase

---

## Structure

```
Backend/
├── auth-service/           Auth microservice
├── client-service/         Client microservice
├── intervention-service/   Intervention microservice
├── gateway-service/        API Gateway
├── media-service/          Media storage
├── notification-service/   Notifications
├── report-service/         Rapports PDF
└── pom.xml

Frontend/
├── ng-web/                 Dashboard Angular 21
└── templates/              Template Next.js
mobile/                     App Flutter (à créer)
Doc/                        Documentation
```

---

## Démarrage rapide

```bash
# 1. Cloner
git clone https://github.com/moltesse12/ng-fields.git
cd ng-fields

# 2. Backend
cd Backend
./mvnw spring-boot:run

# 3. Frontend (autre terminal)
cd Frontend/ng-web
npm install
npm start        # → http://localhost:4200

# 4. Tests frontend
cd Frontend/ng-web
npm test         # Vitest

# 5. Mobile (autre terminal)
cd mobile
flutter run
```

---

## Documentation API

Swagger UI : `http://localhost:8080/swagger-ui.html` (via gateway-service)

---

## Versions

| Version | Période | Livrables |
|---------|---------|-----------|
| **V0** | 1-12 juin | Auth + Core (API + Mobile) |
| **V0.1** | 13-26 juin | Mobile complet + Envoi |
| **V1** | 29 juin+ | Dashboard web + Intégrations |

---

## Licence

Propriétaire — NG-STARs
