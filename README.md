# Product Comparison API

A sophisticated REST API that provides intelligent product comparison and analysis features, built with Spring Boot WebFlux and following **Domain-Driven Design (DDD)**, **Hexagonal Architecture**, and **Clean Architecture** principles.

## Architecture Overview

This project demonstrates a **production-ready architecture** combining multiple architectural patterns:

### **Domain-Driven Design (DDD)**
- **Entities**: `Product` with proper business invariants
- **Value Objects**: `ProductId`, `Price`, `Rating`, `Specification`  
- **Domain Services**: `ProductService`, `ProductComparisonAnalyzerService`
- **Domain Events**: `ProductComparedEvent`, `ProductViewedEvent`, `ProductUpdatedEvent`
- **Repositories**: `ProductRepository` interface in domain layer

### **Hexagonal Architecture (Ports & Adapters)**
- **Ports**: Repository interfaces and domain event publisher
- **Inbound Adapters**: REST API controllers
- **Outbound Adapters**: JSON persistence, event publishing
- **Application Core**: Pure domain logic isolated from infrastructure

### **Clean Architecture Compliance**
- **Clear layer separation**: Domain → Application → Infrastructure
- **Dependency Rule**: Dependencies point inward toward domain
- **Use Cases**: Business logic orchestration
- **Interface Adapters**: DTOs and mappers for data transformation

## Project Structure

```
src/main/java/com/meli/technical/exam/api/products/
├── ProductsApplication.java                    # Main Spring Boot application
├── domain/                                   # 🏛️ Domain Layer (Core Business Logic)
│   ├── model/                                # Domain Entities & Value Objects
│   │   ├── Product.java                      # Aggregate Root with business rules
│   │   ├── ProductId.java                    # Value Object with validation
│   │   ├── Price.java                        # Value Object with business rules
│   │   ├── Rating.java                       # Value Object with constraints  
│   │   └── Specification.java                # Product specification entity
│   ├── service/                              # Domain Services
│   │   ├── ProductService.java               # Core product operations
│   │   ├── ProductComparisonAnalyzerService.java # Comparison business logic
│   │   └── analysis/                         # Analysis Services & Strategies
│   │       ├── ProductStats.java             # Product statistics collector
│   │       ├── ProductStatsCollector.java    # Statistical analysis
│   │       └── strategy/                     # Strategy Pattern Implementation
│   │           ├── ProductAnalysisStrategy.java      # Base strategy interface
│   │           ├── PriceAnalysisStrategy.java        # Price comparison analysis
│   │           ├── RatingAnalysisStrategy.java       # Rating analysis & insights
│   │           ├── SpecificationAnalysisStrategy.java # Feature comparison
│   │           ├── RecommendationEngine.java         # AI-like recommendations
│   │           └── SummaryGenerationStrategy.java    # Intelligent summaries
│   ├── event/                                # Event-Driven Architecture
│   │   ├── DomainEvent.java                  # Base domain event class
│   │   ├── DomainEventPublisher.java         # Event publisher interface
│   │   ├── ProductComparedEvent.java         # Product comparison event
│   │   ├── ProductViewedEvent.java           # Product view tracking
│   │   └── ProductUpdatedEvent.java          # Product modification event
│   ├── repository/                           # Repository Pattern (Ports)
│   │   └── ProductRepository.java            # Data access abstraction
│   ├── validator/                            # Domain Validation
│   │   ├── ProductValidator.java             # Validation interface
│   │   ├── ProductBusinessRulesValidator.java # Business rule validation
│   │   └── ProductInputFormatValidator.java  # Input format validation
│   └── exception/                            # Domain Exceptions
│       ├── InvalidProductException.java      # Invalid product data
│       ├── ProductNotFoundException.java     # Product not found
│       ├── ProductComparisonException.java   # Comparison errors
│       ├── ProductDataException.java         # Data-related errors
│       └── ProductValidationException.java   # Validation errors
├── application/                              # Application Layer (Use Cases)
│   ├── usecase/                              # Business Use Cases
│   │   └── ProductComparisonUseCase.java     # Product comparison orchestration
│   ├── service/                              # Application Services
│   │   └── ProductApplicationService.java    # Application-level coordination
│   ├── dto/                                  # Data Transfer Objects
│   │   ├── request/                          # Incoming DTOs
│   │   │   ├── ProductDto.java               # Product data transfer
│   │   │   └── SpecificationDto.java         # Specification data transfer
│   │   └── response/                         # Outgoing DTOs
│   │       ├── PaginatedResponseDto.java     # Pagination response
│   │       └── comparison/                   # Advanced Comparison DTOs
│   │           ├── ComparisonResponseDto.java        # Main comparison response
│   │           ├── ComparisonSummaryDto.java         # Intelligent summary
│   │           ├── PriceAnalysisDto.java            # Price analysis insights
│   │           ├── RatingAnalysisDto.java           # Rating analysis insights  
│   │           ├── SpecificationAnalysisDto.java    # Feature comparison
│   │           └── RecommendationDto.java           # Smart recommendations
│   └── mapper/                               # Object Mapping
│       └── ProductMapper.java                # Entity-DTO transformations
└── infrastructure/                           # Infrastructure Layer
    ├── adapter/                              # Hexagonal Architecture Adapters
    │   ├── in/web/                           # Inbound Adapters
    │   │   └── ProductController.java        # REST API controller
    │   └── out/persistence/                  # Outbound Adapters
    │       └── JsonProductRepository.java    # JSON-based persistence
    ├── event/                                # Event Infrastructure
    │   └── SimpleEventPublisher.java         # Event publishing implementation
    ├── web/                                  # Web Infrastructure
    │   └── GlobalExceptionHandler.java       # Global error handling
    └── config/                               # Configuration
        └── WebConfiguration.java             # Web/CORS configuration
```

## Advanced Design Patterns

### **1. Strategy Pattern** - Analysis Strategies
Multiple interchangeable algorithms for product analysis:
```java
// Price analysis with statistical insights
PriceAnalysisStrategy → Cheapest/Most Expensive/Average/Distribution

// Rating analysis with quality insights  
RatingAnalysisStrategy → Best/Worst Rated/Averages/Quality Distribution

// Specification analysis with feature comparison
SpecificationAnalysisStrategy → Common/Unique Features/Most Featured Product

// Intelligent recommendation engine
RecommendationEngine → Value-based/Quality-based/Budget Recommendations

// Summary generation with insights
SummaryGenerationStrategy → Comprehensive Analysis Summary
```

### **2. Repository Pattern** - Data Access Abstraction
```java
interface ProductRepository {
    Mono<Product> findById(String id);
    Flux<Product> findByIds(List<String> ids);  
    Flux<Product> findAllPaginated(int page, int size);
    Mono<Long> count();
}
```

### **3. Event-Driven Architecture** - Domain Events
```java
@DomainEvent
ProductComparedEvent → Published when products are compared
ProductViewedEvent   → Published when a product is viewed  
ProductUpdatedEvent  → Published when product data changes
```

### **4. Use Case Pattern** - Business Logic Orchestration
```java
ProductComparisonUseCase → Orchestrates product comparison workflow
ProductApplicationService → Coordinates application-level operations
```

### **5. Builder Pattern** - Complex Object Construction
Extensive use of Lombok `@Builder` for DTOs and domain objects

### **6. Validator Pattern** - Domain Validation
Multiple validators for different validation concerns:
- Business rules validation
- Input format validation  
- Domain invariant validation

## Advanced Features

### **Intelligent Product Comparison**
The API provides sophisticated product comparison with multiple analysis dimensions:

#### **Price Analysis**
- **Cheapest/Most Expensive** product identification
- **Average price** calculation across compared products
- **Price range** and **distribution analysis**
- **Price-to-value ratio** insights

#### **Rating Analysis**  
- **Best/Worst rated** product identification
- **Rating distribution** and **quality insights**
- **Highly-rated products** filtering (configurable thresholds)
- **Average rating** calculations with variance analysis

#### **Specification Analysis**
- **Common specifications** across all compared products
- **Unique features** identification per product
- **Most featured product** detection
- **Specification comparison matrix**

#### **Intelligent Recommendations**
- **Best Value** recommendations (price-to-feature ratio)
- **Best Quality** recommendations (rating-based)
- **Budget Options** for cost-conscious buyers
- **Premium Options** for feature-focused buyers

#### **Smart Summary Generation**
- **Automated insights** generation
- **Key differences** highlighting
- **Actionable recommendations** 
- **Comprehensive analysis** conclusion

### **Event-Driven Architecture**
- **Domain event publishing** for analytics and tracking
- **Product view tracking** for usage analytics
- **Comparison analytics** for business intelligence
- **Extensible event handling** for future integrations

### **Reactive Programming**
- **Non-blocking I/O** with Spring WebFlux
- **Reactive streams** with Mono and Flux
- **Better performance** under load
- **Scalable architecture** for high throughput

## API Endpoints

### **Base URL:** `/api/v1/products`

#### **1. Get Product by ID**
```http
GET /api/v1/products/{id}
```
**Features:**
- Product detail retrieval
- Automatic view event publishing
- Error handling for non-existent products

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
    },
    {
      "key": "Storage", 
      "value": "256GB/512GB/1TB"
    }
  ]
}
```

#### **2. Advanced Product Comparison**
```http
GET /api/v1/products/compare?ids=1,2,3
```
**Features:**
- **Multi-dimensional analysis** across price, rating, and specifications
- **Intelligent recommendations** based on comparison results
- **Smart summary generation** with actionable insights
- **Comparison event publishing** for analytics

**Enhanced Response:**
```json
{
  "products": [...],
  "totalProducts": 3,
  "requestedIds": ["1", "2", "3"],
  "comparisonTimestamp": "2024-01-01T12:00:00Z",
  "priceAnalysis": {
    "cheapestProduct": {...},
    "mostExpensiveProduct": {...},
    "averagePrice": 899.99,
    "priceRange": 600.00,
    "priceDistribution": {
      "budget": 299.99,
      "midRange": 699.99, 
      "premium": 1299.99
    }
  },
  "ratingAnalysis": {
    "bestRatedProduct": {...},
    "lowestRatedProduct": {...},
    "averageRating": 4.4,
    "ratingDistribution": {
      "excellent": 2,
      "good": 1, 
      "average": 0
    },
    "highlyRatedProducts": [...]
  },
  "specificationAnalysis": {
    "commonSpecifications": ["Display", "Camera", "Battery"],
    "uniqueSpecifications": {
      "Samsung Galaxy S24 Ultra": ["S Pen", "Ultra-Wide Camera"],
      "iPhone 15 Pro Max": ["Action Button", "ProRAW"]
    },
    "specificationComparison": {...},
    "mostFeaturedProduct": {...}
  },
  "recommendations": [
    {
      "type": "BEST_VALUE",
      "title": "Best Value for Money",
      "description": "Excellent features at competitive price",
      "recommendedProduct": {...},
      "reason": "Best price-to-feature ratio"
    },
    {
      "type": "PREMIUM_CHOICE",
      "title": "Premium Option", 
      "description": "Top-tier specifications and performance",
      "recommendedProduct": {...},
      "reason": "Highest rating and most features"
    }
  ],
  "summary": {
    "bestValue": {...},
    "bestQuality": {...},
    "budgetOption": {...},
    "insights": [
      "Samsung Galaxy S24 Ultra offers the most features",
      "iPhone 15 Pro Max has the highest user rating",
      "Google Pixel 8 Pro provides best value for money"
    ],
    "conclusion": "For premium features choose Samsung, for reliability choose iPhone, for value choose Google Pixel"
  }
}
```

#### **3. Paginated Product Listing**
```http
GET /api/v1/products?page=0&size=10
```
**Features:**
- **Intelligent pagination** with optimized queries
- **Large page size handling** (switches to non-paginated for efficiency)
- **Parameter validation** with fallback defaults

**Response:**
```json
{
  "content": [...],
  "page": 0,
  "size": 10, 
  "totalElements": 10,
  "totalPages": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

#### **4. Health Check**
```http
GET /api/v1/products/health
```

## Technology Stack

### **Core Technologies**
- **Java 17** - Latest LTS with modern language features
- **Spring Boot 3.5.5** - Latest Spring Boot framework  
- **Spring WebFlux** - Reactive web framework for non-blocking I/O
- **Project Reactor** - Reactive programming library (Mono/Flux)

### **Architecture & Patterns**
- **MapStruct 1.5.5** - Type-safe object mapping
- **Lombok 1.18.34** - Code generation and boilerplate reduction
- **Bean Validation** - Comprehensive input validation
- **Spring Boot Actuator** - Production-ready monitoring

### **Development & Testing**
- **JUnit 5** - Modern testing framework
- **Spring Boot Test** - Integration testing support  
- **WebTestClient** - Reactive web testing
- **Mockito** - Mocking framework
- **Gradle** - Build automation

### **Data & Serialization**
- **Jackson** - High-performance JSON processing
- **SLF4J** - Logging facade

## Running the Application

### **Prerequisites**
- **Java 17** or higher
- **Gradle** (or use included wrapper)

### **Quick Start**
```bash
# Clone and navigate to project
git clone https://github.com/josedavidgonzalezh/MeLi_Tech/tree/master
cd products

# Build the application  
./gradlew build

# Run the application
./gradlew bootRun

# Alternative: Run JAR directly
java -jar build/libs/products-0.0.1-SNAPSHOT.jar
```

**Application starts on:** `http://localhost:8080`

### **Testing**
```bash
# Run all tests
./gradlew test

# Run with coverage report
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests "ProductComparisonAnalyzerServiceTest"

# Run integration tests only
./gradlew test --tests "*IntegrationTest"
```

## Sample Data

The application includes **10 premium smartphone products** with detailed specifications:

| Product | Price | Rating | Key Features |
|---------|-------|--------|-------------|
| **Samsung Galaxy S24 Ultra** | $1,299.99 | 4.8★ | S Pen, 200MP Camera, 5000mAh |
| **iPhone 15 Pro Max** | $1,199.99 | 4.7★ | A17 Pro, Action Button, ProRAW |
| **Google Pixel 8 Pro** | $999.99 | 4.6★ | AI Features, Pure Android, Magic Eraser |
| **OnePlus 12** | $799.99 | 4.5★ | 120Hz LTPO, 5400mAh, Fast Charging |
| **Xiaomi 14 Ultra** | $1,099.99 | 4.4★ | Leica Camera, 90W Charging |
| **Sony Xperia 1 VI** | $1,199.99 | 4.3★ | 4K Display, Pro Camera Controls |
| **Nothing Phone (2a)** | $399.99 | 4.2★ | Unique Design, Clean Android |
| **ASUS ROG Phone 8 Pro** | $1,199.99 | 4.6★ | Gaming Performance, 6000mAh |
| **Motorola Edge 50 Ultra** | $899.99 | 4.4★ | 144Hz Display, 125W Charging |
| **Huawei Pura 70 Ultra** | $1,299.99 | 4.5★ | Advanced Photography, HarmonyOS |

## Domain Model Deep Dive

### **Product Aggregate (Entity)**
```java
@Entity
public class Product {
    private final ProductId id;           // Value Object
    private String name;                  // Business Rules: Required, max 200 chars
    private String imageUrl;              // Business Rules: Valid URL, max 500 chars  
    private String description;           // Business Rules: Required, max 1000 chars
    private Price price;                  // Value Object with validation
    private Rating rating;                // Value Object with constraints
    private List<Specification> specifications; // Specifications list
    
    // Business invariants enforced in constructor and setters
    // Domain events published on state changes
}
```

### **Value Objects**
```java
// ProductId - Identity with validation
public class ProductId {
    private final String value;
    // Validation: Non-null, non-empty, trimmed
}

// Price - Monetary value with constraints  
public class Price {
    private final BigDecimal value;
    // Constraints: Non-negative, max $999,999.99, 2 decimal places
}

// Rating - Product rating with business rules
public class Rating {
    private final BigDecimal value;  
    // Constraints: 0.0 to 5.0 range, 1 decimal precision
}

// Specification - Product feature
public class Specification {
    private final String key;    // Feature name
    private final String value;  // Feature value
}
```

## Comprehensive Testing Strategy

### **Test Architecture**
```
src/test/java/
├── unit/                           # Unit Tests (85% coverage target)
│   ├── domain/                     # Domain layer tests
│   │   ├── model/                  # Entity and Value Object tests
│   │   ├── service/                # Domain service tests  
│   │   └── validator/              # Validation logic tests
│   ├── application/                # Application layer tests
│   │   ├── usecase/                # Use case tests with mocks
│   │   ├── service/                # Application service tests
│   │   └── mapper/                 # Mapping logic tests
│   └── infrastructure/             # Infrastructure tests
│       ├── persistence/            # Repository implementation tests
│       └── web/                    # Controller tests
├── integration/                    # Integration Tests
│   ├── api/                        # End-to-end API tests
│   ├── repository/                 # Data access integration
│   └── comparison/                 # Comparison feature integration
└── fixtures/                       # Test Data & Utilities
    ├── ProductTestFactory.java     # Test object creation
    └── TestDataBuilder.java        # Builder pattern for tests
```

### **Testing Technologies & Approaches**
- **Unit Tests**: `@ExtendWith(MockitoExtension.class)` for fast, isolated tests
- **Integration Tests**: `@SpringBootTest` with `WebTestClient` for realistic scenarios  
- **Test Slices**: `@WebFluxTest` for controller-only tests
- **Test Containers**: Ready for database integration testing
- **Property-Based Testing**: Ready for complex validation scenarios

## 🔍 Advanced Error Handling

### **Structured Error Responses**

#### **Domain Exception (404 Not Found)**
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 404,
  "error": "Product not found with id: 999", 
  "message": "The requested product does not exist in our catalog",
  "path": "/api/v1/products/999",
  "traceId": "abc123def456"
}
```

#### **Validation Error (400 Bad Request)**
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
    },
    {
      "field": "ids", 
      "rejectedValue": "invalid-id-format",
      "message": "Product ID format is invalid"
    }
  ]
}
```

#### **Business Rule Violation (422 Unprocessable Entity)**
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 422, 
  "error": "Business Rule Violation",
  "message": "Cannot compare more than 10 products at once",
  "path": "/api/v1/products/compare",
  "businessRule": "MAX_COMPARISON_LIMIT",
  "limit": 10,
  "provided": 15
}
```

## Monitoring & Observability

### **Spring Boot Actuator Endpoints**
```http
GET /actuator/health     # Application health status
GET /actuator/info       # Application information
GET /actuator/metrics    # Application metrics
GET /actuator/prometheus # Metrics in Prometheus format
```

### **Custom Metrics** (Future Enhancement)
- **Comparison request rates** - Track API usage patterns
- **Popular product combinations** - Business intelligence
- **Response time distributions** - Performance monitoring  
- **Error rate tracking** - Reliability metrics

### **Distributed Tracing** (Future Enhancement)
- **Request correlation IDs** - Track requests across services
- **Performance bottleneck identification** - Optimize slow operations
- **Error correlation** - Debug issues across system boundaries

## Future Enhancements & Roadmap

### **Phase 1: CRUD Operations (POST/PUT/DELETE)**

#### **Create Product (POST)**
```http
POST /api/v1/products
Content-Type: application/json

{
  "name": "New Smartphone",
  "imageUrl": "https://example.com/image.jpg",
  "description": "Revolutionary new smartphone with advanced features...",
  "price": 899.99,
  "rating": 4.5,
  "specifications": [
    {
      "key": "Display",
      "value": "6.5-inch AMOLED"
    },
    {
      "key": "Storage", 
      "value": "256GB"
    }
  ]
}
```

**Implementation Plan:**
```java
// Enhanced Use Case
@UseCase
public class CreateProductUseCase {
    // Domain validation with ProductValidator
    // Duplicate detection by name/specifications  
    // Auto-generated ProductId with UUID
    // Domain event: ProductCreatedEvent
    // Business rules: Name uniqueness, price validation
}

// Enhanced Controller  
@RestController
public class ProductController {
    @PostMapping
    public Mono<ResponseEntity<ProductDto>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        // Input validation with Bean Validation
        // Rate limiting for product creation
        // Authorization checks (Future: Admin-only)
        // Response: 201 Created with Location header
    }
}

// New DTOs
public record CreateProductRequest(
    @NotBlank @Size(max = 200) String name,
    @NotBlank @URL @Size(max = 500) String imageUrl,
    @NotBlank @Size(max = 1000) String description,
    @NotNull @Positive @DecimalMax("999999.99") BigDecimal price,
    @NotNull @DecimalMin("0.0") @DecimalMax("5.0") Double rating,
    @Valid List<CreateSpecificationRequest> specifications
) {}
```

#### **Update Product (PUT)**  
```http
PUT /api/v1/products/{id}
Content-Type: application/json

{
  "name": "Updated Smartphone Name",
  "description": "Updated description with new features...",
  "price": 799.99,
  "rating": 4.6,
  "specifications": [...]
}
```

**Implementation Plan:**
```java
// Enhanced Use Case
@UseCase  
public class UpdateProductUseCase {
    // Optimistic locking with version field
    // Partial updates support (PATCH semantics)
    // Change tracking for audit log
    // Domain event: ProductUpdatedEvent with change details
    // Business rules: Price change validation, rating recalculation
}

// Enhanced Domain Model
@Entity
public class Product {
    private Long version;           // Optimistic locking
    private Instant lastModified;   // Audit timestamp
    private String lastModifiedBy;  // Audit user (Future)
    
    // Change tracking method
    public ProductUpdatedEvent updateWith(UpdateProductCommand command) {
        // Track changes, validate business rules
        // Return domain event with change details
    }
}
```

#### **Delete Product (DELETE)**
```http
DELETE /api/v1/products/{id}
```

**Implementation Plan:**
```java
// Soft Delete Implementation
@UseCase
public class DeleteProductUseCase {
    // Soft delete with deletedAt timestamp
    // Cascade delete for related entities
    // Domain event: ProductDeletedEvent
    // Business rules: Cannot delete if in active comparisons
}

// Enhanced Product Entity
@Entity  
public class Product {
    private Instant deletedAt;      // Soft delete timestamp
    private String deletionReason;  // Audit trail
    
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
```

### **Phase 2: Advanced Features**

#### **Advanced Search & Filtering**
```http
GET /api/v1/products/search?
    q=smartphone&
    minPrice=500&
    maxPrice=1000&
    minRating=4.0&
    specifications=Display:AMOLED,Storage:256GB&
    sortBy=price&
    sortDirection=asc&
    page=0&
    size=10
```

**Implementation Plan:**
```java
// Search Service with Strategy Pattern
public interface ProductSearchStrategy {
    Flux<Product> search(ProductSearchCriteria criteria);
}

@Service
public class ElasticsearchProductSearchStrategy implements ProductSearchStrategy {
    // Full-text search capabilities
    // Faceted search with aggregations  
    // Auto-complete suggestions
    // Search result ranking
}

// Search Criteria Value Object
public record ProductSearchCriteria(
    Optional<String> query,
    Optional<PriceRange> priceRange, 
    Optional<RatingRange> ratingRange,
    Map<String, String> specifications,
    SortCriteria sortCriteria,
    PaginationCriteria pagination
) {}
```

#### **Advanced Analytics & Insights**
```http
GET /api/v1/products/analytics/trends
GET /api/v1/products/analytics/popular-comparisons  
GET /api/v1/products/analytics/price-history/{id}
```

**Implementation Plan:**
```java
// Analytics Service
@Service
public class ProductAnalyticsService {
    // Popular products tracking
    // Comparison patterns analysis
    // Price trend analysis
    // User behavior insights
}

// Event Sourcing for Analytics
@EventSourcing
public class ProductEventStore {
    // Store all domain events for analysis
    // Replay events for analytics
    // Time-series data for trends
}
```

### **Phase 3: Enterprise Features**

#### **Security & Authorization**
```java
// JWT-based Authentication
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/api/v1/products")
public Mono<ResponseEntity<ProductDto>> createProduct(...) {}

@PreAuthorize("hasRole('USER')")  
@GetMapping("/api/v1/products/{id}")
public Mono<ResponseEntity<ProductDto>> getProduct(...) {}

// Rate Limiting
@RateLimit(requests = 100, per = TimeUnit.HOURS)
@PostMapping("/api/v1/products")
public Mono<ResponseEntity<ProductDto>> createProduct(...) {}
```

#### **Database Integration**
```java
// PostgreSQL with R2DBC
@Repository
public class PostgresProductRepository implements ProductRepository {
    // Reactive database operations
    // Connection pooling
    // Transaction management
    // Database migrations with Flyway
}

// MongoDB Alternative  
@Repository
public class MongoProductRepository implements ProductRepository {
    // Document-based storage
    // Flexible schema evolution
    // Rich query capabilities
}
```

#### **Microservices Architecture**
```yaml
# Docker Compose for development
version: '3.8'
services:
  products-api:
    build: .
    ports: ["8080:8080"]
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on: [postgres, redis, elasticsearch]
      
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: products
      POSTGRES_USER: products  
      POSTGRES_PASSWORD: products
      
  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]
    
  elasticsearch:
    image: elasticsearch:8.11
    ports: ["9200:9200"]
    environment:
      - discovery.type=single-node
```

#### **Cloud-Native Deployment**
```yaml
# Kubernetes Deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: products-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: products-api
  template:
    metadata:
      labels:
        app: products-api
    spec:
      containers:
      - name: products-api
        image: products-api:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"  
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

## Configuration & Customization

### **Application Properties**
```properties
# Server Configuration
server.port=8080
server.netty.connection-timeout=10s

# Application Settings
app.products.max-comparison-items=10
app.products.default-page-size=10  
app.products.max-page-size=100
app.products.cache-ttl=3600

# Performance Tuning
spring.webflux.multipart.max-in-memory-size=1MB
spring.reactor.netty.ioWorkerCount=4

# Monitoring
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true

# Logging  
logging.level.com.meli.technical.exam.api.products=INFO
logging.level.org.springframework.web.reactive=DEBUG
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

### **Environment-Specific Configurations**
```yaml
# application-production.yml
spring:
  webflux:
    base-path: /api/v1
  
app:
  products:
    max-comparison-items: 20
    enable-analytics: true
    cache-provider: redis
    
logging:
  level:
    root: WARN
    com.meli.technical.exam.api.products: INFO
    
management:
  endpoints:
    web:
      exposure:
        include: health,metrics
```

## API Usage Examples

### **Advanced cURL Examples**
```bash
# Create new product
curl -X POST "http://localhost:8080/api/v1/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Smartphone",
    "imageUrl": "https://example.com/phone.jpg", 
    "description": "Revolutionary smartphone...",
    "price": 899.99,
    "rating": 4.5,
    "specifications": [
      {"key": "Display", "value": "6.5-inch AMOLED"},
      {"key": "Storage", "value": "256GB"}
    ]
  }'

# Update product
curl -X PUT "http://localhost:8080/api/v1/products/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Smartphone",
    "price": 799.99,
    "rating": 4.6
  }'

# Advanced search
curl -X GET "http://localhost:8080/api/v1/products/search?q=samsung&minPrice=500&maxPrice=1000&minRating=4.0"

# Get analytics
curl -X GET "http://localhost:8080/api/v1/products/analytics/trends"
```

### **JavaScript/TypeScript Integration**
```typescript
// Enhanced TypeScript client
interface ProductComparisonResponse {
  products: Product[];
  totalProducts: number;
  priceAnalysis: PriceAnalysis;
  ratingAnalysis: RatingAnalysis; 
  specificationAnalysis: SpecificationAnalysis;
  recommendations: Recommendation[];
  summary: ComparisonSummary;
}

class ProductsApiClient {
  private baseUrl = 'http://localhost:8080/api/v1/products';
  
  async compareProducts(ids: string[]): Promise<ProductComparisonResponse> {
    const response = await fetch(`${this.baseUrl}/compare?ids=${ids.join(',')}`);
    return response.json();
  }
  
  async createProduct(product: CreateProductRequest): Promise<Product> {
    const response = await fetch(this.baseUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(product)
    });
    return response.json();
  }
  
  async searchProducts(criteria: SearchCriteria): Promise<PaginatedResponse<Product>> {
    const params = new URLSearchParams(criteria as any);
    const response = await fetch(`${this.baseUrl}/search?${params}`);
    return response.json();
  }
}
```

## Best Practices Used

### **Clean Architecture**
- **Dependency inversion** - Domain doesn't depend on infrastructure
- **Single responsibility** - Each class has one reason to change  
- **Open/closed principle** - Open for extension, closed for modification
- **Interface segregation** - Focused, cohesive interfaces
- **Liskov substitution** - Proper inheritance and polymorphism

### **Domain-Driven Design**
- **Ubiquitous language** - Consistent terminology across code and business
- **Bounded contexts** - Clear domain boundaries
- **Aggregate design** - Proper entity clustering and invariants
- **Domain events** - Decoupled communication within domain
- **Value objects** - Immutable, behavior-rich objects

### **Reactive Programming** 
- **Non-blocking I/O** - Better resource utilization
- **Backpressure handling** - Proper flow control
- **Error handling** - Reactive error propagation
- **Composition** - Declarative data transformation pipelines

**Technologies Showcased:**
- **Domain-Driven Design** with rich domain models
- **Hexagonal Architecture** with clear port/adapter separation  
- **Clean Architecture** with proper layer isolation
- **Reactive Programming** with Spring WebFlux
- **Event-Driven Architecture** with domain events
- **Strategy Pattern** for extensible business logic
- **Comprehensive Testing** with unit and integration tests
- **Production-Ready** monitoring and error handling