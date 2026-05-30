# Expense Tracker API

A backend REST API for real-time personal and business expense tracking. Built with Java 17, Spring Boot, and PostgreSQL. Designed with a clean architecture that makes future AI-powered categorization easy to integrate.

---

## Features

- **Transaction Logging** — Record income and expense transactions with category, amount, date, and description
- **Statistical Analysis** — Aggregate spending by category, time period, or custom filters using SQL aggregations
- **Category Management** — Create custom expense categories and assign transactions accordingly
- **Monthly/Weekly Reports** — Query spending summaries over any date range
- **JWT Authentication** — Each user manages their own isolated transaction data
- **AI-Ready Architecture** — Data model and API structure designed for easy integration of ML-based auto-categorization

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3 |
| Database | PostgreSQL |
| ORM | JPA / Hibernate |
| Auth | JWT |
| Build | Gradle |

---

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL 14+
- Gradle

### Setup

```bash
git clone https://github.com/vusal466/expense-tracker-api.git
cd expense-tracker-api
```

Configure your database in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Run:

```bash
./gradlew bootRun
```

API available at `http://localhost:8080`

---

## API Endpoints

### Auth

```
POST /api/auth/register      - Register new user
POST /api/auth/login         - Login, receive JWT
```

### Transactions

```
GET    /api/transactions              - List all transactions (with filters)
POST   /api/transactions              - Add new transaction
GET    /api/transactions/{id}         - Get transaction detail
PUT    /api/transactions/{id}         - Update transaction
DELETE /api/transactions/{id}         - Delete transaction
```

Query parameters for filtering:

```
?category=food
?startDate=2026-01-01&endDate=2026-01-31
?type=EXPENSE  (or INCOME)
```

### Analytics

```
GET /api/analytics/summary            - Total income vs expenses
GET /api/analytics/by-category        - Spending breakdown by category
GET /api/analytics/monthly            - Month-over-month comparison
```

### Categories

```
GET    /api/categories                - List categories
POST   /api/categories                - Create category
DELETE /api/categories/{id}           - Delete category
```

---

## Data Model

```
User
  └── Transactions (1:N)
        └── Category (N:1)
```

Example transaction payload:

```json
{
  "amount": 45.00,
  "type": "EXPENSE",
  "category": "food",
  "description": "Lunch",
  "date": "2026-05-15"
}
```

---

## Analytics Example

`GET /api/analytics/by-category?startDate=2026-01-01&endDate=2026-01-31`

```json
[
  { "category": "food",          "total": 320.00 },
  { "category": "transport",     "total": 85.50  },
  { "category": "entertainment", "total": 60.00  }
]
```

---

## Future Improvements

- [ ] ML model integration for automatic transaction categorization
- [ ] Budget alerts — notify when spending exceeds set limits
- [ ] CSV export for transactions
- [ ] Docker support

---

## Author

**Vusal Cafarli** — Java Backend Developer
- GitHub: [@vusal466](https://github.com/vusal466)
