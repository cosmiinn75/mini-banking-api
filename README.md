# Mini Banking API

A Java Spring Boot REST API for a mini banking system.

The application supports JWT authentication, bank account management, deposits, withdrawals, transfers between users, transaction history, admin role management, global exception handling, and service layer unit tests.

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
- Postman

## Features

- User registration and login with JWT authentication
- Automatic `Main Account` creation after registration
- Create and view bank accounts
- Deposit money into an account
- Withdraw money from an account
- Transfer money between users
- View transaction history for each account
- Role-based authorization with `CUSTOMER` and `ADMIN`
- Admin can view all users
- Admin can view all bank accounts
- Admin can change user roles
- Admin cannot change their own role
- Custom exceptions and global exception handling
- Service layer unit tests using JUnit and Mockito

## Roles

### CUSTOMER

A customer can:

- Create bank accounts
- View their own accounts
- Deposit money
- Withdraw money
- Transfer money to another user
- View their own transaction history

### ADMIN

An admin can:

- View all users
- View all bank accounts
- Change another user's role

Admins cannot change their own role.

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive a JWT token |

### Accounts

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/accounts` | Get all accounts of the authenticated user |
| GET | `/api/accounts/{accountNumber}` | Get one account by account number |
| POST | `/api/accounts` | Create a new account |

### Transactions

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/accounts/{accountNumber}/deposit` | Deposit money into an account |
| POST | `/api/accounts/{accountNumber}/withdraw` | Withdraw money from an account |
| POST | `/api/accounts/{accountNumber}/transfer` | Transfer money to another user |
| GET | `/api/accounts/{accountNumber}/transactions` | Get transaction history for an account |

### Admin

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/users` | Get all users |
| GET | `/api/admin/accounts` | Get all bank accounts |
| PUT | `/api/admin/users/{id}/role` | Change a user's role |

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

## Authentication

Protected endpoints require a JWT token in the `Authorization` header.

```text
Authorization: Bearer your_token_here
```

Example flow:

1. Register a user
2. Login with username and password
3. Copy the returned JWT token
4. Use the token in the `Authorization` header for protected endpoints

## Example Flow

### 1. Register user

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

### 2. Login user

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

### 3. Get user accounts

```http
GET /api/accounts
Authorization: Bearer jwt_token_here
```

### 4. Deposit money

```http
POST /api/accounts/1/deposit
Authorization: Bearer jwt_token_here
```

```json
{
  "amount": 500
}
```

### 5. Withdraw money

```http
POST /api/accounts/1/withdraw
Authorization: Bearer jwt_token_here
```

```json
{
  "amount": 100
}
```

### 6. Transfer money

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

### 7. Get transaction history

```http
GET /api/accounts/1/transactions
Authorization: Bearer jwt_token_here
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
- Transfer to the same account
- Access denied
- Admin trying to change their own role
- Validation errors

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

The application was tested with unit tests and manual Postman requests.

Main tested flows:

- Register user
- Login user
- Automatically create main account
- Create additional account
- Deposit money
- Withdraw money
- Prevent withdrawal with insufficient funds
- Transfer money between users
- Prevent transfer to the same account
- View transaction history
- Prevent customer access to admin endpoints
- Allow admin to view all users
- Allow admin to view all accounts
- Allow admin to change another user's role
- Prevent admin from changing their own role

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
- DTO usage
- Spring Security
- JWT authentication
- Role-based authorization
- JPA relationships
- BigDecimal money handling
- Custom exception handling
- Unit testing with JUnit and Mockito
- Manual API testing with Postman

## Status

Core application completed.

Implemented:

- Backend logic
- Authentication
- Authorization
- Admin features
- Global exception handling
- Service layer unit tests
- Manual Postman testing

Possible future improvements:

- Integration tests
- Swagger/OpenAPI documentation
- Docker support
- Pagination for admin endpoints
- Refresh tokens
- Better error response format
