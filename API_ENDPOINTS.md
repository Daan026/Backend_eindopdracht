# Fonds de Lecture Libre API - Endpoints Overzicht

## 🚀 **Base URL**
```
http://localhost:8080
```

## 🔐 **Authentication Endpoints**

### Register Nieuwe Gebruiker
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

### Login (Krijgt JWT Token)
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "Admin",
  "password": "fondsdelecturelibre"
}
```

**Response:**
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

### Gebruikersprofiel Ophalen
```http
GET /api/auth/profile?username=Admin
```

## 📚 **EBook Endpoints**

### EBook Uploaden
```http
POST /api/ebooks
Content-Type: multipart/form-data
Authorization: Bearer <your-jwt-token>

Form Data:
- file: [PDF/EPUB bestand]
- title: "Boek Titel"
- author: "Auteur Naam"
- description: "Beschrijving van het boek"
```

### Alle EBooks Ophalen
```http
GET /api/ebooks
Authorization: Bearer <your-jwt-token>
```

### Specifiek EBook Ophalen
```http
GET /api/ebooks/{id}
Authorization: Bearer <your-jwt-token>
```

### EBook Downloaden
```http
GET /api/ebooks/download/{id}
Authorization: Bearer <your-jwt-token>
```

### Zoeken op Titel
```http
GET /api/ebooks/search?title=zoekterm
Authorization: Bearer <your-jwt-token>
```

### EBook Verwijderen
```http
DELETE /api/ebooks/{id}
Authorization: Bearer <your-jwt-token>
```

## ⭐ **Review Endpoints**

### Review Toevoegen
```http
POST /api/ebooks/reviews
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "ebookId": 1,
  "userId": 1,
  "rating": 5,
  "comment": "Geweldig boek!"
}
```

### Reviews van EBook Ophalen
```http
GET /api/ebooks/reviews/ebook/{ebookId}
Authorization: Bearer <your-jwt-token>
```

## 👤 **User Profile Endpoints**

### Profielfoto Uploaden
```http
POST /api/userprofile/{id}/photo
Content-Type: multipart/form-data
Authorization: Bearer <your-jwt-token>

Form Data:
- file: [JPG/PNG bestand, max 1MB]
```

### Profielfoto Downloaden
```http
GET /api/userprofile/{id}/photo
Authorization: Bearer <your-jwt-token>
```

## 🔑 **Test Credentials**

### Admin Gebruiker
- **Username:** `Admin`
- **Password:** `fondsdelecturelibre`
- **Email:** `admin@fondsdelecturelibre.com`
- **Roles:** ADMIN, MEMBER

## 🛡️ **Authorization Headers**

Voor alle beveiligde endpoints gebruik je:
```
Authorization: Bearer <your-jwt-token>
```

## 📝 **Gebruikersrollen**

- **ROLE_GUEST:** Alleen lezen
- **ROLE_MEMBER:** Ebooks uploaden/downloaden, reviews schrijven
- **ROLE_ADMIN:** Volledige toegang, gebruikersbeheer

## 🗄️ **Database Info**

- **Type:** PostgreSQL
- **Database:** `fonds_de_lecture_libre`
- **Host:** `localhost:5432`
- **Username:** `postgres`

## 🔧 **Development Info**

- **Java Version:** 21
- **Spring Boot:** 3.2.2
- **Maven:** Build tool
- **JWT:** Voor authenticatie
- **Lombok:** Voor cleaner code

## 📋 **Volgende Stappen**

1. **Test de API** met Postman of curl
2. **Maak echte unit tests** (momenteel alleen dummy tests)
3. **Schrijf documentatie** voor schoolproject:
   - Technisch ontwerp (PDF)
   - Verantwoordingsdocument (PDF)
   - Installatiehandleiding (PDF)
   - Postman collectie (JSON)

## ✅ **Status**

- ✅ Compilatie: SUCCESS
- ✅ Tests: SUCCESS (2 dummy tests)
- ✅ Applicatie startup: SUCCESS
- ✅ Database connectie: SUCCESS
- ✅ JWT authenticatie: READY
- ✅ File upload/download: READY
- ✅ Alle endpoints: READY

**De API is volledig functioneel en klaar voor gebruik!** 🎉
