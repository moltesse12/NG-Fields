# Cahier des charges

> **⚠️ Note :** Ce document est le cahier des charges original (référence historique). Les choix techniques ont évolué depuis : Flyway remplacé par `ddl-auto: update`, Supabase remplacé par PostgreSQL local, Docker Compose non utilisé, Resend remplace JavaMailSender. Se referer au Backlog V6.0 et à Technologies.md pour la version actualisee.

**Proposé par :** FOLLY Nelson Emmanuel

Sis au 373 Rue 53. Quartier Hédzranawoé
10 BP 10 286 Lomé – TOGO
Tél : +228 70 42 63 80
Email : contacts@ng-stars.com

## Table des matières

1. [INTRODUCTION](#introduction)
2. [PRÉSENTATION DU PROJET](#1-présentation-du-projet)
3. [PROBLÉMATIQUE](#2-problématique)
4. [OBJECTIFS ET RÉSULTATS ATTENDUS](#3-objectifs-et-résultats-attendus)
5. [ETUDE ET CRITIQUE DE L'EXISTANT](#4-etude-et-critique-de-lexistant)
6. [PROPOSITION ET CHOIX DE SOLUTION](#5-proposition-et-choix-de-solution)
7. [ÉTUDE DES TECHNOLOGIES](#6-étude-des-technologies)
8. [PLANNING PRÉVISIONNEL](#7-planning-prévisionnel)
9. [CONCLUSION](#conclusion)

---

## INTRODUCTION

Dans un secteur aussi dynamique que celui des services informatiques, l'excellence opérationnelle repose autant sur la qualité des outils internes que sur la maîtrise des solutions proposées aux clients. Dans cette optique, NG-STARs engage une démarche structurée d'amélioration continue de ses processus terrain, avec pour ambition de doter ses techniciens d'instruments à la hauteur de leur expertise et de la qualité de service qu'ils délivrent chaque jour.

C'est dans cette logique d'évolution interne que s'inscrit le présent projet de **« Développement d'une Solution de Digitalisation et centralisation de la gestion des interventions terrain (SDCGI) »**. Il vise à remplacer le support papier actuel (fiche FI-01-2025) par une solution numérique permettant aux techniciens de documenter leurs prestations en temps réel sur smartphone ou tablette : signatures électroniques, preuves photographiques géolocalisées, calcul automatique des durées et génération instantanée de rapports PDF professionnels, avec un fonctionnement garanti même en zone de faible connectivité.

Ce projet est avant tout un levier de performance et de professionnalisme : il renforce la traçabilité des interventions en temps réel, accélère la prise de décision grâce aux données en temps réel, et enrichit la qualité du service rendu aux clients par des rapports fiables et immédiatement transmissibles.

Le présent cahier des charges est rédigé par **FOLLY Nelson Emmanuel (Stagiaire)**, à destination de l'équipe projet NG-STARs et du validateur technique **David KATOH**. Il constitue le document de référence pour la phase d'étude, de choix de solution et de développement. Il a été produit dans le cadre d'un projet de développement interne et définit les besoins fonctionnels, les contraintes techniques et le cadre de réalisation attendu. Il servira de base à l'ensemble des décisions prises tout au long du cycle de développement.

---

## 1. PRÉSENTATION DU PROJET

### 1.1 Présentation de NG-STARs

NG-STARs est une entreprise togolaise de services informatiques et de conseil en transformation digitale, implantée à Lomé au Togo. Depuis sa création, elle accompagne des entreprises et organisations de toutes tailles dans la modernisation de leurs outils et de leurs processus numériques. Son positionnement repose sur l'alliance entre expertise technique de haut niveau et compréhension approfondie du contexte local africain. Elle intervient dans plusieurs domaines clés du numérique :

- Développement d'applications web et mobiles sur mesure
- Intégration de systèmes ERP pour la gestion d'entreprise
- Formation aux technologies numériques et accompagnement au changement
- Déploiement de réseaux et de solutions cloud
- Conseil en architecture informatique et transformation digitale

Forte d'une équipe de développeurs et techniciens expérimentés de niveau senior, NG-STARs accompagne ses clients dans la modernisation de leurs outils informatiques. Le projet s'inscrit dans cette dynamique, en appliquant cette expertise à ses propres processus internes.

### 1.2 Contexte du projet

La gestion des interventions techniques terrain constitue le cœur de métier opérationnel de NG-STARs. Chaque jour, des techniciens se rendent chez les clients pour réaliser des prestations de services telles que la maintenance, le dépannage, l'installation ou l'audit informatique, etc. Ces interventions doivent être rigoureusement documentées afin de garantir la traçabilité des actes réalisés et le suivi de la qualité de service.

À ce jour, l'ensemble de ce processus de documentation repose sur une fiche d'intervention papier de 3 pages (référence FI-01-2025), conçue en interne. Ce support, bien que fonctionnel dans ses grandes lignes, présente des limitations croissantes au regard des exigences actuelles de réactivité, de traçabilité et de pilotage par la donnée.

---

## 2. PROBLÉMATIQUE

À l'ère de la transformation numérique, les entreprises de services techniques sont confrontées à une exigence croissante de réactivité, de traçabilité et de fiabilité dans la gestion de leurs opérations terrain. La digitalisation des processus métier n'est plus un avantage concurrentiel optionnel, mais une condition nécessaire à la performance et à la compétitivité. Dans ce contexte, les organisations qui maintiennent des processus manuels s'exposent à des risques opérationnels, juridiques et stratégiques de plus en plus difficiles à absorber.

C'est précisément la situation dans laquelle se trouve NG-STARs. Entreprise évoluant dans un environnement technique exigeant, elle continue de s'appuyer sur un dispositif entièrement papier pour documenter ses interventions terrain. Ce choix génère aujourd'hui des défaillances aux conséquences tangibles : données incomplètes, absence de traçabilité géolocalisée, double saisie, supervision en temps réel impossible et risques juridiques liés aux signatures électronique.

Dès lors, la question centrale qui se pose est la suivante :

> **Dans quelle mesure la digitalisation du processus de gestion des interventions terrain permettrait-elle à NG-STARs de garantir une traçabilité fiable, de réduire les pertes opérationnelles et d'offrir une supervision en temps réel, y compris en environnement à faible connectivité ?**

Cette interrogation soulève trois axes complémentaires :

- Comment fiabiliser la collecte de données tout en simplifiant le travail du technicien sur le terrain ?
- Comment remplacer les supports papier par des solutions numériques offrant des garanties juridiques au moins équivalentes ?
- Comment centraliser les données dans un système unique permettant un pilotage efficace de l'activité ?

---

## 3. OBJECTIFS ET RÉSULTATS ATTENDUS

### 3.1 Objectif Général

L'objectif de ce projet est de digitaliser et de centraliser entièrement la gestion des interventions terrain de NG-STARs à travers le développement d'une application mobile et web performante. Cette solution vise à supprimer l'usage du papier, à garantir une traçabilité complète des interventions, à améliorer la réactivité du management et à fournir des données fiables et exploitables pour le pilotage ainsi que l'amélioration continue des services.

### 3.2 Objectifs Spécifiques

Les objectifs spécifiques ci-après traduisent de manière opérationnelle les finalités du projet :

- **Digitalisation complète du formulaire :** L'application doit reprendre en premiers lieu les 8 sections et tous les champs de la fiche papier FI-01-2025 sous forme numérique, structurée et guidée, sans aucune perte d'information par rapport au support actuel.
- **Calcul automatique et précis du temps sur site :** Le système doit calculer automatiquement la durée réelle de chaque intervention (heure d'arrivée – heure de départ du site) et la rendre visible immédiatement pour le technicien et le management. C'est l'exigence prioritaire exprimée par le Responsable IT.
- **Mode hors-ligne complet :** La solution doit fonctionner intégralement sans connexion réseau, couvrir toutes les zones d'intervention y compris rurales, et synchroniser automatiquement les données dès le retour de la connectivité.
- **Signatures électroniques :** Le système doit intégrer trois zones de signature tactile distinctes une pour le client sur site, une pour le technicien intervenant, et une pour le responsable hiérarchique (pouvant être apposée au retour au siège).
- **Preuves visuelles géolocalisées :** La solution doit permettre la capture de photos avant et après intervention (jusqu'à 5), avec enregistrement automatique de la position GPS du technicien à l'arrivée sur site.
- **Génération automatique de rapports PDF professionnels :** Chaque intervention validée doit générer un rapport PDF incluant le logo NG-STARs, toutes les informations de l'intervention, les photos, les trois signatures et un QR code renvoyant vers la fiche numérique.
- **Envoi multicanal immédiat :** Le rapport doit pouvoir être envoyé directement par Email depuis l'application, immédiatement après validation de l'intervention.
- **Notifications en temps réel :** Le management doit recevoir des alertes automatiques (push et email) à chaque étape clé : création d'une intervention, fin d'intervention, retard constaté par rapport à une durée seuil paramétrable.
- **Tableau de bord manager :** Un espace web dédié aux managers doit offrir une vue globale et filtrée de toutes les interventions, avec statistiques, indicateurs clés (dont le temps moyen sur site) et exports CSV/Excel.
- **Historique centralisé par client :** L'ensemble des interventions réalisées chez un client doit être consultable depuis une fiche client avec recherche avancée par date, type ou technicien.
- **Création de tickets client depuis l'application :** Le client doit pouvoir soumettre une demande d'intervention directement depuis un formulaire dédié dans l'application.

### 3.3 Résultats attendus

La solution devra fournir les résultats suivants à l'issue du projet :

- Une application utilisable sur iOS et Android, testée sur plusieurs appareils représentatifs, sans bug bloquant en conditions réelles d'utilisation.
- Un formulaire d'intervention numérique reproduisant l'ensemble des champs de la fiche d'intervention, avec validation des champs obligatoires et calcul automatique fiable de la durée d'intervention sur site.
- Un mode hors ligne complet permettant la saisie intégrale d'une intervention sans connexion réseau, avec synchronisation automatique dans un délai maximum de soixante secondes au rétablissement de la connectivité.
- Trois zones de signatures électroniques distinctes (client, technicien, responsable), correctement enregistrées et intégrées dans les rapports, avec possibilité de signature différée pour le responsable.
- La capture de photos avant et après intervention (limite de 5 par catégorie), avec enregistrement automatique de la position GPS à l'arrivée sur site.
- Un rapport PDF généré automatiquement en moins de dix secondes, incluant le logo, les données de l'intervention, les photos, les signatures, un QR code fonctionnel et un identifiant unique.
- L'envoi des rapports par email (délai inférieur à deux minutes) avec accès direct au document pour le destinataire.
- Un tableau de bord web accessible depuis les principaux navigateurs sans installation, avec filtrage avancé, exports CSV/Excel et affichage du temps moyen sur site par technicien.
- Des notifications automatiques push et email avec délai de réception inférieur à une minute, testées sur iOS et Android.
- Une base de données centralisée conforme RGPD, avec chiffrement en transit et au repos, sauvegardes quotidiennes automatiques et registre des traitements documenté.
- Un formulaire de demande d'intervention accessible au client (via lien web ou espace dédié), dont la soumission notifie automatiquement l'équipe concernée.
- Une documentation complète : guide utilisateur en français (10 pages minimum), documentation technique de l'API et guide d'installation et de maintenance.

---

## 4. ETUDE ET CRITIQUE DE L'EXISTANT

### 4.1 Etude de l'existant

#### 4.1.1 Description du support actuel

La fiche d'intervention papier FI-01-2025 est un document interne de 3 pages, conçu et utilisé exclusivement par NG-STARs pour documenter ses prestations techniques terrain. Elle constitue depuis sa mise en place le seul outil de traçabilité des interventions de l'entreprise. Sa structure a été pensée pour couvrir l'intégralité du cycle de vie d'une prestation, depuis le départ du siège jusqu'à l'archivage final, en passant par l'ensemble des étapes opérationnelles et de validation. Elle se présente comme suit :

**PHASE 1 : DÉPART (Au siège NG-STARs)**
- Le technicien est notifié d'une intervention (demande client)
- Le technicien informe son responsable hiérarchique de son départ
- Le technicien se rend au département RH pour retirer la fiche FI
- Le technicien renseigne les informations préalables (REF, Date, Numéro d'intervention ; Nom de l'entreprise cliente ; Nom de l'intervenant / Administrateur ; Service /Département ; Heure de SORTIE de la société)

**PHASE 2 : ARRIVÉE CHEZ LE CLIENT**
- Le technicien se déplace chez le client
- Le technicien note l'heure d'ARRIVÉE sur site
- Le technicien renseigne les informations CLIENT (Nom, adresse, téléphone/email, contact sur site)

**PHASE 3 : INTERVENTION**
- Le technicien relève la DESCRIPTION DU PROBLÈME
- Le technicien note l'heure de DÉBUT d'intervention puis INTERVIENT
- Le technicien donne son DIAGNOSTIC
- Le technicien renseigne le TYPE D'INTERVENTION (Maintenance, Dépannage, Installation, Mise à jour, Audit, Autre)
- Le technicien renseigne le MATÉRIEL / SYSTÈME (Marque, Modèle, Numéro de série, Localisation)
- Le technicien décrit les TRAVAUX réalisés

**PHASE 4 : CONSOMMABLES & RÉSULTAT**
- Le technicien note l'heure de FIN d'intervention (calcul manuel durée)
- Le technicien liste les CONSOMMABLES utilisés
- Le technicien renseigne le RÉSULTAT : Résolu / Partiellement / Non résolu
- Le technicien note les RECOMMANDATIONS

**PHASE 5 : VALIDATION SUR SITE & FACTURATION**
- Le technicien demande au CLIENT de SIGNER la fiche
- Le technicien renseigne FACTURATION : Facturable / Non facturable

**PHASE 6 : RETOUR AU SIÈGE**
- Le technicien note l'heure de RETOUR
- Le responsable vérifie et signe la fiche
- Le responsable recueille le FEEDBACK du client
- La fiche est ARCHIVÉE (classement physique)

**PHASE 7 : TRAITEMENT ULTÉRIEUR**
- Rapport PDF généré et envoyé par email
- Suivi des recommandations

Le document est organisé en sept sections logiques correspondant aux sept phases du processus d'intervention. Chaque section comprend des champs de saisie manuscrite, des cases à cocher et des zones de signature.

### 4.2 Critique de l'existant

La fiche FI-01-2025, bien que fonctionnelle dans sa conception, s'avère structurellement inadaptée aux exigences opérationnelles et managériales actuelles de NG-STARs. Trois axes de critique majeurs peuvent être dégagés.

**Premier axe : L'impossibilité de répondre aux exigences de traçabilité moderne.** Le support papier ne peut pas intégrer de données géolocalisées, de preuves photographiques ni de signatures numériques. Dans un contexte où les clients exigent de plus en plus de transparence et de preuves de la valeur des prestations réalisées, l'absence de ces éléments constitue un désavantage compétitif croissant pour NG-STARs.

**Deuxième axe : La rupture entre la collecte de l'information et sa mise à disposition pour la décision.** Actuellement, les données d'une intervention n'atteignent le management qu'au retour physique du technicien, soit avec un délai de 24 heures. Durant toute la durée de l'intervention, le manager est dans l'impossibilité totale de savoir où en est le technicien, quel est le résultat obtenu ni combien de temps a été passé sur site. Cette opacité est incompatible avec les exigences d'un management réactif et basé sur les données.

**Troisième axe : Le coût caché du processus papier.** Les 15 minutes perdues pour récupérer la fiche, les 10 minutes de double saisie dans le système, les 20 à 30 minutes nécessaires pour retrouver une fiche ancienne, les erreurs de calcul de durée et le risque de perte de données représentent un coût opérationnel significatif. Sur une base de 10 interventions par jour, le seul aller-retour au RH représente 2h30 de temps technicien perdu chaque semaine.

En conclusion, le processus actuel présente plusieurs limitations majeures qui entravent l'efficacité globale du service. Un passage à un système numérique centralisé et offline-first permettrait d'améliorer significativement la réactivité, la traçabilité et la qualité de service.

#### 4.2.1 Points forts du processus actuel

Avant d'envisager toute transformation, il est essentiel de reconnaître et de documenter les points forts du processus existant, afin de s'assurer que la solution retenue en conserve les qualités tout en en corrigeant les faiblesses.

- **Couverture fonctionnelle complète :** en trois pages, la fiche couvre l'intégralité du cycle d'une intervention (informations préalables, données client, diagnostic, description des travaux, matériel, consommables, résultat, recommandations et signatures).
- **Processus structuré :** Il dispose d'un processus stable et bien maîtrisé par l'ensemble des techniciens, avec une logique (départ ; arrivée ; intervention ; résultat ; validation ; retour).
- **Validations multiparties :** trois signatures distinctes (client, technicien, responsable hiérarchique) conférant une valeur de preuve et un mécanisme naturel de contrôle qualité.
- **Référencement normé (FI-01-2025)** facilitant l'identification dans les classeurs d'archivage.
- **Accessibilité universelle :** ne requiert aucune connexion réseau, aucun appareil particulier, aucune formation technique.

#### 4.2.2 Analyse des profils utilisateurs

L'analyse des utilisateurs du système met en évidence trois profils principaux, chacun ayant des usages, des besoins et des niveaux de maîtrise technologique distincts.

- **Techniciens terrain (minimum 15 utilisateurs) :** usage principal de saisie des informations liées aux interventions sur appareil mobile. Besoin d'une interface simple, rapide et intuitive, avec mode hors ligne et géolocalisation automatique. Niveau de maîtrise technologique généralement élevé.
- **Managers (minimum 10 utilisateurs) :** supervision des activités terrain via interface web. Besoin d'un tableau de bord clair, de notifications en temps réel, d'une visibilité sur le temps passé sur site et d'exports de données. Bonne aisance avec les outils bureautiques et web.
- **Administrateurs IT (minimum 10 utilisateurs) :** configuration, maintenance et administration du système. Accès complet à la gestion des comptes, droits et référentiels. Niveau de compétence technique avancé.
- **Clients (nombre variable) :** accès limité à un formulaire de saisie de demande d'intervention. Aucune authentification complexe requise (lien sécurisé par token ou portail client simplifié). Niveau de maîtrise technologique supposé faible à moyen — l'interface doit être minimaliste et guidée.

#### 4.2.3 Analyse des contraintes de connectivité

L'analyse des conditions réelles d'intervention met en évidence une forte instabilité de la qualité de la connectivité réseau selon les zones géographiques couvertes par NG-STARs :

- Siège et bureaux administratifs : connexion excellente et stable, synchronisation en temps réel possible.
- Zones urbaines (clients en centre-ville) : connectivité globalement bonne, disponible dans environ 70 % des cas. Des interruptions ponctuelles peuvent survenir.
- Zones périphériques : qualité de réseau moyenne à faible, concernant environ 30 % des interventions. Le mode hors ligne est régulièrement utilisé.
- Zones rurales : connectivité faible voire totalement absente, représentant environ 20 % des interventions. Le mode hors ligne est indispensable pour la saisie complète, les photos, signatures et la géolocalisation.

Au regard des éléments, il apparaît clairement que le mode hors ligne ne constitue pas une fonctionnalité optionnelle, mais une exigence fondamentale pour garantir la couverture de 100 % des interventions. La solution devra être conçue selon une logique **« offline-first »**.

---

## 5. PROPOSITION ET CHOIX DE SOLUTION

### 5.1 Etude de solutions

Avant de statuer sur la nature de la solution à retenir, une analyse des offres disponibles sur le marché a été conduite. Les options les plus pertinentes ont été étudiées selon les critères fonctionnels, économiques et contextuels de NG-STARs.

#### 5.1.1 Solutions SaaS du marché (BIGCHANGE)

BigChange est une solution cloud qui permet de gérer l'ensemble du cycle des interventions.

**Avantages :**
- Planification des techniciens
- Suivi en temps réel via GPS
- Gestion des ordres de travail
- Génération automatique de rapports et de devis d'intervention (PDF)
- Facturation et gestion client
- Intégration d'une application mobile utilisée par les agents sur le terrain, avec un mode hors ligne partiel.
- Dashcams IA
- Location de véhicules utilitaires
- Carte de carburants
- DAAS
- Données mobile (appel et internet) illimitées et smartphone (Tablette avec stylet et téléphone avec stylet)

**Inconvénients :**
- Coût élevé : souvent supérieur à 30 €/utilisateur/mois (soit 17 000 F CFA)
- Dépendance à Internet : même si un mode offline existe, il reste limité
- Complexité de déploiement : paramétrage avancé, formation nécessaire
- Support principalement international : peu adapté au contexte local (langue, fuseau, réactivité)
- Pas optimisé pour les environnements à faible connectivité (cas fréquent au Togo)

#### 5.1.2 Solutions formulaire en ligne (Google Forms + Drive)

Solution gratuite, simple et déployable immédiatement.

**Avantages :**
- Coût nul
- Facilité d'utilisation et d'accès immédiat

**Inconvénients :**
- Pas de signatures électroniques
- Pas de mode hors ligne
- Pas de génération automatique de PDF
- Pas de géolocalisation
- Pas de tableau de bord manager ni de notifications

#### 5.1.3 Développement sur mesure (application interne)

Cette solution consiste à développer l'application entièrement en interne, en partant des besoins précis de NG-STARs.

**Avantages :**
- 100 % adapté aux besoins et au contexte de NG-STARs
- Évolutif et propriété de NG-STARs
- Coût de licence nul, coût d'hébergement nul (Supabase free tier)
- Mode hors ligne avancé possible avec contrôle total
- Équipe interne disposant de toutes les compétences nécessaires

**Inconvénients :**
- Temps de développement initial (10 à 11 semaines)

### 5.2 Choix de la solution

#### 5.2.1 Solution retenue

À l'issue de cette analyse, le développement sur mesure d'une application interne s'est révélé être le meilleur compromis entre performance, maîtrise de la solution, coût et adéquation au contexte. Toutes les solutions SaaS du marché dépassent très largement le budget mensuel disponible et aucune n'est adaptée au contexte spécifique togolais en termes de connectivité et de langue. Cette décision sera officiellement validée par **David KATOH** et **Rachid DERMAN** lors de la réunion de validation du présent cahier des charges.

#### 5.2.2 Justification du choix

Ce choix se justifie par :

- **Équilibre coût/efficacité :** pas de frais de licence, coût d'hébergement minimal tout en garantissant une performance optimale adaptée aux besoins de NG-STARs.
- **Adaptabilité :** l'application sera conçue précisément pour le contexte togolais, avec un mode hors ligne robuste répondant aux contraintes réelles de connectivité terrain.
- **Propriété intellectuelle :** NG-STARs sera entièrement propriétaire de la solution, sans dépendance à un fournisseur externe.
- **Compétences internes disponibles :** l'équipe senior de NG-STARs dispose de toutes les compétences techniques nécessaires pour développer et maintenir la solution de manière autonome.
- **Évolutivité :** possibilité d'enrichir progressivement la solution selon les besoins futurs de l'entreprise.

#### 5.2.3 Fonctionnement de la solution et avantages

Quelle que soit la solution technologique retenue, la solution cible se décomposera en trois composantes complémentaires :

- **Une application mobile (iOS et Android)** dédiée aux techniciens terrain, permettant la saisie complète des interventions, la capture de photos, la signature électronique et l'envoi de rapports, avec un fonctionnement garanti hors connexion.
- **Un tableau de bord web** accessible aux managers et administrateurs via navigateur, offrant une vision en temps réel de toutes les interventions, des statistiques, des exports de données, ainsi que la réception et la gestion des demandes clients.
- **Un backend centralisé (API REST)** assurant la synchronisation des données entre l'application mobile et le dashboard web, la génération des PDF, la gestion des notifications et la sécurisation des données.

L'application mobile comportera les écrans principaux suivants :

- **Écran d'accueil :** liste des interventions du jour, bouton « Nouvelle Intervention », indicateur de statut de synchronisation (vert/orange/rouge), accès rapide aux fiches récentes.
- **Formulaire d'intervention en 8 sections :** saisie guidée pas à pas, auto-complétions, calcul automatique des durées, validation des champs obligatoires.
- **Écran de signature :** 3 zones tactiles (Client, Technicien, Responsable), bouton d'envoi Email.
- **Fiche Client :** historique complet des interventions par client, coordonnées et contacts.
- **Paramètres / Profil :** informations de l'utilisateur connecté, préférences de notification, statut de synchronisation.
- **Portail Client (accès externe) :** formulaire simplifié de création de demande (nature du problème, urgence, coordonnées, description libre), accessible via un lien sécurisé à usage unique ou un espace client dédié. L'envoi notifie automatiquement l'équipe concernée.

Cette solution apportera une valeur considérable à l'organisation : zéro papier, 100 % de traçabilité, temps de saisie ramené à moins de 5 minutes par intervention, et visibilité management en temps réel depuis n'importe quel navigateur.

---

## 6. ÉTUDE DES TECHNOLOGIES

### 6.1 Critères de Sélection Technologique

Le choix technologique définitif sera arrêté lors du Sprint 0, en concertation avec l'équipe technique senior. Les critères de sélection retenus, par ordre de priorité, sont les suivants :

- Qualité du support natif du mode hors-ligne (critère éliminatoire)
- Performance et fluidité de l'interface utilisateur sur iOS et Android
- Maturité de l'écosystème et disponibilité des librairies nécessaires : signature tactile, génération PDF, GPS, upload de photos
- Maîtrise effective par l'équipe de développement senior disponible
- Solutions open-source privilégiées pour éliminer les coûts de licence
- Compatibilité iOS et Android simultanée depuis une base de code unique

### 6.2 Application Mobile

**Solution retenue :** Flutter (Dart) — choisi pour ses performances élevées, son UI cohérente cross-platform et la maîtrise de l'équipe.

| Technologie | Éditeur | Points Forts |
|-------------|---------|--------------|
| Flutter | Google | Performances très élevées, UI cohérente iOS/Android, Drift (SQLite) natif pour offline |

### 6.3 Backend – Options Étudiées

**Solution retenue :** Spring Boot (Java) — choisi pour sa robustesse, son écosystème mature et la maîtrise de l'équipe.

| Technologie | Type | Points Forts | Points Faibles |
|-------------|------|--------------|----------------|
| Spring Boot | Framework Java | Performances élevées, auto-documentation, validation native | Requiert une maîtrise en Java |

### 6.4 Base de Données

**Recommandation préliminaire :** PostgreSQL est privilégié pour sa structure robuste et sa maintenabilité à long terme.

| Base de Données | Type | Rôle dans NG-Fields | Justification |
|-----------------|------|---------------------|---------------|
| PostgreSQL | Relationnelle SQL | Backend principal : interventions, clients, utilisateurs, médias | Robuste, ACID, open-source, idéal pour données structurées relationnelles |
| Drift (SQLite) | Locale embarquée | Stockage local mobile pour le mode hors-ligne | Conçu offline-first, performant sur mobile, synchronisation native |
| Redis | Cache en mémoire | Sessions, cache API, file de notifications | Ultra-rapide, adapté aux données temporaires et queues de tâches |

### 6.5 Infrastructure et Hébergement

Supabase (free tier) est utilisé pour le développement : PostgreSQL 500MB, Storage 1GB, Auth intégré. Gratuit, zéro maintenance serveur.

| Solution | Type | Avantages | Coût estimé / mois |
|----------|------|-----------|---------------------|
| Supabase | DB + Storage + Auth | PostgreSQL 500MB, Storage 1GB, Auth, API — gratuit | **0 €** |
| Supabase | Backend-as-a-Service | PostgreSQL managé, authentification intégrée, tier gratuit généreux | 0 – 25 € |
| Firebase | BaaS Google | Notifications push natives, synchronisation temps réel | 0 – 10 € (faible volume) |

### 6.6 Exigences de Sécurité

| Exigence | Implémentation Technique Prévue |
|----------|--------------------------------|
| Conformité RGPD | Politique de conservation des données, droit à l'effacement, consentement explicite documenté |
| Chiffrement des communications | HTTPS obligatoire sur tous les endpoints avec TLS 1.3 |
| Authentification sécurisée | JSON Web Tokens (JWT) avec expiration courte + authentification multi-facteurs (MFA) |
| Chiffrement des données locales | Base de données locale mobile chiffrée (Drift + flutter_secure_storage) |
| Audit trail complet | Journalisation horodatée de toutes les actions utilisateurs avec identification |
| Sauvegardes automatiques | Backup quotidien de la base de données avec rétention de 30 jours minimum |
| Plan de reprise d'activité (PRA) | Documentation et procédures de reprise en cas d'incident majeur ou de sinistre |

### 6.7 Volumétrie et Dimensionnement

| Métrique | Estimation |
|----------|------------|
| Utilisateurs simultanés en heure de pointe | 20 utilisateurs |
| Interventions créées par jour (pic) | 10 interventions/jour |
| Stockage médias (photos) | ~5 Go/mois |
| Requêtes API par jour | 500 à 1 000 requêtes/jour |
| Volume base de données à 1 an (hors médias) | < 10 Go |
| Infrastructure | Supabase Free Tier (500MB DB, 1GB Storage) — suffisant pour V1 |

---

## 7. PLANNING PRÉVISIONNEL

### 7.1 Méthodologie de Développement

Le projet NG-Fields sera développé en suivant la méthodologie Agile Scrum avec des sprints d'une (1) semaine. Cette approche garantit une livraison incrémentale de valeur, une visibilité constante sur l'avancement et une adaptation rapide aux retours des utilisateurs.

**Cérémonies Agile prévues à chaque sprint :**
- Sprint Planning (lundi matin) : définition du backlog du sprint et attribution des tâches de la semaine
- Daily Stand-up (quotidien) : synchronisation sur l'avancement, les blocages et les priorités du jour
- Sprint Review (vendredi) : démonstration des fonctionnalités livrées aux parties prenantes
- Sprint Retrospective (vendredi) : analyse des points d'amélioration du travail

### 7.2 Jalons Principaux du Projet

| Jalon | Description | Responsable |
|-------|-------------|-------------|
| J-1 | Validation officielle du Cahier des Charges | David (IT) |
| J-2 | Démarrage officiel du développement – Sprint 1 | Chef de projet |
| J-3 | V0 – Architecture de base, CI/CD, authentification JWT opérationnelle | Stagiaire |
| J-4 | V0.5 – Formulaire d'intervention complet (mode connecté) | Stagiaire |
| J-5 | V0.7 – Mode hors-ligne complet et synchronisation automatique | Stagiaire |
| J-6 | V0.9 – PDF, Signatures, Notifications push, Dashboard manager | Stagiaire |
| J-7 | Phase de Tests d'Acceptation Utilisateur (UAT) avec les techniciens et managers | Stagiaire + Users |
| J-8 | Corrections et ajustements post-UAT | Équipe Dev |
| J-9 ★ | **MISE EN PRODUCTION OFFICIELLE – Version 1.0** | Chef de projet |

### 7.3 Détail des Sprints

| Sprint | Livrables et Objectifs |
|--------|------------------------|
| S-01 | Mise en place de l'architecture applicative, environnements dev/staging/prod, pipeline CI/CD, authentification JWT + MFA |
| S-02 | Module gestion des clients, profils et droits utilisateurs, structure de la base de données PostgreSQL |
| S-03 | Formulaire d'intervention – Sections 1 à 4 : informations préalables, client, détails intervention, consommables |
| S-04 | Formulaire – Sections 5 à 7 : photos avant/après, recommandations, signatures électroniques (canvas) |
| S-05 | Géolocalisation GPS, auto-calcul de durée, intégration mode hors-ligne (Drift / SQLite local) |
| S-06 | Queue de synchronisation offline, résolution de conflits, indicateur visuel de statut réseau en temps réel |
| S-07 | Génération automatique du rapport PDF (logo, photos, signatures, QR code), envoi par Email |
| S-08 | Système de notifications push et email (création, complétion, retard d'intervention) |
| S-09 | Dashboard manager web (statistiques, filtres avancés, exports CSV/Excel), tests d'intégration complets |
| S-10 | Tests UAT avec techniciens et managers, recueil des retours, corrections de bugs et optimisations de performance |
| S-11 | Déploiement en production, publication App Store et Google Play, rédaction documentation technique et guide utilisateur |

### 7.4 Budget Prévisionnel du Projet

| Poste de Dépense | Coût Initial | Coût Récurrent / Mois |
|------------------|-------------|----------------------|
| Supabase (PostgreSQL + Storage + Auth) | 0 € | 0 € |
| Abonnement Claude Pro (assistance IA développement) | 20 € | 20 € |
| **TOTAL ESTIMÉ** | **20 €** | **25 – 35 €** |

---

## CONCLUSION

Le présent cahier des charges constitue l'aboutissement d'une analyse approfondie du processus d'intervention terrain de NG-STARs. Il a permis de documenter avec précision l'état des lieux actuel, d'identifier et de quantifier treize dysfonctionnements majeurs, de formuler quatre questions de problématique structurantes, et de proposer un cadre fonctionnel et technique complet pour y répondre.

À l'issue du projet, si la solution proposée est validée et développée conformément aux spécifications de ce document, NG-STARs disposera d'un outil qui transformera durablement sa performance opérationnelle.

Le présent cahier des charges constitue la référence contractuelle du projet à compter de sa date de validation officielle.

---

| Auteur du Document | Chef de Projet | Validateur Final |
|--------------------|----------------|------------------|
| Nelson Emmanuel FOLLY | Barnabé MIDJRATO | David KATOH |
| Signature : _____________ | Signature : _____________ | Signature : _____________ |
| Date : __________________ | Date : __________________ | Date : __________________ |
