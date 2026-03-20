# Reservation Service (Spring Boot)

Backend REST service for managing room reservations. The application
allows creating, updating, approving and cancelling reservations while
enforcing business rules such as date validation and conflict detection.

The project demonstrates a typical **Spring Boot backend architecture**
using Controller → Service → Repository layers.

------------------------------------------------------------------------

## Features

-   Create reservations
-   Update reservation details
-   Approve reservations
-   Cancel reservations
-   Prevent overlapping reservations for the same room
-   Input validation using Jakarta Validation
-   Global exception handling
-   Structured error responses
-   Logging with SLF4J

------------------------------------------------------------------------

## Tech Stack

-   Java 17+
-   Spring Boot
-   Spring Web
-   Spring Data JPA
-   Hibernate
-   Jakarta Validation
-   Maven

------------------------------------------------------------------------

## Architecture

The project follows a layered backend architecture:

Controller → Service → Repository → Database

### Controller Layer

Handles HTTP requests and exposes REST endpoints.

Example endpoints:

GET /res\
GET /res/{id}\
POST /res/post\
PUT /res/{id}\
POST /res/{id}/approve\
DELETE /res/{id}/cancel

------------------------------------------------------------------------

### Service Layer

Contains the core business logic:

-   validation of reservation dates
-   reservation status management
-   enforcing allowed state transitions
-   conflict detection between reservations

Business rules implemented:

-   startDate must not be after endDate
-   only PENDING reservations can be updated
-   APPROVED reservations cannot be cancelled
-   reservations for the same room cannot overlap

------------------------------------------------------------------------

### Repository Layer

Implemented using **Spring Data JPA**.

Provides:

-   CRUD database operations
-   custom status update query

Main entity:

ReservationEntity - id - userId - roomId - startDate - endDate - status

------------------------------------------------------------------------

## Reservation Status

Reservations support the following states:

PENDING\
APPROVED\
CANCELLED

Allowed transitions:

PENDING → APPROVED\
PENDING → CANCELLED

------------------------------------------------------------------------

## Validation

Request validation is implemented using **Jakarta Bean Validation**
annotations such as:

-   @NotNull
-   @FutureOrPresent
-   @Null (for ID during creation)

------------------------------------------------------------------------

## Error Handling

Global exception handling is implemented using **@ControllerAdvice**.

Handled exceptions include:

-   EntityNotFoundException
-   IllegalArgumentException
-   IllegalStateException
-   MethodArgumentNotValidException
-   generic server errors

Example error response:

{ "message": "Bad request error", "detailedMessage": "StartDate must be
earlier than EndDate!", "errorTime": "2026-03-10T15:32:01" }

------------------------------------------------------------------------

## Reservation Conflict Detection

When approving a reservation, the system checks for date conflicts with
already approved reservations for the same room.

Two reservations conflict if:

startDate \< existing.endDate\
AND\
existing.startDate \< endDate

Only APPROVED reservations are considered in this check.

------------------------------------------------------------------------

## Running the Application

Clone repository:

git clone https://github.com/your-username/your-repository.git

Build project:

mvn clean install

Run application:

mvn spring-boot:run

Application starts at:

http://localhost:8080