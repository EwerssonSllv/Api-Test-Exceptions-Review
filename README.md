# Product API - Detailed README

## Overview

This project is a Kotlin-based Spring Boot REST API for managing users and products. It features authentication via JWT tokens, user registration, login, and basic CRUD operations for products. Testing is included using both Spring's `TestRestTemplate` and `Rest-Assured`.

---

## Project Structure

### 1. Entities & DTOs

#### User.kt

* Represents a user in the system.
* Implements `UserDetails` for Spring Security integration.
* Fields:

  * `id`: UUID, unique identifier.
  * `login`: User login.
  * `password`: Encrypted password.
  * `role`: Enum representing `ADMIN` or `USER`.
* Methods:

  * `getAuthorities()`: Returns roles for Spring Security.
  * Standard `equals()` and `hashCode()` for entity equality.

#### UserRole.kt

* Enum class with two roles: `ADMIN` and `USER`.
* Used to differentiate access levels in the application.

#### UserDTO.kt

* Data Transfer Object for returning user information without exposing sensitive data like passwords.
* `fromEntity(user: User)`: Converts a User entity into a DTO.

#### Product.kt

* Represents a product entity.
* Fields include `id`, `product_name`, `image`, `price`, `stock`.
* Standard `equals()` and `hashCode()` based on `id`.

#### ProductDTO.kt

* DTO for Product entity.
* `fromEntity(product: Product)`: Converts Product entity to DTO.

#### AuthenticationDTO, LoginResponseDTO, RegisterDTO

* DTOs for authentication and registration:

  * `AuthenticationDTO`: login and password.
  * `LoginResponseDTO`: JWT token response.
  * `RegisterDTO`: registration details with role, login, and password.

---

### 2. Repositories

#### UserRepository.kt

* Extends `JpaRepository<User, String>`.
* Custom method `findByLogin(login: String?)` to find users by their login.

#### ProductRepository.kt

* Extends `JpaRepository<Product, String>` for basic CRUD operations.

---

### 3. Services

#### TokenService.kt

* Responsible for generating and validating JWT tokens.
* Methods:

  * `generateToken(user: User)`: Creates a JWT for a given user.
  * `validateToken(token: String?)`: Validates the token and returns the login.
  * Private helper `genExpirationDate()`: Sets token expiration to 24 hours.

#### ProductService.kt

* Handles business logic for products.
* Methods:

  * `createProduct(productDTO, authenticatedUser)`: Creates a new product.
  * `findAllProducts()`: Returns all products.
  * `deleteProduct(id)`: Deletes a product by ID.
  * `updateStock(productId, quantity)`: Updates stock for a product.

#### AuthorizationService.kt

* Implements `UserDetailsService`.
* Loads a user by username for Spring Security authentication.

---

### 4. Controllers

#### AuthController.kt

* Handles authentication and user registration endpoints.
* Endpoints:

  * POST `/auth/register`: Registers a new user.
  * POST `/auth/login`: Authenticates a user and returns a JWT token.

#### ProductController.kt

* Handles product management endpoints.
* Requires JWT authentication for product operations.
* Endpoints:

  * POST `/products`: Creates a new product.
  * GET `/products/all`: Returns all products.
  * DELETE `/products/{id}`: Deletes a product by ID.

---

### 5. Security

#### SecurityFilter.kt

* Custom filter for JWT authentication.
* Extracts the token from the `Authorization` header and sets authentication in `SecurityContext`.

#### SecurityConfigurations.kt (if exists)

* Configures HTTP security and disables CSRF for API endpoints.
* Stateless session management for REST.
* Maps roles to endpoints using `.hasRole()`.

---

### 6. Testing

#### ProductApiSpringTest.kt

* Uses Spring Boot `TestRestTemplate`.
* Tests include:

  * Registering a user.
  * Logging in a user and retrieving JWT token.
  * Creating a product with JWT authentication.

#### AppApiRestAssured.kt

* Uses `Rest-Assured` library for fluent HTTP testing.
* Advantages:

  * Readable BDD-style syntax (`Given`, `When`, `Then`).
  * Allows easy extraction of JWT tokens and response validation.
  * Better for testing APIs as external clients.
* Tests include:

  * Registering a user.
  * Logging in and extracting JWT.
  * Creating a product using JWT.

---

### 7. Configuration

#### application.properties

* Server port: `8082`
* Database connection to PostgreSQL:

  * `spring.datasource.url` = `jdbc:postgresql://localhost:5432/data`
  * `spring.datasource.username` = `postgres`
  * `spring.datasource.password` = `root`

---

### 8. Dependencies

* Spring Boot Web, Data JPA, Security.
* PostgreSQL driver.
* JWT (auth0).
* Test dependencies:

  * JUnit 5
  * Rest-Assured (with Kotlin extensions)
  * Mockito

---

### 9. Usage

1. Run PostgreSQL database and configure credentials.
2. Start Spring Boot application.
3. Use API clients or tests to interact with endpoints:

   * `/auth/register`
   * `/auth/login`
   * `/products` (requires JWT)

---

### 10. Notes

* Security is handled with JWT tokens.
* Admin and User roles differentiate access.
* Tests verify API functionality both from inside Spring (`TestRestTemplate`) and as an external client (`Rest-Assured`).
* Recommended to use `Rest-Assured` for more readable and maintainable integration tests for APIs.
