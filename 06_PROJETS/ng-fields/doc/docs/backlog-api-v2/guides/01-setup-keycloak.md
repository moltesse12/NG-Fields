# 01 — Installer et lancer Keycloak (sans Docker)

**Objectif :** Keycloak 26.6.4 tourne sur `http://localhost:8080`
**Temps estimé :** 10 minutes
**Dépend de :** Rien (Java 21+ inclus dans la distribution)

---

## Prérequis

- Java 21+ (vérifier avec `java -version`)
- 2 Go de RAM libre
- Connexion Internet

## Étapes

### 1. Télécharger Keycloak 26.6.4

```powershell
cd F:\03_Pro_IT\07_Clients\NG-STARs\06_PROJETS\Projet_NG-Fields\apps

# Télécharger Keycloak ZIP à la racine du projet
Invoke-WebRequest -Uri "https://github.com/keycloak/keycloak/releases/download/26.6.4/keycloak-26.6.4.zip" `
  -OutFile "apps\keycloak-26.6.4.zip"

### 2. Extraire l'archive

```powershell
# Avec l'explorateur : clic droit sur apps/keycloak-26.6.4.zip → Extraire tout → apps/
# Ou en ligne de commande :
Expand-Archive -Path "apps\keycloak-26.6.4.zip" -DestinationPath "apps\"
```

Vérifier la structure :
```
apps/
├── keycloak-26.6.4/         ← dossier extrait dans apps/
│   ├── bin/
│   │   ├── kc.bat          ← Lanceur Windows
│   │   └── kc.sh           ← Lanceur Linux/Mac
│   ├── conf/
│   ├── data/
│   ├── providers/
│   └── ...
├── ng-fields-api/            ← projet Spring Boot
├── docs/
└── ...
```

### 3. Créer l'utilisateur admin (AVANT le premier démarrage)

Dans Keycloak 26+, l'admin se définit via des variables d'environnement **avant** de lancer le serveur.

**Dans le même terminal PowerShell :**

```powershell
$env:KEYCLOAK_ADMIN="Master"
$env:KEYCLOAK_ADMIN_PASSWORD="Master1234!"
```

> Ces variables ne sont valables que pour la session PowerShell. À redéfinir à chaque nouveau terminal.

### 4. Démarrer Keycloak en mode développement

```powershell
cd F:\03_Pro_IT\07_Clients\NG-STARs\06_PROJETS\Projet_NG-Fields\apps\keycloak-26.6.4\bin
.\kc.bat start-dev --http-port=8080
```

**Résultat attendu dans la console :**
```
2026-06-01 17:30:00,000 INFO  [io.quarkus] (main) Keycloak 26.6.4 on JVM (powered by Quarkus ...)
2026-06-01 17:30:02,000 INFO  [org.keycloak.services] (main) Added user 'admin' to realm 'master'
2026-06-01 17:30:02,500 INFO  [io.quarkus] (main) Installed features: [cdi, ...]
2026-06-01 17:30:03,000 INFO  [org.keycloak.services] (main) Profile dev on port 8080
```

> **Important :** La console doit rester ouverte. Ne pas fermer la fenêtre.

### 5. Vérifier que tout fonctionne

```powershell
# Admin Console
start http://localhost:8080/admin
```

Se connecter avec `admin` / `admin123`.

---

## Structure finale attendue

| Service | URL | Credentials |
|---------|-----|-------------|
| Admin Console | `http://localhost:8080/admin` | `admin` / `admin123` |
| Port trafic | `8080` | — |

> **Note :** En mode `start-dev`, Keycloak 26.6.4 n'expose pas de port management 9000
> ni d'endpoint `/health` séparé. Le health check se fait via `GET /realms/master`
> ou en vérifiant que la Admin Console répond.

## Dépannage

| Symptôme | Cause | Solution |
|----------|-------|----------|
| `Java not found` | Java 21+ pas installé | Installer OpenJDK 21+ et vérifier `java -version` |
| `Port 8080 already in use` | Un autre service utilise le port | `netstat -ano \| Select-String ":8080"` → `Stop-Process -Id <PID> -Force` |
| `Connection refused` | Keycloak pas encore prêt | Attendre 10-15 secondes, le temps que Quarkus démarre |
| La console s'arrête toute seule | Pas assez de RAM | Fermer les applis lourdes (IDE, navigateur) |
| `KEYCLOAK_ADMIN not set` | Variables d'env absentes au démarrage | Définir `$env:KEYCLOAK_ADMIN="admin"` puis relancer `kc.bat` |
| Page d'accueil sans Admin Console | Admin non créé | Arrêter Keycloak (Ctrl+C), définir les variables d'env, relancer |

## Arrêter Keycloak

Dans la console Keycloak : `Ctrl+C`.

## Prochaine étape

Keycloak tourne. Passer au guide suivant pour configurer le realm :

➡️ [02-configure-realm.md](02-configure-realm.md)
