# CineBook Movie Ticket Booking System

## Project Report

### 1. Executive Summary
CineBook is a full-stack movie ticket booking system built as an OOAD mini project using Spring Boot, MongoDB, Spring Security JWT, and a React/Vite frontend. The application supports end-to-end booking flows including user registration, login, movie discovery, show selection, seat reservation, payment processing, ticket generation, booking history, cancellation, refunds, and admin operations.

The codebase has been refactored to demonstrate core OOAD concepts with concrete implementations of SOLID principles, GRASP responsibilities, and several design patterns. The architecture now separates responsibilities into dedicated services, controllers, pattern classes, and repository abstractions so the application remains maintainable, extensible, and presentation-friendly.

### 2. What The System Does
CineBook allows a user to:
- Register and log in using JWT authentication.
- Browse active movies and search by title, genre, or language.
- View upcoming shows only, with past/expired shows filtered out.
- Select seats from a show-specific seat grid.
- Reserve seats temporarily during payment.
- Pay using UPI, card, wallet, or net banking.
- Receive a digital ticket after successful payment.
- View booking history and cancellation preview.
- Cancel bookings based on policy and receive refunds accordingly.
- Access admin-only dashboards to manage movies, theatres, shows, bookings, refunds, and summary reports.

### 3. Main Features
#### User Features
- User registration and login.
- JWT-based authentication.
- Movie browsing and search.
- Show listing with active and upcoming filter.
- Seat selection and seat locking.
- Payment processing with multiple methods.
- Ticket generation and lookup.
- Booking history.
- Cancellation preview.
- Booking cancellation and refund.

#### Admin Features
- Add, update, soft-delete, and hard-delete movies.
- Add, update, and deactivate theatres.
- Add and update shows.
- Auto-generate seats for a new show.
- View all bookings and monitor bookings.
- View refund requests.
- Generate summary reports.
- View all registered users.

#### System Features
- MongoDB persistence.
- Password hashing with BCrypt.
- Role-based access control.
- Async observer notifications.
- Seat hold timeout and payment timeout handling.
- Duplicate booking prevention.
- Expired/past show filtering.

### 4. Technology Stack
- Backend: Spring Boot 3.3.5
- Security: Spring Security + JWT
- Database: MongoDB
- Frontend: React 18 + Vite + React Router + Axios
- Build tools: Maven Wrapper, npm
- Language: Java 17 for backend, JavaScript/JSX for frontend

### 5. Architecture Overview
The project is organized into clear layers:
- Controllers handle HTTP requests and delegate work.
- Services contain business logic and orchestration.
- Pattern classes implement reusable OOAD behaviors.
- Repositories handle persistence through Spring Data MongoDB.
- Models represent the core business entities.
- DTOs carry request and response data.
- Frontend pages and components provide the user interface.

The refactor introduces a Booking Facade and several pure-fabrication services to reduce coupling and improve cohesion.

### 6. SOLID Principles Implementation
#### S - Single Responsibility Principle
Each class now focuses on one responsibility.
- [AuthService](../src/main/java/com/project/moviebooking/service/AuthService.java) handles registration, login, and credential validation only.
- [BookingServiceImpl](../src/main/java/com/project/moviebooking/service/impl/BookingServiceImpl.java) manages booking lifecycle only.
- [PaymentServiceImpl](../src/main/java/com/project/moviebooking/service/impl/PaymentServiceImpl.java) processes payment only.
- [NotificationService](../src/main/java/com/project/moviebooking/service/NotificationService.java) handles notifications only.
- [PricingService](../src/main/java/com/project/moviebooking/service/PricingService.java) handles pricing logic only.
- [RefundService](../src/main/java/com/project/moviebooking/service/RefundService.java) handles refund policy only.
- [SeatService](../src/main/java/com/project/moviebooking/service/SeatService.java) handles seat state only.
- [TicketService](../src/main/java/com/project/moviebooking/service/TicketService.java) handles ticket retrieval only.
- [ReportService](../src/main/java/com/project/moviebooking/service/ReportService.java) handles report generation only.

#### O - Open/Closed Principle
The system is designed so new behavior can be added without changing core logic.
- New payment methods can be added as new `PaymentStrategy` classes.
- New ticket variants can be added through factory/decorator extensions.
- Cancellation rules can be extended through the cancellation chain.
- New report types can be added through new report generators.

#### L - Liskov Substitution Principle
Subclasses and implementations can replace their parent abstractions safely.
- `UPIPaymentStrategy`, `CardPaymentStrategy`, `WalletPaymentStrategy`, and `NetBankingPaymentStrategy` all implement the same payment contract.
- Ticket decorators can wrap any ticket component without changing calling code.
- Cancellation handlers can be added to the chain without breaking flow.

#### I - Interface Segregation Principle
The project now includes smaller focused interfaces instead of one large interface.
- `IBookable`
- `ICancellable`
- `IPayable`
- `INotifiable`
- `IReportable`
- `ISearchable`
- `IAuthenticable`
- `ISeatManageable`

These keep classes from depending on methods they do not use.

#### D - Dependency Inversion Principle
High-level modules now depend on abstractions.
- Controllers depend on facade/service abstractions instead of concrete classes.
- Repositories now have contract interfaces.
- Payment flow uses strategy/proxy abstractions.
- Admin reporting uses admin/report abstractions.

### 7. GRASP Principles Implementation
#### 1. Information Expert
Responsibilities are placed where the information already exists.
- [Seat](../src/main/java/com/project/moviebooking/model/Seat.java) knows availability and hold/release operations.
- [Show](../src/main/java/com/project/moviebooking/model/Show.java) knows whether it is expired.
- [Booking](../src/main/java/com/project/moviebooking/model/Booking.java) knows its total price and refund eligibility.
- [User](../src/main/java/com/project/moviebooking/model/User.java) can check active bookings.

#### 2. Creator
Objects are created by classes that aggregate or closely use them.
- [BookingBuilder](../src/main/java/com/project/moviebooking/patterns/builder/BookingBuilder.java) creates `Booking` objects.
- [TicketFactory](../src/main/java/com/project/moviebooking/patterns/TicketFactory.java) creates ticket variants.
- [Theatre](../src/main/java/com/project/moviebooking/model/Theatre.java) and admin flows create shows.
- `BookingServiceImpl` creates booking and ticket flow objects through factory/builders.

#### 3. Controller
Controllers receive requests and delegate business logic.
- [BookingController](../src/main/java/com/project/moviebooking/controller/BookingController.java)
- [PaymentController](../src/main/java/com/project/moviebooking/controller/PaymentController.java)
- [AuthController](../src/main/java/com/project/moviebooking/controller/AuthController.java)
- [AdminController](../src/main/java/com/project/moviebooking/controller/AdminController.java)
- [CancellationController](../src/main/java/com/project/moviebooking/controller/CancellationController.java)

#### 4. Low Coupling
The codebase reduces direct dependencies between modules.
- Controllers use the facade.
- Notification is separated from booking/payment logic.
- Payment gateway access is protected by proxy and adapter layers.
- Repository interfaces isolate business services from persistence implementations.

#### 5. High Cohesion
Each service and pattern class has one focused job.
- Reporting, pricing, refund logic, notifications, seat state, and ticket handling are separated.
- This improves testing and makes the code easier to explain in a presentation.

#### 6. Polymorphism
Variation is handled with interfaces and implementations instead of large if-else chains.
- Payment selection uses `PaymentStrategy`.
- Report selection uses `ReportGenerator`.
- Cancellation policy uses a handler chain.
- Ticket customization is handled through decorators.

#### 7. Pure Fabrication
Several classes exist purely to keep design clean.
- `BookingFacade`
- `NotificationService`
- `PricingService`
- `RefundService`
- `SeatLockManager`
- `ReportService`
- `TicketService`
- `PaymentStrategyFactory`

#### 8. Indirection
Intermediate layers reduce coupling.
- Controllers → `BookingFacade` → services
- Payment flow → `PaymentContext` → `PaymentProxy` → gateway
- Booking notifications → observer publisher → listeners
- Cancellation rules → chain handlers

#### 9. Protected Variations
Likely change points are isolated behind stable interfaces.
- Payment gateway changes are protected by proxy and gateway interface.
- Cancellation policy changes are protected by the cancellation chain.
- Pricing changes are localized to pricing service.
- Report formats are localized to report generators.
- Database access is protected by repository contracts.

### 8. Design Patterns Implemented
#### 1. Singleton
- `MongoDBSingleton` is implemented as a singleton helper for database connection logic.
- Spring itself also manages beans as singletons by default.

#### 2. Factory Method
- [TicketFactory](../src/main/java/com/project/moviebooking/patterns/TicketFactory.java) creates regular, premium, and VIP tickets.
- [PaymentStrategyFactory](../src/main/java/com/project/moviebooking/patterns/PaymentStrategyFactory.java) resolves the correct payment strategy.

#### 3. Builder
- [BookingBuilder](../src/main/java/com/project/moviebooking/patterns/builder/BookingBuilder.java) assembles a booking step by step.

#### 4. Facade
- [BookingFacade](../src/main/java/com/project/moviebooking/service/BookingFacade.java) presents a single simple interface to the controllers.

#### 5. Decorator
- [BaseTicket](../src/main/java/com/project/moviebooking/patterns/decorator/BaseTicket.java) can be wrapped by:
  - [SeatUpgradeDecorator](../src/main/java/com/project/moviebooking/patterns/decorator/SeatUpgradeDecorator.java)
  - [CancellationProtectionDecorator](../src/main/java/com/project/moviebooking/patterns/decorator/CancellationProtectionDecorator.java)
  - [MealComboDecorator](../src/main/java/com/project/moviebooking/patterns/decorator/MealComboDecorator.java)

#### 6. Proxy
- [PaymentProxy](../src/main/java/com/project/moviebooking/patterns/proxy/PaymentProxy.java) validates, masks, retries, and delegates payment requests.

#### 7. Observer
- [BookingEventPublisher](../src/main/java/com/project/moviebooking/patterns/BookingEventPublisher.java) publishes booking events.
- [EmailNotificationListener](../src/main/java/com/project/moviebooking/patterns/EmailNotificationListener.java) and [SMSNotificationListener](../src/main/java/com/project/moviebooking/patterns/SMSNotificationListener.java) respond asynchronously.

#### 8. Strategy
- [PaymentContext](../src/main/java/com/project/moviebooking/patterns/PaymentContext.java) chooses payment behavior at runtime.
- Payment strategies include UPI, Card, Wallet, and NetBanking.

#### 9. State
- [BookingContext](../src/main/java/com/project/moviebooking/patterns/state/BookingContext.java)
- [PaymentContextState](../src/main/java/com/project/moviebooking/patterns/state/PaymentContextState.java)
- [CancellationContextState](../src/main/java/com/project/moviebooking/patterns/state/CancellationContextState.java)
- [UserAuthContextState](../src/main/java/com/project/moviebooking/patterns/state/UserAuthContextState.java)

#### 10. Template Method
- [BookingTemplate](../src/main/java/com/project/moviebooking/patterns/template/BookingTemplate.java)
- [StandardBookingTemplate](../src/main/java/com/project/moviebooking/patterns/template/StandardBookingTemplate.java)
- [GroupBookingTemplate](../src/main/java/com/project/moviebooking/patterns/template/GroupBookingTemplate.java)
- [LastMinuteBookingTemplate](../src/main/java/com/project/moviebooking/patterns/template/LastMinuteBookingTemplate.java)

#### 11. Chain of Responsibility
- [CancellationChain](../src/main/java/com/project/moviebooking/patterns/chain/CancellationChain.java)
- Handlers:
  - [BookingStatusHandler](../src/main/java/com/project/moviebooking/patterns/chain/BookingStatusHandler.java)
  - [ShowTimePassedHandler](../src/main/java/com/project/moviebooking/patterns/chain/ShowTimePassedHandler.java)
  - [WithinTwoHoursHandler](../src/main/java/com/project/moviebooking/patterns/chain/WithinTwoHoursHandler.java)
  - [RefundProcessorHandler](../src/main/java/com/project/moviebooking/patterns/chain/RefundProcessorHandler.java)
  - [NotificationHandler](../src/main/java/com/project/moviebooking/patterns/chain/NotificationHandler.java)

### 9. Important Business Rules
- Only upcoming, active shows are displayed.
- Past shows cannot be booked or viewed for seat selection.
- Duplicate bookings for the same user, same show, and overlapping seats are rejected.
- Seats are held during booking/payment flow.
- Payment timeouts release held seats.
- Cancellation policy is evaluated before cancellation is confirmed.
- Refund behavior is centralized in the cancellation/refund flow.
- Notifications are sent asynchronously after booking confirmation.

### 10. Backend Flow Summary
#### Booking Flow
1. User selects seats.
2. Booking controller delegates to the facade.
3. Booking service validates show and seat availability.
4. Pricing service calculates fees.
5. Booking builder creates the booking object.
6. Booking is saved as pending.

#### Payment Flow
1. Payment controller sends request to facade.
2. Payment service selects the correct strategy.
3. Proxy validates and delegates to gateway.
4. Payment is stored.
5. Ticket is generated after success.
6. Notification observers are triggered.

#### Cancellation Flow
1. User requests cancellation preview.
2. Refund service evaluates policy through the chain.
3. Cancellation controller or booking controller confirms cancellation.
4. Seats are released.
5. Refund is recorded.
6. Booking status becomes cancelled.

#### Admin Flow
1. Admin logs in with restricted access.
2. Admin manages movies, theatres, and shows.
3. Admin views bookings, refunds, and summary reports.
4. Report service generates analytics data for presentation or dashboard use.

### 11. Frontend Summary
The React/Vite frontend provides pages for:
- Landing page
- Movie list
- Show selection
- Seat selection
- Booking history
- Login and registration
- Admin dashboard
- Payment page
- Ticket page

The frontend communicates with backend APIs using Axios and uses a local dev proxy to forward `/api` requests to the Spring Boot server.

### 12. Database Collections
MongoDB collections used by CineBook:
- users
- movies
- theatres
- shows
- seats
- bookings
- payments
- tickets

### 13. Authentication and Security
- JWT token-based stateless authentication.
- BCrypt password hashing.
- Role-based authorization for admin routes.
- CORS configured for frontend ports 3000 and 5173.
- `@PreAuthorize` used for admin-only endpoints.

### 14. Validation and Stability
The refactored backend was validated with successful compilation and tests, and the frontend production build also passes. This means the application remains runnable while the architecture has been significantly improved.

### 15. Presentation Talking Points
Use these points when presenting:
- The project is not just a booking app, but a structured OOAD case study.
- Controllers are thin; services contain logic.
- Booking, payment, notification, refund, reporting, and seat management are separated.
- The design patterns are not decorative; they are used in actual runtime flows.
- The system demonstrates extensibility: adding a new payment method, ticket type, or cancellation rule requires minimal code change.
- The application is secured, data-backed, and has both user and admin journeys.

### 16. Conclusion
CineBook is a feature-complete, presentation-ready movie ticket booking system with a clean OOAD architecture. It demonstrates strong separation of concerns, reusable pattern-driven design, and business workflows that are easy to explain in a viva or project demo.

If you present this project, you can confidently say that it covers real-world system design concepts, not just CRUD screens.
