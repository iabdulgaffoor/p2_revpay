# RevPay - Enterprise Digital Wallet & Business Payment Platform

RevPay is a Full-Stack Enterprise Financial Web Application built with Spring Boot, JPA/Hibernate, and a Vanilla HTML/JS/Thymeleaf frontend. It seamlessly integrates Personal Digital Wallet functionality with robust Business Account services.

---

## 🚀 Key Features

### Personal Wallet Features
* **Secure Authentication:** JWT-based login with BCrypt password hashing and security questions.
* **Money Transfers:** Send/Request money via Email, Phone, or Account ID.
* **Wallet Management:** Add/Withdraw funds using linked Payment Methods (Credit/Debit cards).
* **Comprehensive History:** Filterable and searchable transaction history.

### Enterprise & Business Features
* **Business Registration:** Upgraded profile tier with Tax ID and Business Address requirements.
* **Document Verification:** Multipart file upload API to submit business licenses and verifications.
* **Invoicing System:** Generate, send, and track payments mapping directly to transactions.
* **Loan Applications:** Apply for business loans and simulate repayment/EMI mathematically using the `/api/loans/simulate` endpoint.
* **Analytics Dashboard:** Graphical, real-time Chart.js integration displaying cash flow, outstanding invoices, and revenue stats.

### Architecture & Security Enhancements
* **Clean Layered Architecture:** Controllers → Services → Repositories → Entities.
* **Exception Handling:** Centralized `@ControllerAdvice` mapping errors to clean JSON responses.
* **Data Management:** All core entities utilize an abstract `@MappedSuperclass` `Auditable` to track `createdAt` and `lastModifiedAt`. Standardized **Soft Delete** via Hibernate `@SQLDelete` and `@SQLRestriction` to preserve records.
* **API Protection:** In-memory Rate Limiting Filter to throttle excessive traffic, protecting core REST endpoints. (Configurable to 100 req/min/IP).
* **Validation & OpenAPI:** Strict Bean validation applied across DTOs and Controller payloads. Fully documented Swagger API explorer.

---

## 🛠 Tech Stack

* **Backend:** Java 17, Spring Boot 3.x, Spring Web, Spring Security.
* **Database:** H2 Database (In-Memory) by default - easily portable to MySQL/Oracle via `application.properties`.
* **ORM:** Spring Data JPA + Hibernate.
* **Frontend:** Thymeleaf Templates, HTML5, Vanilla JS, Bootstrap-inspired custom CSS, Chart.js.
* **API Documentation:** springdoc-openapi (Swagger UI).
* **Testing:** JUnit 5 + Mockito for Service Layer unit testing.

---

## 📂 Project Structure

```text
src/
 ├── main/
 │   ├── java/com/rev/app/
 │   │   ├── config/          # Spring Security, CORS, OpenAPI concepts
 │   │   ├── controller/      # UI/Thymeleaf Controllers (Route views)
 │   │   ├── dto/             # Data Transfer Objects with Bean Validations
 │   │   ├── entity/          # JPA Entities (extends Auditable)
 │   │   ├── exception/       # Custom Exceptions and GlobalExceptionHandler
 │   │   ├── mapper/          # Entity <-> DTO Mapping utility interfaces
 │   │   ├── repository/      # Spring Data JPA Interfaces
 │   │   ├── rest/            # Secure REST API Controllers
 │   │   ├── security/        # JWT Utils, Auth Filters, Rate Limiting
 │   │   ├── service/         # Core business logic implementations
 │   │   └── Revpay1Application.java
 │   └── resources/
 │       ├── static/          # CSS, JS, Image assets
 │       ├── templates/       # HTML Pages (Thymeleaf)
 │       └── application.properties # H2/MySQL connection flags
 └── test/
     └── java/com/rev/app/    # JUnit Test Cases for core Services
```

---

## ⚙ Setup & Run Instructions

### Prerequisites
* Java Development Kit (JDK) 17 or higher
* Maven 3.6+

### 1. Clone & Build
Navigate to the project directory and run:
`mvn clean install`

### 2. Run the Application
Start the Spring Boot server:
`mvn spring-boot:run`

*The application will launch on port **8099**.*

### 3. Accessing the Application
* **Frontend UI:** Navigate to `http://localhost:8099/`
* **Swagger/OpenAPI UI:** Navigate to `http://localhost:8099/swagger-ui/index.html`
* **Default Database:** H2 Console available at `http://localhost:8099/h2-console`
  * **JDBC URL:** `jdbc:h2:mem:revpaydb`
  * **User:** `sa`
  * **Password:** `password`

---

## 🧪 Testing Checklist
- [x] Register a Personal User and simulate money transfers.
- [x] Register a Business User, toggle the Business Dashboard.
- [x] Check Swagger at `/swagger-ui/index.html` to execute DTO constraint tests (e.g., submitting empty emails).
- [x] Upload theoretical verification documents to `/api/documents/upload`.
- [x] Run `mvn test` to validate the `IWalletServiceImplTest` and `ITransactionServiceImplTest` cases.
