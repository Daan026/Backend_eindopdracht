# Fonds de Lecture Libre API

Een REST API voor het beheren van e-books met gebruikersauthenticatie en bestandsbeheer.

## Features

- JWT-based authenticatie en autorisatie
- File upload/download functionaliteit voor e-books
- Review en rating systeem
- Gebruikersprofielen met foto's
- Role-based access control (Guest, Member, Admin)

## Tech Stack

- **Java 21** - LTS versie met moderne features
- **Spring Boot 3.2.2** - REST API framework
- **PostgreSQL** - Relationele database
- **JWT** - Stateless authenticatie
- **Maven** - Dependency management

## API Endpoints

- `POST /auth/register` - Gebruiker registratie
- `POST /auth/login` - Gebruiker login
- `GET /ebooks` - Lijst van e-books
- `POST /ebooks` - Upload e-book
- `GET /ebooks/{id}/download` - Download e-book
- `POST /reviews` - Schrijf review

## Getting Started

1. Clone de repository
2. Configureer PostgreSQL database
3. Run `mvn spring-boot:run`
4. API is beschikbaar op `http://localhost:8080`

## Database Schema

- **Users** - Gebruikersinformatie en authenticatie
- **UserProfiles** - Uitgebreide gebruikersprofielen (One-to-One)
- **EBooks** - E-book metadata en bestanden
- **Reviews** - Gebruikersreviews voor e-books (One-to-Many)
- **Roles** - Gebruikersrollen voor autorisatie

## Security

- Wachtwoorden worden gehashed met BCrypt
- JWT tokens voor stateless authenticatie
- Role-based autorisatie op endpoint niveau
- Input validatie via DTOs

## Testing

- Unit tests voor service layer
- Integration tests voor controllers
- Test coverage met JUnit en MockMvc

---

**Ontwikkeld als eindopdracht Backend Development**
