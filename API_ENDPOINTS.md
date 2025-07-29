# API Endpoints Documentation

## Authentication Endpoints

### POST /auth/register
Registreer een nieuwe gebruiker.

**Request Body:**
```json
{
  "username": "string",
  "email": "string", 
  "password": "string"
}
```

**Response:** `201 Created`
```json
{
  "message": "Gebruiker succesvol geregistreerd"
}
```

### POST /auth/login
Login met gebruikersnaam en wachtwoord.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:** `200 OK`
```json
{
  "token": "jwt-token-here",
  "type": "Bearer",
  "username": "string",
  "roles": ["ROLE_MEMBER"]
}
```

## E-Book Endpoints

### GET /ebooks
Haal alle e-books op.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Book Title",
    "author": "Author Name",
    "description": "Book description",
    "uploadDate": "2024-01-01T10:00:00"
  }
]
```

### POST /ebooks
Upload een nieuw e-book.

**Headers:** 
- `Authorization: Bearer {token}`
- `Content-Type: multipart/form-data`

**Form Data:**
- `file`: E-book bestand
- `title`: Titel van het boek
- `author`: Auteur van het boek
- `description`: Beschrijving van het boek

**Response:** `201 Created`

### GET /ebooks/{id}/download
Download een e-book bestand.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK` (Binary file data)

## Review Endpoints

### POST /reviews
Schrijf een review voor een e-book.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "ebookId": 1,
  "rating": 5,
  "comment": "Excellent book!"
}
```

**Response:** `201 Created`

### GET /reviews/ebook/{ebookId}
Haal alle reviews op voor een specifiek e-book.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "rating": 5,
    "comment": "Great book!",
    "username": "reviewer123",
    "createdDate": "2024-01-01T10:00:00"
  }
]
```

## User Profile Endpoints

### GET /users/profile
Haal gebruikersprofiel op.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "username": "user123",
  "email": "user@example.com",
  "profile": {
    "firstName": "John",
    "lastName": "Doe",
    "address": "123 Main St"
  }
}
```

### PUT /users/profile
Update gebruikersprofiel.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe", 
  "address": "123 Main St"
}
```

**Response:** `200 OK`

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "message": "Validatiefout: veld is verplicht",
  "details": "uri=/api/endpoint"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "message": "Ongeldige gebruikersnaam of wachtwoord",
  "details": "uri=/auth/login"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "message": "Toegang geweigerd: onvoldoende rechten",
  "details": "uri=/admin/endpoint"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "message": "Resource niet gevonden",
  "details": "uri=/ebooks/999"
}
```

## Authorization Roles

- **ROLE_GUEST**: Alleen lezen van publieke content
- **ROLE_MEMBER**: Upload/download e-books, schrijven reviews
- **ROLE_ADMIN**: Volledige toegang, gebruikersbeheer
