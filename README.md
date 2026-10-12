# Medilabo Solutions

## Application de suivi médical et d'évaluation du risque de diabète

Medilabo Solutions est une application web développée dans le cadre du projet 9 du parcours de développement Java d'OpenClassrooms.

Elle permet aux professionnels de santé de consulter les dossiers des patients, de gérer leurs notes médicales et d'évaluer leur niveau de risque de développer un diabète de type 2.

L'application repose sur une architecture en microservices développée avec Java 17 et Spring Boot.

---

## Fonctionnalités

### Gestion des patients

- Consulter la liste des patients.
- Consulter les informations détaillées d'un patient.
- Ajouter un nouveau patient.
- Modifier les informations d'un patient.

Chaque dossier patient contient les informations suivantes :

- Prénom et nom.
- Date de naissance.
- Sexe.
- Adresse (facultative).
- Numéro de téléphone (facultatif).

### Gestion des notes médicales

- Consulter l'historique médical d'un patient.
- Ajouter une nouvelle note médicale.

Les notes médicales sont stockées dans MongoDB et associées à leur patient.

### Évaluation du risque de diabète

L'application calcule automatiquement le niveau de risque de diabète d'un patient en fonction de trois critères :

- Son âge.
- Son sexe.
- Les termes médicaux déclencheurs présents dans ses notes.

L'analyse des notes repose sur des expressions régulières (Regex), permettant de reconnaître différentes variantes grammaticales des termes médicaux.

Quatre niveaux de risque sont possibles :

| Niveau | Description |
|---|---|
| `NONE` | Aucun risque |
| `BORDERLINE` | Risque limité |
| `IN_DANGER` | Danger |
| `EARLY_ONSET` | Apparition précoce |

---

## Architecture technique

L'application est organisée autour de cinq microservices Spring Boot.

| Microservice | Port | Responsabilité |
|---|---|---|
| `front-service` | 8081 | Interface utilisateur développée avec Thymeleaf |
| `gateway-service` | 8080 | Point d'entrée des API et authentification JWT |
| `patient-service` | 8082 | Gestion des dossiers patients |
| `note-service` | 8083 | Gestion des notes médicales |
| `assessment-service` | 8084 | Calcul du risque de diabète |

### Bases de données

Deux bases de données sont utilisées :

**MySQL 8.0**
- Stockage des informations des patients.
- Accès aux données avec Spring Data JPA et Hibernate.

**MongoDB 7**
- Stockage des notes médicales.
- Accès aux données avec Spring Data MongoDB.

### Communication entre services

Les microservices communiquent à travers des API REST.

Le `gateway-service` centralise l'accès aux API et assure la gestion de l'authentification.

Le `assessment-service` récupère les informations des patients et leurs notes médicales pour calculer le niveau de risque.

L'interface web communique avec les services métier par l'intermédiaire du Gateway.

### Conteneurisation

L'application est conteneurisée avec Docker et orchestrée avec Docker Compose.
L'ensemble des services est défini dans le fichier compose.yaml, situé à la racine du projet.

Sept conteneurs sont utilisés :
- Cinq conteneurs pour les microservices.
- Un conteneur MySQL.
- Un conteneur MongoDB.

Les bases de données utilisent des volumes Docker pour assurer la persistance des données.

---

## Technologies utilisées

### Back-end

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Data MongoDB
- Spring Security
- Spring Cloud Gateway
- Hibernate
- Maven

### Front-end

- Thymeleaf
- HTML
- CSS

### Bases de données

- MySQL 8.0
- MongoDB 7

### Sécurité

- Spring Security
- Authentification JWT
- Authentification HTTP Basic pour certaines communications internes

### Tests

- JUnit 5
- Mockito
- Maven Surefire

### Déploiement

- Docker
- Docker Compose

---

## Prérequis

Pour installer et exécuter l'application, les outils suivants sont nécessaires :

- Java 17
- Maven
- Docker et Docker Compose
- Git

MySQL et MongoDB sont exécutés dans des conteneurs Docker.

Il n'est donc pas nécessaire de les installer directement sur l'ordinateur.

---

## Installation

### 1. Cloner le dépôt

```bash
git clone <https://github.com/DenisSanVicente/medilabo-solutions/tree/dev>
cd medilabo-solutions
```

### 2. Configurer les variables d'environnement

À la racine du projet, créer un fichier `.env` à partir du fichier `.env.example`.

Sous Linux ou macOS :

```bash
cp .env.example .env
```

Sous Windows PowerShell :

```powershell
Copy-Item .env.example .env
```

Ouvrir ensuite le fichier `.env` et renseigner une clé secrète JWT.

Exemple :

```dotenv
JWT_SECRET=REMPLACER_PAR_UNE_CLE_SECRETE_LONGUE_ET_ALEATOIRE
```

La clé doit respecter les exigences de longueur et de format de la configuration JWT du projet.

**Important :** le fichier `.env` contient des informations sensibles et ne doit pas être ajouté au dépôt Git.

### 3. Compiler les microservices

Chaque microservice dispose de son propre fichier `pom.xml`.

Depuis la racine du projet :

```bash
cd patient-service
mvn clean package
cd ..

cd note-service
mvn clean package
cd ..

cd assessment-service
mvn clean package
cd ..

cd gateway-service
mvn clean package
cd ..

cd front-service
mvn clean package
cd ..
```

Cette étape génère les fichiers JAR nécessaires à la construction des images Docker.

### 4. Démarrer les conteneurs

Depuis la racine du projet :

```bash
docker compose up -d --build
```

Docker Compose construit et démarre les sept conteneurs de l'application.

Le premier démarrage peut prendre plusieurs minutes, notamment pour l'initialisation des bases de données.

### 5. Vérifier le démarrage

```bash
docker compose ps
```

Pour consulter les journaux des services :

```bash
docker compose logs -f
```

### 6. Accéder à l'application

Une fois les services démarrés, ouvrir un navigateur à l'adresse :

http://localhost:8081

L'interface web est accessible sur le port `8081`.

Le Gateway est accessible sur le port `8080`.

---

## Authentification

L'application utilise Spring Security et une authentification par JWT.

Un compte de démonstration est configuré en mémoire pour permettre de tester l'application.

**Identifiants de démonstration :**

| Champ | Valeur |
|---|---|
| Identifiant | `user` |
| Mot de passe | `password` |
| Rôle | `USER` |

Ces identifiants sont destinés exclusivement à un environnement de développement ou de démonstration.

Ils ne doivent pas être utilisés en production.

---

## Données de démonstration

L'application dispose de données de test correspondant aux quatre patients fournis dans le cahier des charges.

### Initialisation des patients

Le microservice `patient-service` utilise un script SQL pour insérer les quatre patients dans MySQL.

L'initialisation est configurée avec Spring Boot et Hibernate.

Les instructions SQL vérifient l'absence de chaque patient avant son insertion afin d'éviter les doublons.

### Initialisation des notes médicales

Le microservice `note-service` utilise une classe `DataInitializer` reposant sur `CommandLineRunner`.

Au démarrage, si la collection MongoDB est vide, neuf notes médicales sont automatiquement insérées.

### Patients de démonstration

| Identifiant attendu | Patient | Notes médicales | Risque attendu |
|---|---|---|---|
| 1 | TestNone | 1 | Aucun risque |
| 2 | TestBorderline | 2 | Risque limité |
| 3 | TestInDanger | 2 | Danger |
| 4 | TestEarlyOnset | 4 | Apparition précoce |

**Remarque :** les notes de démonstration utilisent les identifiants patients `1` à `4`. Cette correspondance suppose une base MySQL initialement vide, dans laquelle les quatre patients sont créés dans l'ordre prévu.

---

## Tests

Les tests automatisés utilisent JUnit 5 et Mockito.

Ils permettent de vérifier notamment :

- Les règles métier.
- Les seuils de risque selon l'âge et le sexe.
- La reconnaissance des termes médicaux.
- Les variantes grammaticales grâce aux expressions régulières.
- La gestion des accents et des majuscules.
- La prévention des correspondances incorrectes.
- Les quatre scénarios officiels du cahier des charges.

### Exécuter les tests

Depuis le dossier du microservice concerné :

```bash
mvn test
```

Par exemple :

```bash
cd assessment-service
mvn clean test
```

### Résultats des tests

Lors de la dernière vérification du microservice `assessment-service` :

```text
Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Les quatre niveaux de risque ont également été vérifiés depuis l'interface web avec les données officielles.

---

## Persistance des données

Deux volumes Docker assurent la persistance des données :

- `mysql-data` : informations des patients.
- `mongo-data` : notes médicales.

### Arrêter l'application

```bash
docker compose down
```

Cette commande arrête et supprime les conteneurs, mais conserve les volumes et les données.

### Redémarrer l'application

```bash
docker compose up -d
```

### Supprimer les conteneurs et les données

```bash
docker compose down -v
```

**Attention :** cette dernière commande supprime également les volumes de données associés au projet.

---

## Green Code et éco-conception

Le projet intègre une réflexion sur les principes du Green Code et de l'éco-conception logicielle.

L'objectif est d'identifier les possibilités de réduction de la consommation de ressources et d'amélioration de l'efficacité des traitements.

### Gestion de la mémoire

Java dispose d'un Garbage Collector permettant de récupérer automatiquement la mémoire occupée par les objets devenus inutilisés.

Ce mécanisme facilite la gestion de la mémoire, sans garantir à lui seul une consommation minimale des ressources.

### Traitement des données

L'évaluation du risque repose sur la recherche de termes médicaux dans les notes des patients.

L'utilisation d'expressions régulières permet de centraliser les règles de reconnaissance des termes et de leurs variantes.

### Pistes d'amélioration

Plusieurs optimisations pourraient être envisagées :

- Réduire les appels réseau inutiles entre microservices.
- Optimiser les requêtes MySQL et MongoDB.
- Précompiler les expressions régulières pour éviter de les recompiler à chaque analyse.
- Adapter les ressources allouées aux conteneurs Docker.
- Mettre en place des mesures de consommation des ressources afin d'identifier les traitements les plus coûteux.

Ces pistes n'ont pas fait l'objet d'une évaluation énergétique chiffrée dans le cadre de ce projet.

---

## Limites et évolutions possibles

Plusieurs améliorations pourraient être apportées à l'application :

- Externaliser les identifiants et les mots de passe actuellement définis dans la configuration.
- Renforcer la gestion des utilisateurs et des rôles.
- Améliorer l'initialisation des données de démonstration pour éviter toute dépendance à des identifiants fixes.
- Étendre la couverture des tests automatisés.
- Optimiser les performances des échanges entre microservices.

---

## Contexte du projet

Medilabo Solutions a été développé dans le cadre du projet 9 de la formation Java d'OpenClassrooms.

Ce projet a permis de mettre en pratique :

- La conception d'une architecture en microservices.
- Le développement d'API REST avec Spring Boot.
- La gestion de bases de données relationnelles et NoSQL.
- La sécurisation des applications avec Spring Security et JWT.
- L'implémentation de règles métier.
- Les tests unitaires et fonctionnels.
- La conteneurisation avec Docker.

---

## Auteur

Projet réalisé par **Denis San Vicente** dans le cadre de sa formation de développeur Java.