# Product Comparison API

A simplified REST API that provides product details for item comparison features, built with Spring Boot WebFlux and following hexagonal architecture principles.

## 🏗️ Architecture

This project follows **Hexagonal Architecture** (Ports and Adapters) with clear separation of concerns:

- **Domain Layer**: Core business logic, entities, and interfaces
- **Application Layer**: Use cases, DTOs, and orchestration
- **Infrastructure Layer**: External concerns (JSON persistence, REST controllers)

### Project Structure

```
src/main/java/com/meli/technical/exam/api/products/
├── domain/
│   ├── model/          # Product and Specification entities
│   ├── port/           # Repository interfaces (ports)
│   └── service/        # Domain services
├── application/
│   ├── usecase/        # Business use cases
│   ├── dto/            # Data Transfer Objects
│   └── mapper/         # Entity-DTO mappers
├── infrastructure/
│   ├── persistence/    # JSON file repository implementation
│   ├── web/           # REST controllers
│   └── config/        # Configuration classes
└── shared/
    └── exception/     # Custom exceptions and error handling
```

## 🚀 Features

- **RESTful API** with reactive programming (WebFlux)
- **Product Comparison** endpoint for multiple items
- **Pagination support** for product listings
- **Comprehensive error handling** with structured responses
- **Input validation** with detailed error messages
- **Logging and monitoring** with Spring Boot Actuator
- **JSON file-based persistence** (no external database required)
- **CORS support** for frontend integration

## 📋 API Endpoints

### Get Product by ID
```http
GET /api/v1/products/{id}
```

**Response:**
```json
{
  "id": "1",
  "name": "Samsung Galaxy S24 Ultra",
  "imageUrl": "https://images.samsung.com/...",
  "description": "The most powerful Galaxy S ever...",
  "price": 1299.99,
  "rating": 4.8,
  "specifications": [
    {
      "key": "Display",
      "value": "6.8-inch Dynamic AMOLED 2X"
    }
  ]
}
```

### Compare Multiple Products
```http
GET /api/v1/products/compare?ids=1,2,3
```

**Response:**
```json
{
  "products": [...],
  "totalProducts": 3,
  "comparisonTimestamp": "2024-01-01T12:00:00Z",
  "requestedIds": ["1", "2", "3"]
}
```

### Get All Products (Paginated)
```http
GET /api/v1/products?page=0&size=10
```

**Response:**
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 50,
  "totalPages": 5,
  "hasNext": true,
  "hasPrevious": false
}
```

### Health Check
```http
GET /api/v1/products/health
```

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.5.5**
- **Spring WebFlux** (Reactive programming)
- **Spring Boot Actuator** (Monitoring)
- **Jackson** (JSON processing)
- **Bean Validation** (Input validation)
- **JUnit 5** & **Mockito** (Testing)
- **Gradle** (Build tool)

## 🔧 Running the Application

### Prerequisites
- Java 17 or higher
- Gradle (or use the included Gradle wrapper)

### Build and Run
```bash
# Build the application
./gradlew build

# Run the application
./gradlew bootRun

# Or run the JAR file
java -jar build/libs/products-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

### Testing
```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport
```

## 📊 Sample Data

The application comes with 10 pre-loaded smartphone products in `src/main/resources/data/products.json`:

- Samsung Galaxy S24 Ultra
- iPhone 15 Pro Max  
- Google Pixel 8 Pro
- OnePlus 12
- Xiaomi 14 Ultra
- Sony Xperia 1 VI
- Nothing Phone (2a)
- ASUS ROG Phone 8 Pro
- Motorola Edge 50 Ultra
- Huawei Pura 70 Ultra

## 🎯 Design Patterns Used

- **Repository Pattern**: Data access abstraction
- **Strategy Pattern**: Different comparison strategies (extensible)
- **Builder Pattern**: Complex DTO construction
- **Command Pattern**: Use case encapsulation
- **Decorator Pattern**: Cross-cutting concerns (logging, validation)

## 📏 SOLID Principles

- **SRP**: Single responsibility per class/method
- **OCP**: Extension through interfaces and polymorphism  
- **LSP**: Proper interface implementations
- **ISP**: Focused, cohesive interfaces
- **DIP**: Dependency injection and abstraction

## 🔍 Error Handling

The API provides structured error responses:

```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "path": "/api/v1/products/999"
}
```

For validation errors:
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input provided",
  "path": "/api/v1/products/compare",
  "validationErrors": [
    {
      "field": "ids",
      "rejectedValue": "",
      "message": "Product IDs cannot be empty"
    }
  ]
}
```

## 📈 Monitoring

Spring Boot Actuator endpoints available at:

- `/actuator/health` - Application health status
- `/actuator/info` - Application information  
- `/actuator/metrics` - Application metrics

## 🔧 Configuration

Key application properties in `application.properties`:

```properties
# Server
server.port=8080

# Logging
logging.level.com.meli.technical.exam.api.products=INFO

# Custom settings
app.products.max-comparison-items=10
app.products.default-page-size=10
app.products.max-page-size=100
```

## 🧪 Testing Strategy

- **Unit Tests**: Domain models, services, mappers
- **Integration Tests**: Repository implementations
- **Web Layer Tests**: Controller endpoints with WebTestClient
- **Test Coverage**: Comprehensive coverage of business logic

## 🚀 Future Enhancements

- Database integration (PostgreSQL, MongoDB)
- Product search and filtering
- Advanced comparison features
- Caching with Redis
- API rate limiting
- OpenAPI/Swagger documentation
- Docker containerization
- Kubernetes deployment

## 📝 API Usage Examples

### cURL Examples

```bash
# Get a specific product
curl -X GET "http://localhost:8080/api/v1/products/1"

# Compare multiple products
curl -X GET "http://localhost:8080/api/v1/products/compare?ids=1,2,3"

# Get paginated products
curl -X GET "http://localhost:8080/api/v1/products?page=0&size=5"

# Health check
curl -X GET "http://localhost:8080/api/v1/products/health"
```

### JavaScript/Fetch Examples

```javascript
// Get product by ID
const product = await fetch('http://localhost:8080/api/v1/products/1')
  .then(response => response.json());

// Compare products
const comparison = await fetch('http://localhost:8080/api/v1/products/compare?ids=1,2,3')
  .then(response => response.json());

// Get paginated products  
const products = await fetch('http://localhost:8080/api/v1/products?page=0&size=10')
  .then(response => response.json());
```

## 👨‍💻 Development

This API was built following backend best practices:

- ✅ Clean architecture with clear separation of concerns
- ✅ Reactive programming with WebFlux for better performance
- ✅ Comprehensive error handling and validation
- ✅ Extensive logging for debugging and monitoring
- ✅ Unit and integration testing
- ✅ SOLID principles and design patterns
- ✅ Proper HTTP status codes and REST conventions
- ✅ Input validation and sanitization
- ✅ Configuration externalization

## 📄 License

This project is part of a technical assessment and is for demonstration purposes.