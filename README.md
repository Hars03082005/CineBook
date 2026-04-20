# CineBook Movie Ticket Booking System

OOAD mini project using Spring Boot, MongoDB, and React (Vite).

## Tech Stack
- Backend: Spring Boot 3, Spring Security (JWT), Spring Data MongoDB
- Frontend: React, React Router, Axios, Vite
- Database: MongoDB
- Build: Maven Wrapper + npm

## Project Structure
```text
CineBook_OOAD_Project/
  src/main/java/com/project/moviebooking/
    config/
    controller/
    dto/
    exception/
    model/
    patterns/
    repository/
    service/
  src/main/resources/application.properties
  frontend/
    src/
      api/
      components/
      pages/
  pom.xml
  mvnw.cmd
```

## Prerequisites
- Java 17+
- Node.js 18+
- MongoDB running locally on port 27017

## Run Locally

### 1) Start MongoDB
```powershell
net start MongoDB
```

### 2) Start Backend
From the project root:
```powershell
.\mvnw.cmd spring-boot:run
```
```bash
./mvnw spring-boot:run
```

If Maven wrapper download fails in your network/DNS setup, use a local Maven install or the cached Maven wrapper distribution.

Backend URL: http://localhost:8080

### 3) Start Frontend
```powershell
Set-Location .\frontend
npm install
npm run dev
```

Frontend URL: http://localhost:3000

## Default Login
- Admin: `admin@cinebook.com`
- Password: `admin123`

These credentials are seeded automatically on startup if the admin account does not already exist.

## Key Functional Flows
- Authentication: Register/Login with JWT
- Movie Discovery: Browse and search movies
- Show Selection: Upcoming shows only (expired/past shows filtered)
- Seat Booking: Live availability, seat locking, duplicate prevention
- Payment: UPI, Card, Wallet, Net Banking
- Ticketing: Digital ticket with QR payload, download/print support
- Cancellation: Policy-based refund with seat release
- Admin: Manage movies/theatres/shows, monitor bookings, refunds, reports

## Important Business Rules Implemented
- Expired show filtering by real-time clock on listings and seat access
- Booking blocked for already started/past shows
- Payment hold timeout: 10 minutes, then seat release
- Cancellation and refund policy:
  - More than 2 hours before show: 50% refund
  - Within 2 hours before show: no refund, but cancellation is still allowed
  - Past show: cancellation rejected

## API Overview

### Auth
- POST /api/auth/register
- POST /api/auth/login

### Movies
- GET /api/movies
- GET /api/movies/{id}
- GET /api/movies/search?title=

### Shows and Seats
- GET /api/shows/movie/{movieId}
- GET /api/shows/{showId}
- GET /api/shows/{showId}/seats

### Booking
- POST /api/bookings
- GET /api/bookings/my
- GET /api/bookings/{id}/cancellation-preview
- POST /api/bookings/{id}/cancel

### Payment
- POST /api/payments/process

### Ticket
- GET /api/tickets/{ticketId}
- GET /api/tickets/my

### Admin
- /api/admin/movies (CRUD)
- /api/admin/theatres (CRUD)
- /api/admin/shows (CRUD)
- GET /api/admin/bookings
- GET /api/admin/bookings/monitor
- GET /api/admin/refunds
- GET /api/admin/reports/summary

## Data Notes
Collections used in MongoDB:
- users
- movies
- theatres
- shows
- seats
- bookings
- payments
- tickets

## Troubleshooting
- Backend fails to start:
  - Verify MongoDB is running on localhost:27017
  - Verify Java version is 17+
  - If wrapper fails, use a working Maven installation/network
- Frontend API errors:
  - Ensure backend is up at port 8080
  - Ensure JWT token is present for protected routes

## License
Academic project for learning OOAD concepts.
