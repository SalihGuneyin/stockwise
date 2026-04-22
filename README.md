# Stockwise

Stockwise is an inventory and supplier operations dashboard built for backend-focused portfolio presentation. The project combines a Spring Boot API with a React dashboard and demonstrates layered backend design, relational modeling, validation, CRUD operations, stock movement tracking, and operational reporting.

## Project Description

Stockwise is an internal inventory management application designed to help teams monitor stock levels, supplier reliability, and warehouse movement activity from a single panel. The backend is built with Spring Boot using layered architecture, DTO-based request and response handling, validation, and JPA/Hibernate for data persistence. The frontend is a React dashboard that surfaces inventory metrics, low-stock alerts, supplier data, and recent inbound or outbound movements in a responsive interface.

## GitHub Short Description

Backend-first inventory control dashboard built with Spring Boot, React, JPA, and H2 in MSSQL mode.

## Stack

- Backend: Java 21, Spring Boot 3.5, Spring Data JPA, Spring Security, Validation
- Database: H2 in MSSQL compatibility mode
- Frontend: React 19, Vite
- Testing: JUnit 5, MockMvc

## Core Features

- Product catalog with SKU, warehouse zone, reorder point, unit price, and activity status
- Supplier management with lead time, reliability score, and preferred vendor flag
- Stock movement logging for `INBOUND`, `OUTBOUND`, and `ADJUSTMENT` transactions
- Dashboard metrics for product coverage, low-stock items, supplier quality, and recent movement history
- Seed data for immediate demo without manual setup
- Global error handling and request validation

## Project Structure

- `backend`: Spring Boot REST API
- `frontend`: React dashboard

## Run Locally

### Requirements

- Java 21 or newer
- Node.js 20+
- npm

### 1. Start the backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The backend starts on `http://localhost:8080`

### 2. Start the frontend in a second terminal

```powershell
cd frontend
npm install
npm run dev
```

The frontend starts on `http://localhost:5173`

### 3. Open the application

- Open `http://localhost:5173`
- The frontend talks to `http://localhost:8080` by default
- If you want to change the backend URL, set `VITE_API_BASE_URL` before running the frontend

### Easier option

From the project root, you can start both services with one command:

```powershell
.\start-local.ps1
```

To stop background processes started for this project:

```powershell
.\stop-local.ps1
```

## Useful Endpoints

- `GET /api/dashboard`
- `GET /api/products`
- `POST /api/products`
- `GET /api/suppliers`
- `POST /api/suppliers`
- `GET /api/movements`
- `POST /api/movements`

## Demo Notes

- H2 console is available at `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:stockwise`
- Username: `sa`
- Password: empty

## Why It Fits Your CV

- Shows `Spring Boot`, `REST API`, `JPA`, `Hibernate`, `DTO`, and layered architecture knowledge
- Adds a realistic operations use case beyond recruitment-style CRUD examples
- Gives you a project that is easy to explain through stock logic, supplier tracking, and business metrics
