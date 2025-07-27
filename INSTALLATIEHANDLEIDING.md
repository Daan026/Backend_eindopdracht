# Installatiehandleiding - Fonds de Lecture Libre API

> **📝 CONVERSIE NAAR PDF:** 
> 1. Open dit bestand in een Markdown editor (bijv. Typora, Mark Text, of VS Code)
> 2. Exporteer naar PDF via File → Export → PDF
> 3. Voor professionele layout: gebruik Pandoc: `pandoc INSTALLATIEHANDLEIDING.md -o installatiehandleiding.pdf`

---

## 📋 Inhoudsopgave

1. [Systeemvereisten](#1-systeemvereisten)
2. [Benodigde Software](#2-benodigde-software)
3. [Database Setup](#3-database-setup)
4. [Project Installatie](#4-project-installatie)
5. [Configuratie](#5-configuratie)
6. [Applicatie Starten](#6-applicatie-starten)
7. [API Testen](#7-api-testen)
8. [Testgebruikers](#8-testgebruikers)
9. [Troubleshooting](#9-troubleshooting)
10. [REST Endpoints](#10-rest-endpoints)

---

## 1. Systeemvereisten

### Minimale Vereisten
- **Besturingssysteem:** Windows 10/11, macOS 10.15+, Linux Ubuntu 18.04+
- **RAM:** 4GB (8GB aanbevolen)
- **Schijfruimte:** 2GB vrije ruimte
- **Netwerkverbinding:** Voor Maven dependencies download

### Ondersteunde Platforms
- ✅ Windows 10/11
- ✅ macOS (Intel & Apple Silicon)
- ✅ Linux (Ubuntu, CentOS, Debian)

---

## 2. Benodigde Software

### 2.1 Java Development Kit (JDK) 21

**Download & Installatie:**

#### Windows:
1. Download Oracle JDK 21 of OpenJDK 21 van:
   - Oracle: https://www.oracle.com/java/technologies/downloads/#java21
   - OpenJDK: https://adoptium.net/
2. Voer installer uit en volg instructies
3. Controleer installatie:
   ```cmd
   java -version
   javac -version
   ```

#### macOS:
```bash
# Via Homebrew (aanbevolen)
brew install openjdk@21

# Of download van Adoptium website
```

#### Linux:
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# CentOS/RHEL
sudo yum install java-21-openjdk-devel
```

**Verificatie:**
```bash
java -version
# Output moet tonen: openjdk version "21.x.x"
```

### 2.2 Apache Maven 3.9+

**Download & Installatie:**

#### Windows:
1. Download Maven van: https://maven.apache.org/download.cgi
2. Unzip naar `C:\Program Files\Apache\maven`
3. Voeg toe aan PATH: `C:\Program Files\Apache\maven\bin`
4. Herstart Command Prompt

#### macOS:
```bash
# Via Homebrew
brew install maven
```

#### Linux:
```bash
# Ubuntu/Debian
sudo apt install maven

# CentOS/RHEL
sudo yum install maven
```

**Verificatie:**
```bash
mvn -version
# Output moet tonen: Apache Maven 3.9.x
```

### 2.3 PostgreSQL 15+

**Download & Installatie:**

#### Windows:
1. Download PostgreSQL van: https://www.postgresql.org/download/windows/
2. Voer installer uit
3. Onthoud wachtwoord voor `postgres` gebruiker
4. Standaard poort: 5432

#### macOS:
```bash
# Via Homebrew
brew install postgresql@15
brew services start postgresql@15
```

#### Linux:
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# Start service
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

**Verificatie:**
```bash
psql --version
# Output: psql (PostgreSQL) 15.x
```

### 2.4 Git (Optioneel)

**Voor clonen van repository:**
```bash
# Windows: Download van https://git-scm.com/
# macOS: brew install git
# Linux: sudo apt install git
```

---

## 3. Database Setup

### 3.1 PostgreSQL Configuratie

**1. Connect naar PostgreSQL:**
```bash
# Windows/Linux
psql -U postgres

# macOS (Homebrew)
psql postgres
```

**2. Maak database aan:**
```sql
-- Maak database
CREATE DATABASE fonds_de_lecture_libre;

-- Maak gebruiker (optioneel)
CREATE USER ebook_user WITH PASSWORD 'ebook_password';

-- Geef rechten
GRANT ALL PRIVILEGES ON DATABASE fonds_de_lecture_libre TO ebook_user;

-- Verlaat psql
\q
```

**3. Test database connectie:**
```bash
psql -U postgres -d fonds_de_lecture_libre -c "SELECT version();"
```

### 3.2 Database Schema

**Automatische Schema Creatie:**
- Schema wordt automatisch aangemaakt door Hibernate
- Tabellen: `users`, `roles`, `user_roles`, `user_profiles`, `ebooks`, `reviews`
- Default data wordt geladen via `AdminInitializer`

---

## 4. Project Installatie

### 4.1 Project Downloaden

**Optie 1: Via Git (als beschikbaar)**
```bash
git clone [REPOSITORY_URL]
cd ebook-api
```

**Optie 2: Via ZIP Download**
1. Download project ZIP file
2. Unzip naar gewenste locatie
3. Open terminal in project directory

### 4.2 Dependencies Installeren

**Maven Dependencies Downloaden:**
```bash
# In project root directory
mvn clean install

# Of alleen dependencies:
mvn dependency:resolve
```

**Verwachte Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 30-60 seconds
```

---

## 5. Configuratie

### 5.1 Database Configuratie

**Bestand:** `src/main/resources/application.properties`

**Standaard Configuratie:**
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/fonds_de_lecture_libre
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
app.jwt.secret=mySecretKey12345678901234567890123456789012345678901234567890
app.jwt.expiration=86400000

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging Configuration
logging.level.com.fondsdelecturelibre=DEBUG
logging.level.org.springframework.security=DEBUG
```

### 5.2 Configuratie Aanpassen

**Als je andere database credentials gebruikt:**
```properties
# Pas aan naar jouw setup
spring.datasource.username=jouw_username
spring.datasource.password=jouw_password
spring.datasource.url=jdbc:postgresql://localhost:5432/jouw_database
```

**Voor productie (optioneel):**
```properties
# Productie instellingen
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.com.fondsdelecturelibre=INFO
```

---

## 6. Applicatie Starten

### 6.1 Compileren & Testen

**1. Project Compileren:**
```bash
mvn clean compile
```

**2. Tests Uitvoeren:**
```bash
mvn test
```

**3. JAR Bestand Maken:**
```bash
mvn package
```

### 6.2 Applicatie Starten

**Optie 1: Via Maven (Development)**
```bash
mvn spring-boot:run
```

**Optie 2: Via JAR (Production)**
```bash
java -jar target/fonds-de-lecture-libre-1.0.0.jar
```

**Optie 3: Via IDE**
- Open project in IntelliJ IDEA/Eclipse
- Run `FondsDeLibreApplication.java`

### 6.3 Startup Verificatie

**Verwachte Console Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.2)

...
Started FondsDeLibreApplication in 8.123 seconds
Standaard admin-gebruiker aangemaakt: gebruikersnaam 'Admin', wachtwoord 'fondsdelecturelibre'
```

**Applicatie is beschikbaar op:** http://localhost:8080

---

## 7. API Testen

### 7.1 Health Check

**Test of API draait:**
```bash
curl http://localhost:8080/actuator/health
```

### 7.2 Login Test

**Test login endpoint:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "Admin",
    "password": "fondsdelecturelibre"
  }'
```

**Verwachte Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "Admin",
    "email": "admin@fondsdelecturelibre.com"
  },
  "tokenType": "Bearer"
}
```

### 7.3 Postman Collection

**Import Postman Collection:**
1. Open Postman
2. Import → File → Selecteer `postman_collection.json`
3. Alle endpoints zijn vooraf geconfigureerd

---

## 8. Testgebruikers

### 8.1 Standaard Admin Gebruiker

**Automatisch aangemaakt bij eerste startup:**

| Field | Value |
|-------|-------|
| Username | `Admin` |
| Password | `fondsdelecturelibre` |
| Email | `admin@fondsdelecturelibre.com` |
| Roles | `ROLE_ADMIN`, `ROLE_MEMBER` |

### 8.2 Extra Testgebruikers Aanmaken

**Via API (na inloggen als Admin):**

**Member Gebruiker:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testmember",
    "email": "member@test.com",
    "password": "password123"
  }'
```

**Guest Gebruiker:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testguest",
    "email": "guest@test.com",
    "password": "password123"
  }'
```

### 8.3 Gebruikersrollen

| Rol | Permissions |
|-----|-------------|
| **ROLE_GUEST** | - Alleen lezen van publieke content |
| **ROLE_MEMBER** | - Ebooks uploaden/downloaden<br>- Reviews schrijven<br>- Profiel beheren |
| **ROLE_ADMIN** | - Alle MEMBER rechten<br>- Gebruikersbeheer<br>- Systeem configuratie |

---

## 9. Troubleshooting

### 9.1 Veelvoorkomende Problemen

#### Probleem: "Port 8080 already in use"
**Oplossing:**
```bash
# Vind proces op poort 8080
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # macOS/Linux

# Stop proces of gebruik andere poort
# In application.properties:
server.port=8081
```

#### Probleem: Database connectie fout
**Symptomen:**
```
org.postgresql.util.PSQLException: Connection refused
```

**Oplossingen:**
1. **Controleer PostgreSQL status:**
   ```bash
   # Windows
   services.msc → PostgreSQL service
   
   # macOS
   brew services list | grep postgresql
   
   # Linux
   sudo systemctl status postgresql
   ```

2. **Start PostgreSQL:**
   ```bash
   # Windows: Start via Services
   # macOS
   brew services start postgresql@15
   
   # Linux
   sudo systemctl start postgresql
   ```

3. **Controleer database bestaat:**
   ```bash
   psql -U postgres -l | grep fonds_de_lecture_libre
   ```

#### Probleem: Maven build fout
**Symptomen:**
```
[ERROR] Failed to execute goal ... compilation failure
```

**Oplossingen:**
1. **Controleer Java versie:**
   ```bash
   java -version  # Moet 21.x.x zijn
   echo $JAVA_HOME
   ```

2. **Clean en rebuild:**
   ```bash
   mvn clean
   mvn compile
   ```

3. **Update dependencies:**
   ```bash
   mvn dependency:resolve
   ```

#### Probleem: JWT Token errors
**Symptomen:**
```
JWT signature does not match locally computed signature
```

**Oplossing:**
- Controleer `app.jwt.secret` in `application.properties`
- Secret moet minimaal 256 bits (32 karakters) zijn

### 9.2 Logging & Debugging

**Verhoog log level voor debugging:**
```properties
# In application.properties
logging.level.com.fondsdelecturelibre=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

**Log bestanden locatie:**
- Console output (standaard)
- Optioneel: `logs/application.log`

### 9.3 Performance Issues

**Als applicatie traag start:**
1. Controleer beschikbaar RAM (min 4GB)
2. Sluit andere Java applicaties
3. Verhoog JVM heap size:
   ```bash
   java -Xmx2g -jar target/fonds-de-lecture-libre-1.0.0.jar
   ```

---

## 10. REST Endpoints

### 10.1 Authentication Endpoints

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "Admin",
  "password": "fondsdelecturelibre"
}
```

#### Profile
```http
GET /api/auth/profile?username=Admin
Authorization: Bearer {token}
```

### 10.2 EBook Endpoints

#### Upload EBook
```http
POST /api/ebooks
Authorization: Bearer {token}
Content-Type: multipart/form-data

Form Data:
- file: [PDF/EPUB bestand]
- title: "Boek Titel"
- author: "Auteur Naam"
- description: "Beschrijving"
```

#### Get All EBooks
```http
GET /api/ebooks
Authorization: Bearer {token}
```

#### Download EBook
```http
GET /api/ebooks/download/{id}
Authorization: Bearer {token}
```

#### Search EBooks
```http
GET /api/ebooks/search?title=zoekterm
Authorization: Bearer {token}
```

### 10.3 Review Endpoints

#### Add Review
```http
POST /api/ebooks/reviews
Authorization: Bearer {token}
Content-Type: application/json

{
  "ebookId": 1,
  "userId": 1,
  "rating": 5,
  "comment": "Geweldig boek!"
}
```

#### Get Reviews
```http
GET /api/ebooks/reviews/ebook/{ebookId}
Authorization: Bearer {token}
```

### 10.4 User Profile Endpoints

#### Upload Profile Photo
```http
POST /api/userprofile/{id}/photo
Authorization: Bearer {token}
Content-Type: multipart/form-data

Form Data:
- file: [JPG/PNG bestand, max 1MB]
```

#### Get Profile Photo
```http
GET /api/userprofile/{id}/photo
Authorization: Bearer {token}
```

### 10.5 Response Formats

**Success Response (200):**
```json
{
  "id": 1,
  "title": "Example Book",
  "author": "Author Name",
  "description": "Book description",
  "fileName": "book.pdf",
  "fileType": "application/pdf",
  "fileSize": 1024000,
  "uploadDate": "2024-01-15T10:30:00",
  "userId": 1
}
```

**Error Response (400/401/403/404):**
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/ebooks"
}
```

---

## 11. Volgende Stappen

### 11.1 Na Succesvolle Installatie

1. **✅ Test alle endpoints** met Postman collection
2. **✅ Upload een test e-book** om file handling te verifiëren
3. **✅ Maak extra testgebruikers** aan
4. **✅ Test review functionaliteit**

### 11.2 Voor Productie Deployment

1. **Database:** Gebruik externe PostgreSQL instance
2. **Security:** Wijzig JWT secret en database passwords
3. **Monitoring:** Implementeer logging en health checks
4. **Backup:** Setup database backup strategie

### 11.3 Development Workflow

1. **Code Changes:** Maak wijzigingen in source code
2. **Test:** Run `mvn test` voor unit tests
3. **Build:** Run `mvn package` voor nieuwe JAR
4. **Deploy:** Restart applicatie met nieuwe JAR

---

## 📞 Support

**Bij problemen:**
1. Controleer eerst [Troubleshooting](#9-troubleshooting) sectie
2. Controleer log files voor error details
3. Verifieer alle [Systeemvereisten](#1-systeemvereisten)

**Project Informatie:**
- **Versie:** 1.0.0
- **Java:** 21
- **Spring Boot:** 3.2.2
- **Database:** PostgreSQL 15+

---

**📝 CONVERSIE INSTRUCTIES:**
1. **Voor PDF:** Gebruik Pandoc: `pandoc INSTALLATIEHANDLEIDING.md -o installatiehandleiding.pdf --toc`
2. **Voor Word:** Copy-paste met behoud van code formatting
3. **Voor HTML:** `pandoc INSTALLATIEHANDLEIDING.md -o installatiehandleiding.html`

**🎯 Deze installatiehandleiding bevat alle benodigde informatie voor succesvolle setup!**
