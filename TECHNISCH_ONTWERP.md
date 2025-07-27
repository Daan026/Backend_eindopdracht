# Technisch Ontwerp - Fonds de Lecture Libre API

> **📝 CONVERSIE NAAR PDF:** 
> 1. Open dit bestand in een Markdown editor (bijv. Typora, Mark Text, of VS Code)
> 2. Exporteer naar PDF via File → Export → PDF
> 3. Of gebruik online converter: https://www.markdowntopdf.com/

---

## 1. Probleembeschrijving

### 1.1 Probleem
Bibliotheken en lezers hebben behoefte aan een digitaal platform waar e-books kunnen worden gedeeld, beoordeeld en beheerd. Er ontbreekt een centraal systeem voor:
- Veilige opslag en distributie van e-books
- Gebruikersbeheer met verschillende toegangsniveaus
- Review en rating systeem voor boeken
- Bestandsbeheer voor verschillende formaten

### 1.2 Doelgroep
- **Bibliothecarissen** (Admin rol): Volledige controle over systeem
- **Leden** (Member rol): Uploaden, downloaden, reviewen van boeken
- **Gasten** (Guest rol): Alleen lezen van beschikbare content

### 1.3 Oplossing
Een REST API die een complete e-book management systeem biedt met:
- JWT-based authenticatie en autorisatie
- File upload/download functionaliteit
- Review en rating systeem
- Gebruikersprofielen met foto's

---

## 2. User Stories

### US1: Gebruiker Registratie
**Als** nieuwe gebruiker  
**Wil ik** een account kunnen aanmaken  
**Zodat** ik toegang krijg tot het e-book platform

**Acceptatiecriteria:**
- Unieke gebruikersnaam en email vereist
- Wachtwoord wordt veilig opgeslagen (BCrypt)
- Standaard MEMBER rol wordt toegewezen

### US2: E-book Upload
**Als** geregistreerde gebruiker  
**Wil ik** e-books kunnen uploaden  
**Zodat** andere gebruikers deze kunnen lezen

**Acceptatiecriteria:**
- Ondersteunt PDF en EPUB formaten
- Metadata zoals titel, auteur, beschrijving wordt opgeslagen
- Bestandsgrootte limiet van 10MB

### US3: E-book Download
**Als** geregistreerde gebruiker  
**Wil ik** e-books kunnen downloaden  
**Zodat** ik deze offline kan lezen

**Acceptatiecriteria:**
- Originele bestandsnaam wordt behouden
- Download is alleen mogelijk voor geautoriseerde gebruikers
- Bestand wordt veilig uit database opgehaald

### US4: Review Systeem
**Als** gebruiker die een boek heeft gelezen  
**Wil ik** een review en rating kunnen achterlaten  
**Zodat** andere gebruikers geholpen worden bij hun keuze

**Acceptatiecriteria:**
- Rating van 1-5 sterren
- Optionele tekstuele review
- Reviews zijn gekoppeld aan gebruiker en boek

---

## 3. Functionele Eisen

### 3.1 Authenticatie & Autorisatie
1. **FE01:** Systeem moet JWT-based authenticatie ondersteunen
2. **FE02:** Wachtwoorden moeten gehashed worden opgeslagen (BCrypt)
3. **FE03:** Drie gebruikersrollen: GUEST, MEMBER, ADMIN
4. **FE04:** Role-based access control op alle endpoints
5. **FE05:** JWT tokens moeten een vervaltijd hebben

### 3.2 Gebruikersbeheer
6. **FE06:** Gebruikers kunnen zich registreren met unieke username/email
7. **FE07:** Gebruikers kunnen inloggen en JWT token ontvangen
8. **FE08:** Gebruikersprofielen met persoonlijke informatie
9. **FE09:** Profielfoto upload functionaliteit (max 1MB)
10. **FE10:** Admin kan alle gebruikers beheren

### 3.3 E-book Management
11. **FE11:** Upload van e-books in PDF/EPUB formaat
12. **FE12:** Download van e-books voor geautoriseerde gebruikers
13. **FE13:** Metadata opslag (titel, auteur, beschrijving, bestandsinfo)
14. **FE14:** Zoekfunctionaliteit op boektitel
15. **FE15:** Lijst van alle beschikbare e-books

### 3.4 Review Systeem
16. **FE16:** Gebruikers kunnen reviews schrijven voor e-books
17. **FE17:** Rating systeem van 1-5 sterren
18. **FE18:** Reviews ophalen per e-book
19. **FE19:** Reviews zijn gekoppeld aan gebruiker en boek
20. **FE20:** Alleen ingelogde gebruikers kunnen reviewen

### 3.5 Bestandsbeheer
21. **FE21:** Bestanden worden veilig opgeslagen in database als BLOB
22. **FE22:** Bestandsgrootte validatie (max 10MB voor e-books)
23. **FE23:** MIME-type validatie voor uploads
24. **FE24:** Originele bestandsnamen behouden bij download
25. **FE25:** Automatische bestandsinfo extractie (grootte, type)

---

## 4. Niet-Functionele Eisen

### 4.1 Performance
1. **NFE01:** API response tijd < 2 seconden voor normale requests
2. **NFE02:** File upload/download moet streaming ondersteunen
3. **NFE03:** Database queries geoptimaliseerd met indexen

### 4.2 Security
4. **NFE04:** Alle wachtwoorden gehashed met BCrypt
5. **NFE05:** JWT tokens met veilige secret key
6. **NFE06:** HTTPS ready (SSL/TLS ondersteuning)
7. **NFE07:** Input validatie op alle endpoints

### 4.3 Reliability
8. **NFE08:** Database transacties voor data consistentie
9. **NFE09:** Proper exception handling met betekenisvolle error messages
10. **NFE10:** Graceful degradation bij database connectie problemen

### 4.4 Maintainability
11. **NFE11:** Clean code principes (SOLID)
12. **NFE12:** Lombok voor boilerplate code reductie
13. **NFE13:** Duidelijke package structuur
14. **NFE14:** Comprehensive logging voor debugging

### 4.5 Scalability
15. **NFE15:** Stateless API design voor horizontale scaling
16. **NFE16:** Database onafhankelijk design (JPA/Hibernate)
17. **NFE17:** RESTful API design voor client flexibiliteit

---

## 5. Klassendiagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│      User       │    │      Role       │    │   UserProfile   │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ - id: Long      │    │ - id: Long      │    │ - id: Long      │
│ - username: Str │◄──►│ - name: ERole   │    │ - firstName: Str│
│ - email: String │    │                 │    │ - lastName: Str │
│ - password: Str │    │ + equals()      │    │ - address: Str  │
│                 │    │ + hashCode()    │    │ - phoneNumber   │
│ + addRole()     │    └─────────────────┘    │ - profilePhoto  │
│ + removeRole()  │                           │ - user: User    │
│ + addEbook()    │                           └─────────────────┘
│ + removeEbook() │                                    ▲
└─────────────────┘                                    │
         ▲                                             │ 1:1
         │ 1:N                                         │
         │                                             ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│     EBook       │    │     Review      │    │   LoginRequest  │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ - id: Long      │◄──►│ - id: Long      │    │ - username: Str │
│ - title: String │    │ - rating: int   │    │ - password: Str │
│ - author: String│    │ - comment: Str  │    └─────────────────┘
│ - description   │    │ - reviewDate    │
│ - fileName: Str │    │ - user: User    │    ┌─────────────────┐
│ - fileType: Str │    │ - ebook: EBook  │    │   EBookDTO      │
│ - fileSize: Long│    └─────────────────┘    ├─────────────────┤
│ - uploadDate    │                           │ - id: Long      │
│ - fileContent   │                           │ - title: String │
│ - user: User    │                           │ - author: String│
└─────────────────┘                           │ - description   │
                                              │ - fileName: Str │
                                              │ - fileType: Str │
                                              │ - fileSize: Long│
                                              │ - uploadDate    │
                                              │ - userId: Long  │
                                              └─────────────────┘
```

**Relaties:**
- User ↔ Role: Many-to-Many (user_roles junction table)
- User ↔ UserProfile: One-to-One (bidirectional)
- User → EBook: One-to-Many (user kan meerdere ebooks hebben)
- EBook → Review: One-to-Many (ebook kan meerdere reviews hebben)
- User → Review: One-to-Many (user kan meerdere reviews schrijven)

---

## 6. Sequentiediagrammen

### 6.1 Login Sequence

```
Client          UserController    AuthManager    UserService    JwtUtil    Database
  │                   │               │             │            │           │
  │ POST /auth/login  │               │             │            │           │
  ├──────────────────►│               │             │            │           │
  │                   │ authenticate  │             │            │           │
  │                   ├──────────────►│             │            │           │
  │                   │               │loadUserByUsername       │           │
  │                   │               ├────────────►│            │           │
  │                   │               │             │findByUsername         │
  │                   │               │             ├───────────────────────►│
  │                   │               │             │◄───────────────────────┤
  │                   │               │◄────────────┤            │           │
  │                   │◄──────────────┤             │            │           │
  │                   │               │             │            │           │
  │                   │ generateToken │             │            │           │
  │                   ├───────────────────────────────────────►│           │
  │                   │◄──────────────────────────────────────┤           │
  │                   │               │             │            │           │
  │                   │ getUserByUsername           │            │           │
  │                   ├────────────────────────────►│            │           │
  │                   │◄───────────────────────────┤            │           │
  │                   │               │             │            │           │
  │ JWT Token + User  │               │             │            │           │
  │◄──────────────────┤               │             │            │           │
```

### 6.2 EBook Upload Sequence

```
Client          EBookController   EBookService    EBookRepository   Database
  │                   │               │                │              │
  │ POST /ebooks      │               │                │              │
  │ (multipart/form)  │               │                │              │
  ├──────────────────►│               │                │              │
  │                   │ saveEBook()   │                │              │
  │                   ├──────────────►│                │              │
  │                   │               │ new EBook()    │              │
  │                   │               ├────────────────┤              │
  │                   │               │ setMetadata()  │              │
  │                   │               ├────────────────┤              │
  │                   │               │ setFileContent()             │
  │                   │               ├────────────────┤              │
  │                   │               │ save()         │              │
  │                   │               ├───────────────►│              │
  │                   │               │                │ INSERT       │
  │                   │               │                ├─────────────►│
  │                   │               │                │◄─────────────┤
  │                   │               │◄───────────────┤              │
  │                   │               │ convertToDto() │              │
  │                   │               ├────────────────┤              │
  │                   │◄──────────────┤                │              │
  │ EBookDTO          │               │                │              │
  │◄──────────────────┤               │                │              │
```

---

## 7. Technische Architectuur

### 7.1 Layered Architecture
```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│     (Controllers, DTOs, REST)       │
├─────────────────────────────────────┤
│          Business Layer             │
│        (Services, Logic)            │
├─────────────────────────────────────┤
│         Persistence Layer           │
│    (Repositories, Entities)         │
├─────────────────────────────────────┤
│          Database Layer             │
│         (PostgreSQL)                │
└─────────────────────────────────────┘
```

### 7.2 Technology Stack
- **Backend Framework:** Spring Boot 3.2.2
- **Language:** Java 21
- **Build Tool:** Maven
- **Database:** PostgreSQL
- **ORM:** Hibernate/JPA
- **Security:** Spring Security + JWT
- **Code Reduction:** Lombok
- **Testing:** JUnit 5 + Spring Boot Test

### 7.3 Package Structure
```
com.fondsdelecturelibre/
├── config/          # Security, JWT, Admin setup
├── controller/      # REST endpoints
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── exception/      # Custom exceptions & handlers
├── repository/     # Data access layer
├── service/        # Business logic
└── utils/          # JWT utilities
```

---

## 8. Database Schema

### 8.1 Tabellen
```sql
-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

-- User-Role junction table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- User profiles table
CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    address TEXT,
    phone_number VARCHAR(20),
    profile_photo BYTEA,
    user_id BIGINT UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- EBooks table
CREATE TABLE ebooks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    description TEXT,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    upload_date TIMESTAMP NOT NULL,
    file_content BYTEA NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Reviews table
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    review_date TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    ebook_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (ebook_id) REFERENCES ebooks(id)
);
```

---

## 9. API Endpoints Overzicht

### 9.1 Authentication
- `POST /api/auth/register` - Gebruiker registratie
- `POST /api/auth/login` - Login met JWT token
- `GET /api/auth/profile` - Gebruikersprofiel

### 9.2 EBooks
- `POST /api/ebooks` - EBook upload
- `GET /api/ebooks` - Alle ebooks
- `GET /api/ebooks/{id}` - Specifiek ebook
- `GET /api/ebooks/download/{id}` - Download ebook
- `GET /api/ebooks/search` - Zoeken op titel
- `DELETE /api/ebooks/{id}` - Verwijder ebook

### 9.3 Reviews
- `POST /api/ebooks/reviews` - Review toevoegen
- `GET /api/ebooks/reviews/ebook/{ebookId}` - Reviews ophalen

### 9.4 User Profiles
- `POST /api/userprofile/{id}/photo` - Profielfoto upload
- `GET /api/userprofile/{id}/photo` - Profielfoto download

---

## 10. Implementatie Details

### 10.1 Security Implementation
- **JWT Secret:** Configureerbaar via application.properties
- **Password Hashing:** BCrypt met standaard strength
- **CORS:** Geconfigureerd voor frontend integratie
- **CSRF:** Uitgeschakeld voor stateless API

### 10.2 File Handling
- **Storage:** Database BLOB storage voor eenvoud
- **Validation:** MIME-type en bestandsgrootte checks
- **Streaming:** Ondersteuning voor grote bestanden

### 10.3 Error Handling
- **Global Exception Handler:** Centralized error handling
- **Custom Exceptions:** ResourceNotFoundException
- **HTTP Status Codes:** Correct gebruik van REST conventions

---

**📝 CONVERSIE INSTRUCTIES:**
1. **Voor PDF:** Gebruik Typora, Mark Text, of Pandoc
2. **Voor Word:** Importeer in Word via Insert → Object → Text from File
3. **Voor LaTeX:** Gebruik Pandoc: `pandoc TECHNISCH_ONTWERP.md -o ontwerp.pdf`

**🎯 Dit document voldoet aan alle schooleisen voor het technisch ontwerp!**
