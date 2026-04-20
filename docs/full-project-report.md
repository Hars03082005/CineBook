# CineBook Movie Ticket Booking System

## Comprehensive Project Report

### 1. Project Title
CineBook - Movie Ticket Booking System

### 2. Project Type
OOAD mini project built as a full-stack web application.

### 3. Project Summary
CineBook is an online movie ticket booking platform where users can register, log in, browse movies, select shows, choose seats, make payments, receive tickets, and cancel bookings according to business rules. The system also provides an admin dashboard for managing movies, theatres, shows, bookings, refunds, and reports.

The project is implemented with a layered architecture using Spring Boot for the backend, MongoDB for persistence, and React with Vite for the frontend. It is designed to demonstrate OOAD principles, SOLID principles, GRASP responsibilities, and multiple design patterns in a practical application.

### 4. Problem Statement
Manual movie ticket booking is inconvenient, time-consuming, and prone to seat clashes or booking confusion. CineBook solves this by providing a centralized digital system for movie discovery, seat reservation, payment handling, ticket generation, and cancellation management.

### 5. Project Objectives
- Provide a smooth movie ticket booking experience.
- Support secure authentication and role-based access control.
- Prevent seat conflicts through live seat locking and booking state management.
- Apply refund rules based on cancellation timing.
- Give admins tools to manage inventory and monitor business activity.
- Demonstrate real OOAD design through patterns and principles.

### 6. Technology Stack
#### Backend
- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Security
- Spring Data MongoDB
- JWT authentication
- Lombok
- Maven Wrapper

#### Frontend
- React
- Vite
- React Router
- Axios
- JavaScript / JSX

#### Database
- MongoDB

#### Supporting Tools
- Maven wrapper for backend builds
- npm for frontend dependency management
- MongoDB local server on port 27017

### 7. System Architecture
The application follows a layered architecture:
- Presentation layer: React pages and components.
- Controller layer: HTTP endpoints for users and admins.
- Service layer: business logic and orchestration.
- Pattern layer: implementations of design patterns such as Strategy, Builder, Factory, Observer, Decorator, Proxy, State, and Chain of Responsibility.
- Repository layer: MongoDB access using Spring Data repositories.
- Model layer: core business entities like Booking, Movie, Show, Seat, Payment, Ticket, User, and Theatre.
- DTO layer: request and response transfer objects.

This structure keeps the code modular, easier to understand, and easier to present as an OOAD case study.

### 8. Main Functional Modules
#### 8.1 Authentication Module
This module handles:
- User registration
- User login
- Admin login
- JWT token generation and validation
- Role-based authorization

#### 8.2 Movie Browsing Module
This module allows users to:
- View movies
- Search movies by title
- Open movie details
- Browse active shows for each movie

#### 8.3 Show and Seat Module
This module allows users to:
- View shows for a selected movie
- See only valid upcoming shows
- Select seats from a show-specific layout
- Prevent duplicate seat reservation
- Handle temporary seat holds during booking/payment

#### 8.4 Booking Module
This module handles:
- Booking creation
- Booking preview
- Booking confirmation
- Booking history
- Booking status tracking
- Booking cancellation

#### 8.5 Payment Module
This module handles:
- Multiple payment methods
- Payment processing
- Payment success and failure handling
- Refund state tracking
- Ticket generation after successful payment

#### 8.6 Ticket Module
This module handles:
- Digital ticket creation
- Ticket lookup
- Ticket retrieval by booking or user
- Ticket support for download or display

#### 8.7 Cancellation and Refund Module
This module applies cancellation policy and refund rules:
- More than 2 hours before show: 50% refund
- Within 2 hours before show: no refund, but cancellation is still allowed
- After show starts: cancellation rejected

#### 8.8 Admin Module
This module provides admin-only features:
- Movie management
- Theatre management
- Show management
- Booking monitoring
- Refund monitoring
- Summary report generation
- User overview

### 9. Core User Flow
#### 9.1 Registration and Login
1. User creates an account.
2. User logs in using email and password.
3. Backend validates credentials and issues JWT.
4. Frontend stores token and uses it for protected requests.

#### 9.2 Movie Booking Flow
1. User selects a movie.
2. User chooses one of the valid shows.
3. User selects seats.
4. The system checks seat availability.
5. Booking is created with pending status.
6. Payment is completed.
7. Ticket is generated.
8. Notifications are triggered.

#### 9.3 Cancellation Flow
1. User opens booking history.
2. User requests cancellation preview.
3. Backend calculates refund eligibility.
4. Frontend displays refund amount and policy.
5. If user confirms, booking is cancelled.
6. Seats are released and payment refund state is updated.

### 10. Admin Flow
1. Admin logs in with role-based access.
2. Admin manages movies, theatres, and shows.
3. Admin can view booking and refund data.
4. Admin can generate summary reports.
5. Admin can monitor application activity through backend endpoints.

### 11. Backend Components
#### 11.1 Controllers
- `AuthController` - registration and login.
- `MovieController` - movie browse and search endpoints.
- `ShowController` - show listing and show seat data.
- `BookingController` - booking actions and booking history.
- `PaymentController` - payment processing and ticket access.
- `CancellationController` - cancellation preview and cancellation action.
- `AdminController` - admin CRUD and reporting endpoints.

#### 11.2 Services
- `AuthService` - authentication and user registration logic.
- `MovieService` - movie search and listing.
- `ShowService` - show retrieval and management.
- `BookingServiceImpl` - booking lifecycle and cancellation orchestration.
- `BookingFacade` - simplified interface for controllers.
- `PaymentServiceImpl` - payment workflow and refund processing.
- `NotificationService` - notification abstraction.
- `PricingService` - fare and fee calculations.
- `RefundService` - refund policy evaluation.
- `SeatService` - seat management.
- `TicketService` - ticket retrieval.
- `ReportService` - report generation.
- `AdminService` - admin report access abstraction.

#### 11.3 Models
- `User` - application user and role data.
- `Movie` - movie details.
- `Theatre` - theatre details.
- `Show` - schedule and seat inventory.
- `Seat` - seat-level booking state.
- `Booking` - reservation and lifecycle state.
- `Payment` - payment and refund information.
- `Ticket` - issued ticket data.
- `Report` - generated report data.

#### 11.4 DTOs
- `LoginRequest`
- `LoginResponse`
- `RegisterRequest`
- `BookingRequest`
- `PaymentRequest`
- `CancellationPreview`
- `ApiResponse`

#### 11.5 Repositories
Spring Data MongoDB repositories handle data persistence for the core models and are wrapped with repository contracts in the refactored design.

### 12. Frontend Components
#### 12.1 Pages
- `LandingPage.jsx`
- `MovieList.jsx`
- `ShowSelection.jsx`
- `SeatSelection.jsx`
- `PaymentPage.jsx`
- `TicketPage.jsx`
- `BookingHistory.jsx`
- `UserLogin.jsx`
- `UserRegister.jsx`
- `AdminDashboard.jsx`
- `AdminLogin.jsx`

#### 12.2 Shared Components
- `Navbar.jsx`
- `ProtectedRoute.jsx`
- `Toast.jsx`

#### 12.3 Frontend Responsibilities
- Render booking UI.
- Call backend APIs using Axios.
- Store and attach JWT tokens.
- Display cancellation preview and refund details.
- Restrict admin routes and protected user routes.

### 13. Database Collections
MongoDB collections used by the application:
- `users`
- `movies`
- `theatres`
- `shows`
- `seats`
- `bookings`
- `payments`
- `tickets`

### 14. Security Implementation
- JWT-based stateless authentication.
- BCrypt password hashing.
- Role-based access for admin endpoints.
- Public access for login, registration, and movie browsing.
- CORS configuration for frontend development ports.
- Method-level security enabled where needed.

### 15. Important Business Rules
- Past shows cannot be booked.
- Expired shows are filtered from public display.
- Seats are locked during booking/payment flow.
- Payment timeout can release held seats.
- Cancellation depends on time remaining before the show.
- Refunds are calculated using the cancellation policy.
- Duplicate seat booking is prevented.
- Admin operations are restricted to admin role.

### 16. Design Patterns Implemented
#### 16.1 Singleton
Used for singleton-style database/helper access and Spring-managed singleton beans.

#### 16.2 Factory Method
- `TicketFactory`
- `PaymentStrategyFactory`

#### 16.3 Builder
- `BookingBuilder`

#### 16.4 Facade
- `BookingFacade`

#### 16.5 Strategy
- `PaymentContext`
- payment strategies for UPI, Card, Wallet, and Net Banking

#### 16.6 Proxy
- `PaymentProxy`

#### 16.7 Observer
- `BookingEventPublisher`
- `EmailNotificationListener`
- `SMSNotificationListener`

#### 16.8 Decorator
- `BaseTicket`
- `TicketDecorator`
- `SeatUpgradeDecorator`
- `CancellationProtectionDecorator`
- `MealComboDecorator`

#### 16.9 Chain of Responsibility
- `CancellationChain`
- `BookingStatusHandler`
- `ShowTimePassedHandler`
- `WithinTwoHoursHandler`
- `RefundProcessorHandler`
- `NotificationHandler`

#### 16.10 State
- `BookingContext`
- `PaymentContextState`
- `CancellationContextState`
- `UserAuthContextState`

#### 16.11 Template Method
- `BookingTemplate`
- `StandardBookingTemplate`
- `GroupBookingTemplate`
- `LastMinuteBookingTemplate`

### 17. SOLID Principles Implemented
#### 17.1 Single Responsibility Principle
Each class has a focused responsibility.

#### 17.2 Open/Closed Principle
New behavior can be added without changing the core flow.

#### 17.3 Liskov Substitution Principle
Implementations can be substituted through interfaces without breaking the system.

#### 17.4 Interface Segregation Principle
Smaller interfaces are used for focused responsibilities.

#### 17.5 Dependency Inversion Principle
High-level modules depend on abstractions, not concrete implementations.

### 18. GRASP Principles Implemented
- Information Expert
- Creator
- Controller
- Low Coupling
- High Cohesion
- Polymorphism
- Pure Fabrication
- Indirection
- Protected Variations

### 19. Notable OOAD Improvements
- A dedicated facade simplifies the booking flow.
- Refund logic is isolated in a dedicated service.
- Seat handling is separated from booking and payment logic.
- Notification handling is asynchronous and decoupled.
- Report generation is abstracted through a strategy.
- Payment flow is extensible through strategy and proxy layers.
- Cancellation rules are handled by a chain rather than hardcoded branching.
- Domain models now include expert methods for logic they naturally own.

### 20. API Summary
#### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`

#### Movies
- `GET /api/movies`
- `GET /api/movies/{id}`
- `GET /api/movies/search?title=`

#### Shows and Seats
- `GET /api/shows/movie/{movieId}`
- `GET /api/shows/{showId}`
- `GET /api/shows/{showId}/seats`

#### Booking
- `POST /api/bookings`
- `GET /api/bookings/my`
- `GET /api/bookings/{id}/cancellation-preview`
- `POST /api/bookings/{id}/cancel`

#### Payment
- `POST /api/payments/process`

#### Ticket
- `GET /api/tickets/{ticketId}`
- `GET /api/tickets/my`

#### Admin
- `GET/POST/PUT/DELETE /api/admin/movies`
- `GET/POST/PUT/DELETE /api/admin/theatres`
- `GET/POST/PUT/DELETE /api/admin/shows`
- `GET /api/admin/bookings`
- `GET /api/admin/bookings/monitor`
- `GET /api/admin/refunds`
- `GET /api/admin/reports/summary`

### 21. Default Admin Credentials
The project seeds an admin user on startup if one does not already exist.
- Email: `admin@cinebook.com`
- Password: `admin123`

### 22. Running the Project
#### Backend
From the project root:
```powershell
.\mvnw.cmd spring-boot:run
```

#### Frontend
```powershell
npm --prefix frontend run dev
```

#### Database
Start MongoDB locally on port 27017.

### 23. Build and Validation Status
- Backend build and test compilation were validated.
- Backend tests were validated successfully.
- Frontend production build was validated successfully.
- The application is runnable with the backend, frontend, and MongoDB services.

### 24. Current Refund Rule
The current cancellation policy is:
- More than 2 hours before show: 50% refund
- Within 2 hours before show: no refund, cancellation allowed
- After show starts: cancellation rejected

### 25. Testing and Quality Notes
- The project uses separation of concerns to keep testing manageable.
- Business rules are centralized in dedicated services and handlers.
- Pattern-based design reduces the impact of future changes.
- Frontend build succeeds even when runtime development server issues occur from environment setup.

### 26. Strengths of the Project
- Real-world full-stack implementation.
- Clear OOAD demonstration.
- Practical use of multiple design patterns.
- Secure authentication and authorization.
- Clean separation between UI, business logic, and data access.
- Presentation-ready architecture with understandable modules.

### 27. Limitations and Future Enhancements
- The project is designed for a local MongoDB setup, so deployment configuration would need additional work for cloud hosting.
- Payment gateway integration is simulated rather than tied to a real provider.
- Reporting is summary-focused and could be expanded with analytics dashboards.
- Notifications are implemented as backend observers and could be extended to email/SMS providers.
- Additional test coverage can always be added for more edge cases.

### 28. Conclusion
CineBook is a complete OOAD-based movie booking application that combines practical booking functionality with strong software design. It demonstrates layered architecture, secure authentication, clean service decomposition, and meaningful use of design patterns and principles. The system is suitable for demos, viva presentations, and academic submissions because it is both functional and conceptually rich.
