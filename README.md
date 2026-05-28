# Cafheg — Gestion des Allocations Familiales

Projet étudiant réalisé dans le cadre du cours **Pratique de Développement** à la HES-SO (HEG Arc).

Application Spring Boot de gestion des allocations familiales, exposant une API REST pour :
- Déterminer quel parent a droit aux allocations selon le schéma LAFam (art. 7 OAFam)
- Gérer les allocataires (consultation, suppression, modification)
- Exporter des récapitulatifs PDF des versements et allocations

## Groupe

| Nom | Prénom |
|-----|--------|
| Barthoulot | Loïc |
| Nanton | Kylian |
| Bressoud | Jérémie |
| Hummel | Ysias |

## Exercices implémentés

| Exercice | Contenu |
|----------|---------|
| Ex. 1 | Tests unitaires 100% sur `AllocationService#getParentDroitAllocation`, refactoring `Map<String,Object>` → `ParentDroitAllocationRequest`, implémentation schéma LAFam |
| Ex. 2 | Suppression d'allocataire (contrainte versements), modification nom/prénom, API REST |
| Ex. 4 | Remplacement `System.out.println` par SLF4J/Logback, 3 appenders (console DEBUG, `err.log` ERROR, `cafheg_{date}.log` INFO) |
| Ex. 5 | Répertoire `src/integration-test`, tests d'intégration DBUnit + Testcontainers (suppression + modification) |

## Prérequis

- **Java 17+** (Java 25 pour le Dockerfile)
- **Docker Desktop** (ou Docker + Docker Compose)
- **Maven 3.8+** (inclus via `mvnw`)

## Démarrage

### 1. Ouvrir le projet

Dans IntelliJ IDEA : **File → Open** → sélectionner le dossier `cafheg`.

Configurer le SDK Java 25 dans **File → Project Structure → Project**.

### 2. Base de données PostgreSQL

La base de données démarre **automatiquement** via Spring Boot Docker Compose au lancement de l'application.

Démarrage manuel (optionnel) :
```bash
docker compose up -d
```

Paramètres : `cafheg` / `cafheg` / `secret` sur le port `5432`.

### 3. Lancer l'application

**IntelliJ** : bouton vert sur `CafhegApplication.java`

**Terminal** :
```bash
./mvnw clean spring-boot:run        # Linux/Mac
mvnw.cmd clean spring-boot:run      # Windows
```

### 4. Accès

| Ressource | URL |
|-----------|-----|
| API REST | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/api/swagger-ui/index.html |

## Lancer les tests

**Tests unitaires uniquement :**
```bash
./mvnw test
```

**Tests unitaires + tests d'intégration** (nécessite Docker) :
```bash
./mvnw verify
```

## Structure du projet

```
src/
├── main/
│   ├── java/ch/hearc/cafheg/
│   │   ├── domain/          # Logique métier (AllocationService, VersementService)
│   │   └── infrastructure/  # Persistance JDBC, API REST, export PDF
│   └── resources/
│       ├── db/migration/    # Scripts Flyway (schéma + données)
│       └── logback-spring.xml
├── test/                    # Tests unitaires (Mockito)
└── integration-test/        # Tests d'intégration (DBUnit + Testcontainers)
```

## Migrations de base de données

Gérées automatiquement par Flyway au démarrage :
- `src/main/resources/db/migration/ddl/` — schéma
- `src/main/resources/db/migration/dml/` — données initiales

## Dépannage

- **Port 8080 occupé** : modifier `server.port` dans `application.yaml`
- **Connexion DB échouée** : vérifier que Docker est lancé et que les variables d'environnement sont correctes
- **Maven non synchronisé** : clic droit sur `pom.xml` → **Maven → Reload projects**
