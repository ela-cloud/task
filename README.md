# 🚗 Car Rental System

A modern car rental management system built with Spring Boot, implementing object-oriented design principles and providing a comprehensive REST API for vehicle reservation management.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technical Stack](#technical-stack)
- [Project Structure](#project-structure)
- [Installation & Setup](#installation--setup)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Usage Examples](#usage-examples)
- [System Architecture](#system-architecture)
- [Contributing](#contributing)

## 🎯 Overview

This Car Rental System allows customers to reserve vehicles of different types for specific time periods. The system manages a limited fleet of vehicles and ensures proper availability tracking and conflict resolution.

### Key Requirements Implemented:
- ✅ Vehicle reservation by type, date, and duration
- ✅ Three vehicle types: Sedan, SUV, and Van
- ✅ Limited number of vehicles per type
- ✅ Comprehensive unit testing
- ✅ Modern Java development practices

## ✨ Features

### Core Functionality
- **Vehicle Management**: Track 3 types of vehicles (Sedan, SUV, Van) with limited quantities
- **Reservation System**: Create, view, and cancel reservations
- **Real-time Availability**: Check vehicle availability for specific time periods
- **Conflict Detection**: Prevent overlapping reservations
- **Customer Management**: Track customer reservation history

### Technical Features
- **REST API**: Complete HTTP API for all operations
- **Input Validation**: Comprehensive data validation with meaningful error messages
- **Exception Handling**: Graceful error handling with detailed responses
- **Logging**: Detailed logging for monitoring and debugging
- **CORS Support**: Ready for frontend integration

## 🛠 Technical Stack

- **Java 17** - Modern LTS version with latest features
- **Spring Boot 3.2.0** - Application framework
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management and build tool
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for tests
- **AssertJ** - Fluent assertions for tests

## 📁 Project Structure

```
car-rental-system/
├── src/
│   ├── main/
│   │   ├── java/com/rental/
│   │   │   ├── CarRentalApplication.java     # Main application class
│   │   │   ├── controller/                   # REST controllers
│   │   │   │   ├── CarRentalController.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── dto/                         # Data transfer objects
│   │   │   │   ├── ReservationRequest.java
│   │   │   │   └── ReservationResponse.java
│   │   │   ├── exception/                   # Custom exceptions
│   │   │   │   ├── CarNotAvailableException.java
│   │   │   │   └── InvalidReservationException.java
│   │   │   ├── model/                       # Domain models
│   │   │   │   ├── Car.java
│   │   │   │   ├── CarType.java
│   │   │   │   ├── Reservation.java
│   │   │   │   └── ReservationStatus.java
│   │   │   ├── repository/                  # Data access layer
│   │   │   │   ├── CarRepository.java
│   │   │   │   ├── CarRepositoryImpl.java
│   │   │   │   ├── ReservationRepository.java
│   │   │   │   └── ReservationRepositoryImpl.java
│   │   │   └── service/                     # Business logic
│   │   │       ├── CarRentalService.java
│   │   │       └── CarRentalServiceImpl.java
│   │   └── resources/
│   │       └── application.properties       # Configuration
│   └── test/                               # Unit tests
│       └── java/com/rental/
│           ├── repository/
│           │   ├── CarRepositoryTest.java
│           │   └── ReservationRepositoryTest.java
│           └── service/
│               └── CarRentalServiceTest.java
├── pom.xml                                 # Maven configuration
└── README.md
```

## 🚀 Installation & Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Installation Steps

1. **Clone the repository**
```bash
git clone <repository-url>
cd car-rental-system
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run tests**
```bash
mvn test
```

4. **Start the application**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8081`

### Alternative Port Configuration
If port 8081 is busy, you can change it in `application.properties`:
```properties
server.port=8082
```

## 📚 API Documentation

### Base URL
```
http://localhost:8081/api/car-rental
```

### Endpoints

#### System Status
```http
GET /api/car-rental/
```
Returns system status and available endpoints.

#### Vehicle Management

**Get all vehicles by type**
```http
GET /api/car-rental/cars
```

**Get vehicles of specific type**
```http
GET /api/car-rental/cars/{carType}
```
- `carType`: SEDAN, SUV, or VAN

**Check availability for period**
```http
GET /api/car-rental/availability?startDateTime={start}&endDateTime={end}
```

**Get available vehicles for period**
```http
GET /api/car-rental/cars/available?carType={type}&startDateTime={start}&endDateTime={end}
```

#### Reservation Management

**Create reservation**
```http
POST /api/car-rental/reservations
Content-Type: application/json

{
  "carType": "SEDAN",
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "startDateTime": "2025-08-20T10:00:00",
  "durationDays": 3
}
```

**Get reservation by ID**
```http
GET /api/car-rental/reservations/{id}
```

**Get customer reservations**
```http
GET /api/car-rental/reservations/customer/{email}
```

**Cancel reservation**
```http
DELETE /api/car-rental/reservations/{id}
```

### Response Examples

**Successful Reservation**
```json
{
  "reservationId": "a62bb74f-bd8f-4678-a284-3c17940470f3",
  "carId": "90924c21-5848-4cb8-8c89-735095cb27c4",
  "licensePlate": "MNO345",
  "carType": "SUV",
  "customerName": "Anna Nowak",
  "customerEmail": "anna@example.com",
  "startDateTime": "2025-08-20T09:00:00",
  "endDateTime": "2025-08-25T09:00:00",
  "durationDays": 5,
  "totalCost": 400.0,
  "status": "ACTIVE",
  "createdAt": "2025-08-13T14:02:51.084752"
}
```

**Error Response**
```json
{
  "timestamp": "2025-08-13T14:04:12.380408",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "details": {
    "startDateTime": "Start date must be in the future"
  }
}
```

## 🧪 Testing

The project includes comprehensive unit tests covering:

- **Repository Layer**: Data access and storage logic
- **Service Layer**: Business logic and edge cases  
- **Validation**: Input validation and error handling
- **Integration**: End-to-end scenarios

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CarRentalServiceTest

# Run tests with coverage report
mvn clean test jacoco:report
```

### Test Coverage
- **22 test cases** covering all major scenarios
- **Repository tests**: CRUD operations and queries
- **Service tests**: Business logic, validation, and edge cases
- **Integration tests**: Complete workflow testing

## 💡 Usage Examples

### Example 1: Check Fleet Status
```bash
curl http://localhost:8081/api/car-rental/cars
```
**Response:** Shows all available vehicles by type

### Example 2: Create a Reservation
```bash
curl -X POST http://localhost:8081/api/car-rental/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "carType": "SUV",
    "customerName": "Jane Smith",
    "customerEmail": "jane@example.com",
    "startDateTime": "2025-08-25T10:00:00",
    "durationDays": 4
  }'
```

### Example 3: Check Availability for Weekend
```bash
curl "http://localhost:8081/api/car-rental/availability?startDateTime=2025-08-23T10:00:00&endDateTime=2025-08-25T10:00:00"
```

### Example 4: View Customer History
```bash
curl http://localhost:8081/api/car-rental/reservations/customer/jane@example.com
```

## 🏗 System Architecture

### Design Patterns Used
- **Repository Pattern**: Abstraction for data access
- **Service Layer Pattern**: Business logic separation
- **DTO Pattern**: Data transfer between layers
- **Builder Pattern**: Object construction (via Lombok)

### Key Components

**Models**: Core domain entities with business logic
- `Car`: Vehicle information and availability
- `Reservation`: Booking details and time management
- `CarType`: Enumeration with pricing information

**Services**: Business logic implementation
- `CarRentalService`: Main business operations
- Validation, availability checking, cost calculation

**Repositories**: Data persistence abstraction
- In-memory implementation for demonstration
- Easily replaceable with database implementation

**Controllers**: REST API endpoints
- Request/response handling
- Input validation
- Error formatting

## 🔧 Configuration

### Application Properties
```properties
# Server Configuration
server.port=8081

# Logging Configuration  
logging.level.com.rental=DEBUG
logging.level.org.springframework=INFO

# Validation Configuration
spring.jackson.deserialization.fail-on-unknown-properties=false
spring.jackson.serialization.write-dates-as-timestamps=false
```

### Fleet Configuration
Default fleet includes:
- **3 Sedans**: Toyota Camry, Honda Accord, BMW 320i
- **3 SUVs**: Toyota RAV4, Honda CR-V, BMW X3  
- **2 Vans**: Ford Transit, Mercedes Sprinter

Daily rates:
- **Sedan**: 50.00 PLN/day
- **SUV**: 80.00 PLN/day
- **Van**: 100.00 PLN/day

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding conventions
- Write unit tests for new features
- Update documentation for API changes
- Use meaningful commit messages

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **Developer** - Technical Assessment Implementation

## 🚀 Future Enhancements

- Database integration (PostgreSQL/MySQL)
- Payment processing
- Email notifications
- Mobile app API
- Admin dashboard
- Vehicle maintenance tracking
- Customer loyalty program
- Geographic location support

---

**Built with ❤️ using Spring Boot and modern Java practices**
