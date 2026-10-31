# Bus Booking System

A web-first bus ticket booking platform under development. The goal is
to let customers search trips, view seat availability, book tickets, and
manage their bookings, while bus operators and platform administrators
manage trips and operations.

## Project Status

**Version:** V1 --- In development

The Spring Boot backend starts locally, and user registration has been
tested through Swagger UI. Features and API contracts may change as
development continues.

## Architecture

V1 uses a **modular monolith**: one Spring Boot application organized
into domain-based packages.

``` text
bus-booking-system/
├── src/
│   ├── main/
│   │   ├── java/com/busbooking/
│   │   │   ├── booking/
│   │   │   ├── bus/
│   │   │   ├── config/
│   │   │   ├── route/
│   │   │   ├── seat/
│   │   │   ├── trip/
│   │   │   └── user/
│   │   └── resources/
│   └── test/
├── pom.xml
└── README.md
```

The React web client is maintained separately in the
`bus-booking-client` repository.

## Technology Stack

-   **Language:** Java 21
-   **Backend:** Spring Boot 4.1.2
-   **Web:** Spring MVC
-   **Persistence:** Spring Data JPA / Hibernate
-   **Database:** PostgreSQL
-   **Validation:** Jakarta Bean Validation
-   **Object mapping:** MapStruct
-   **Boilerplate reduction:** Lombok
-   **API documentation/testing:** Springdoc OpenAPI / Swagger UI
-   **Build:** Maven

## Prerequisites

Install the following before running the backend:

-   JDK 21
-   Git
-   PostgreSQL
-   Maven, or use the included Maven Wrapper

## Getting Started

### 1. Clone the repository

``` bash
git clone https://github.com/ramana-reddy-mv/bus-booking-system.git
cd bus-booking-system
```

### 2. Create a PostgreSQL database

Create a database for local development, for example:

``` sql
CREATE DATABASE bus_booking;
```

Configure the database connection using the properties expected by your
application. Do not commit real credentials to source control.

For example, if your application uses Spring datasource environment
variables, configure:

``` text
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bus_booking
SPRING_DATASOURCE_USERNAME=your_local_username
SPRING_DATASOURCE_PASSWORD=your_local_password
```

Make sure these variable names match your actual Spring configuration.
If your project uses `application.properties` or `application.yml` with
different names, follow that configuration.

### 3. Run the application

On Windows (PowerShell or Command Prompt):

``` bash
mvnw.cmd spring-boot:run
```

Or, if Maven is installed:

``` bash
mvn spring-boot:run
```

The application currently runs on port `8080` in the local development
setup.

### 4. Open Swagger UI

After the application starts, open:

-   Swagger UI: http://localhost:8080/swagger-ui/index.html
-   OpenAPI JSON: http://localhost:8080/v3/api-docs

Use Swagger UI to inspect and test the API endpoints exposed by the
application.

## Current Domain Areas

The codebase is organized around these domain areas:

-   **User:** registration and user-related operations
-   **Bus:** bus information and management
-   **Route:** route and stop information
-   **Seat:** seat definitions and seat-related operations
-   **Booking:** booking workflows
-   **Trip:** trip creation and search by origin, destination, and travel date

The scope and completion status of individual endpoints may change
during V1 development.

### Trip API

Create a trip with `POST /api/trips`:

``` json
{
  "busId": 1,
  "routeId": 1,
  "departureTime": "08:30:00",
  "arrivalTime": "18:45:00",
  "fare": 1250.00,
  "travelDate": "2026-11-01"
}
```

Search trips with
`GET /api/trips/search?source=Bengaluru&destination=Hyderabad&date=2026-11-01`.
The search is case-insensitive and returns an empty list when no trips
match.

## Booking Workflow

Get and initialize trip seat availability with `GET /api/trips/{tripId}/seats`.
Create a booking and hold its seats for five minutes with `POST /api/bookings`:

```json
{
  "userId": 1,
  "tripId": 50,
  "seatIds": [11, 12]
}
```

Process mock payment with `POST /api/bookings/{bookingId}/payment?userId=1`
and body `{"success":true}`. Cancel a confirmed booking with
`POST /api/bookings/{bookingId}/cancel?userId=1`. Read one booking with
`GET /api/bookings/{bookingId}?userId=1`, or list bookings with
`GET /api/bookings/my-bookings?userId=1`.

Each booking stores per-seat fare snapshots and its total. Trip-level
pessimistic locking plus a unique `(trip_id, seat_id)` database constraint
prevents concurrent double booking. Expired holds are released during
availability and booking operations.

Authentication is intentionally not configured in V1. `userId` is supplied
by the client and is used only to associate bookings and check owner IDs; it
is not a secure identity or authorization boundary. Add authentication
before exposing these endpoints to untrusted clients.

The current booking schema uses `booking_v1_table`, `booking_seat_v1_table`,
and `seat_allocation_v1_table`. Existing booking records are not migrated.

## Important Engineering Considerations

-   Hash passwords; never store plaintext passwords.
-   Add authentication and server-derived customer identity before relying
    on booking owner checks.
-   Prevent double-booking with database constraints and transactional
    seat-reservation logic.
-   Verify payment webhooks before marking bookings as paid or
    confirmed.
-   Keep secrets and production credentials out of Git.

## Development Workflow

1.  Implement or update a domain feature.
2.  Build and run the backend.
3.  Test endpoints in Swagger UI.
4.  Add or update automated tests.
5.  Update this README when setup steps or features change.

## License

No license has been specified yet. All rights are reserved unless a
license is added to this repository.
