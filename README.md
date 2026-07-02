# Mini Banking API

A Java Spring Boot REST API for a mini banking system.

The application supports JWT authentication, role-based authorization, bank account management, deposits, withdrawals, transfers between users, paginated and filtered transaction history, admin role management, user statistics, Swagger/OpenAPI documentation, global exception handling, and service layer unit tests.

## Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- MySQL
- JUnit
- Mockito
- Maven
- Swagger / OpenAPI
- Postman

## Features

- User registration and login with JWT authentication
- BCrypt password hashing
- Automatic `Main Account` creation after registration
- Create and view bank accounts
- Update bank account names
- Delete/deactivate bank accounts if allowed by business rules
- Filter accounts by minimum balance
- Paginated account responses
- Deposit money into an account
- Withdraw money from an account
- Transfer money between users
- View transaction history for each account
- Filter transactions by type and minimum amount
- Paginated transaction history
- Role-based authorization with `CUSTOMER` and `ADMIN`
- Permission-based endpoint protection
- Admin can view all users
- Admin can view all bank accounts
- Admin can view user statistics
- Admin can change user roles
- Admin cannot change their own role
- Custom exceptions and global exception handling
- Swagger/OpenAPI API documentation
- Service layer unit tests using JUnit and Mockito
- Manual API testing with Postman and Swagger UI

## Roles

### CUSTOMER

A customer can:

- Create bank accounts
- View their own accounts
- Update their own account names
- Delete/deactivate their own accounts if allowed by business rules
- Deposit money
- Withdraw money
- Transfer money to another user
- View their own transaction history
- Filter and paginate their own accounts and transactions

### ADMIN

An admin can:

- View all users
- View all bank accounts
- View statistics for each user
- Change another user's role

Admins cannot change their own role.

## API Documentation

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON documentation is available at:

```text
http://localhost:8080/v3/api-docs
```

Protected endpoints require JWT authentication. In Swagger UI, use the **Authorize** button and paste the JWT token returned by the login endpoint.

## Authentication

Protected endpoints require a JWT token in the `Authorization` header.

```text
Authorization: Bearer your_token_here
```

Example authentication flow:

1. Register a user
2. Login with username and password
3. Copy the returned JWT token
4. Use the token in the `Authorization` header for protected endpoints

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive a JWT token |

### Accounts

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/accounts` | Get paginated accounts of the authenticated user, with optional minimum balance filter |
| GET | `/api/accounts/{accountNumber}` | Get one account by account number |
| POST | `/api/accounts` | Create a new account |
| PUT | `/api/accounts/{accountNumber}` | Update account name |
| DELETE | `/api/accounts/{accountNumber}` | Delete/deactivate an account if allowed by business rules |

### Transactions

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/accounts/{accountNumber}/deposit` | Deposit money into an account |
| POST | `/api/accounts/{accountNumber}/withdraw` | Withdraw money from an account |
| POST | `/api/accounts/{accountNumber}/transfer` | Transfer money to another user |
| GET | `/api/accounts/{accountNumber}/transactions` | Get paginated and filtered transaction history for an account |

### Admin

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/users` | Get all users |
| GET | `/api/admin/accounts` | Get all bank accounts |
| GET | `/api/admin/users/stats` | Get statistics for each user |
| PUT | `/api/admin/users/{id}/role` | Change a user's role |

## Query Parameters

### Get Accounts

```http
GET /api/accounts?minBalance=100&page=0&size=5
```

Optional parameters:

| Parameter | Type | Description |
|---|---|---|
| `minBalance` | BigDecimal | Returns only accounts with balance greater than or equal to this value |
| `page` | Integer | Page number, starting from 0 |
| `size` | Integer | Number of accounts per page |

### Get Transactions

```http
GET /api/accounts/{accountNumber}/transactions?type=DEPOSIT&minAmount=100&page=0&size=5
```

Optional parameters:

| Parameter | Type | Description |
|---|---|---|
| `type` | TransactionType | Filter transactions by type |
| `minAmount` | BigDecimal | Returns only transactions with amount greater than or equal to this value |
| `page` | Integer | Page number, starting from 0 |
| `size` | Integer | Number of transactions per page |

Supported transaction types:

```text
DEPOSIT
WITHDRAWAL
TRANSFER_IN
TRANSFER_OUT
```

## Request Body Examples

### Register

```json
{
  "username": "cosmin",
  "email": "cosmin@gmail.com",
  "password": "parola"
}
```

### Login

```json
{
  "username": "cosmin",
  "password": "parola"
}
```

### Create Account

```json
{
  "name": "Savings"
}
```

### Update Account Name

```json
{
  "newName": "Emergency Fund"
}
```

### Deposit

```json
{
  "amount": 500
}
```

### Withdraw

```json
{
  "amount": 100
}
```

### Transfer

```json
{
  "toUsername": "ionut",
  "toAccountNumber": 1,
  "amount": 150
}
```

### Change User Role

```json
{
  "role": "ADMIN"
}
```

## Response Examples

### Authentication Response

```json
{
  "token": "jwt_token_here"
}
```

### Paginated Transaction Response

```json
{
  "content": [
    {
      "accountNumber": 1,
      "accountName": "Main Account",
      "transactionType": "TRANSFER_OUT",
      "amount": 150.00,
      "createdAt": "2026-06-26T21:17:08.250367"
    },
    {
      "accountNumber": 1,
      "accountName": "Main Account",
      "transactionType": "WITHDRAWAL",
      "amount": 100.00,
      "createdAt": "2026-06-26T21:16:39.518088"
    },
    {
      "accountNumber": 1,
      "accountName": "Main Account",
      "transactionType": "DEPOSIT",
      "amount": 500.00,
      "createdAt": "2026-06-26T21:16:21.749898"
    }
  ],
  "page": 0,
  "size": 5,
  "totalElements": 3,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

## Example Flow

### 1. Register User

```http
POST /api/auth/register
```

```json
{
  "username": "cosmin",
  "email": "cosmin@gmail.com",
  "password": "parola"
}
```

After registration, a `Main Account` is automatically created for the user.

### 2. Login User

```http
POST /api/auth/login
```

```json
{
  "username": "cosmin",
  "password": "parola"
}
```

Response:

```json
{
  "token": "jwt_token_here"
}
```

### 3. Get User Accounts

```http
GET /api/accounts?page=0&size=20
Authorization: Bearer jwt_token_here
```

### 4. Filter User Accounts

```http
GET /api/accounts?minBalance=100&page=0&size=5
Authorization: Bearer jwt_token_here
```

### 5. Create Account

```http
POST /api/accounts
Authorization: Bearer jwt_token_here
```

```json
{
  "name": "Savings"
}
```

### 6. Update Account Name

```http
PUT /api/accounts/2
Authorization: Bearer jwt_token_here
```

```json
{
  "newName": "Emergency Fund"
}
```

### 7. Deposit Money

```http
POST /api/accounts/1/deposit
Authorization: Bearer jwt_token_here
```

```json
{
  "amount": 500
}
```

### 8. Withdraw Money

```http
POST /api/accounts/1/withdraw
Authorization: Bearer jwt_token_here
```

```json
{
  "amount": 100
}
```

### 9. Transfer Money

```http
POST /api/accounts/1/transfer
Authorization: Bearer jwt_token_here
```

```json
{
  "toUsername": "ionut",
  "toAccountNumber": 1,
  "amount": 150
}
```

### 10. Get Transaction History

```http
GET /api/accounts/1/transactions?page=0&size=5
Authorization: Bearer jwt_token_here
```

### 11. Filter Transaction History

```http
GET /api/accounts/1/transactions?type=DEPOSIT&minAmount=100&page=0&size=5
Authorization: Bearer jwt_token_here
```

### 12. Delete/Deactivate Account

```http
DELETE /api/accounts/2
Authorization: Bearer jwt_token_here
```

## Admin Flow

### 1. Login as Admin

```http
POST /api/auth/login
```

```json
{
  "username": "admin",
  "password": "parola"
}
```

### 2. Get All Users

```http
GET /api/admin/users
Authorization: Bearer admin_jwt_token_here
```

### 3. Get All Bank Accounts

```http
GET /api/admin/accounts
Authorization: Bearer admin_jwt_token_here
```

### 4. Get User Statistics

```http
GET /api/admin/users/stats
Authorization: Bearer admin_jwt_token_here
```

### 5. Change User Role

```http
PUT /api/admin/users/2/role
Authorization: Bearer admin_jwt_token_here
```

```json
{
  "role": "ADMIN"
}
```

## Error Handling

The application uses custom exceptions and a global exception handler.

Example error response:

```json
{
  "error": "Bad request",
  "message": "Insufficient funds"
}
```

Common handled cases:

- Invalid credentials
- Username already exists
- Email already exists
- Account not found
- User not found
- Insufficient funds
- Invalid deposit amount
- Invalid withdrawal amount
- Invalid transfer amount
- Transfer to the same account
- Access denied
- Admin trying to change their own role
- Validation errors
- Invalid pagination parameters
- Invalid transaction type filter
- Invalid minimum balance or minimum amount
- Account deletion/deactivation not allowed by business rules

## Database Setup

Create a MySQL database:

```sql
CREATE DATABASE mini_banking_db;
```

Example `application.properties` configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mini_banking_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=yourVeryLongSecretKeyForJwtTokenGeneration123456
```

The JWT secret should be long enough for the signing algorithm.

## How to Run

Clone the repository:

```bash
git clone https://github.com/cosmiinn75/mini-banking-api.git
```

Go into the project folder:

```bash
cd mini-banking-api
```

Run the application:

```bash
mvn spring-boot:run
```

The API will run on:

```text
http://localhost:8080
```

Swagger UI will be available at:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI docs will be available at:

```text
http://localhost:8080/v3/api-docs
```

## Running Tests

Run all tests with Maven:

```bash
mvn test
```

The project includes service layer unit tests for:

- Authentication
- Account creation
- Deposits
- Withdrawals
- Transfers
- Transaction history
- Admin role management
- Exception cases

## Tested Scenarios

The application was tested with unit tests, manual Postman requests, and Swagger UI.

Main tested flows:

- Register user
- Login user
- Automatically create main account
- Create additional account
- Update account name
- Filter accounts by minimum balance
- Paginate accounts
- Deposit money
- Withdraw money
- Prevent withdrawal with insufficient funds
- Transfer money between users
- Prevent transfer to the same account
- View transaction history
- Filter transactions by transaction type
- Filter transactions by minimum amount
- Paginate transactions
- Delete/deactivate account if allowed by business rules
- Prevent customer access to admin endpoints
- Allow admin to view all users
- Allow admin to view all accounts
- Allow admin to view user statistics
- Allow admin to change another user's role
- Prevent admin from changing their own role
- Generate and test Swagger/OpenAPI documentation

## Project Structure

```text
src/main/java/com/cosmin/mini_banking_api
│
├── Controller
├── Dto
├── Enum
├── Exception
├── Model
├── Repository
├── Security
└── Service
```

Tests are located in:

```text
src/test/java/com/cosmin/mini_banking_api
```

## Main Concepts Practiced

- REST API design
- Layered architecture
- Controller-Service-Repository pattern
- DTO usage
- Spring Security
- JWT authentication
- Role-based authorization
- Permission-based endpoint protection
- JPA relationships
- JPQL queries
- Filtering with optional query parameters
- Pagination with Spring Data
- Custom paginated response DTO
- BigDecimal money handling
- Custom exception handling
- Global exception handling
- Swagger/OpenAPI documentation
- Unit testing with JUnit and Mockito
- Manual API testing with Postman and Swagger UI

## Status

Core application completed.

Implemented:

- Backend logic
- Authentication
- Authorization
- Bank account management
- Deposits
- Withdrawals
- Transfers between users
- Transaction history
- Admin features
- User statistics
- Filtering
- Pagination
- Swagger/OpenAPI documentation
- Global exception handling
- Service layer unit tests
- Manual Postman testing
- Swagger UI testing

Possible future improvements:

- Integration tests
- Docker support
- Docker Compose with MySQL
- Pagination for admin endpoints
- Refresh tokens
- Soft delete / account closing instead of physical deletion
- Better error response format
- Frontend client
- CI/CD with GitHub Actions
