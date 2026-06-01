# Expense Tracker API

A backend REST API for personal and business expense tracking with **AI-powered automatic categorization**. Built with Java, Spring Boot, and a Python ML microservice.

---

## Features

- **Transaction Logging** — Record income and expense transactions with category, amount, and date
- **ML Auto-Categorization** — Automatically classifies expenses using a Python ML microservice (TF-IDF + Logistic Regression)
- **Statistical Analysis** — Aggregate spending by category, time period, or custom filters
- **Monthly/Weekly Reports** — Query spending summaries over any date range
- **Docker Support** — Full stack runs with a single `docker compose up`

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17, Python 3.12 |
| Framework | Spring Boot 4, Flask |
| ML | Scikit-learn (TF-IDF + Logistic Regression) |
| Database | MySQL 8 |
| ORM | JPA / Hibernate |
| Build | Gradle |
| DevOps | Docker, Docker Compose |

---

## Architecture

```
POST /expenses/save
      ↓
Spring Boot (Java)
      ↓
Python ML Microservice → predicts category from title
      ↓
MySQL Database
```

When a user submits an expense without a category, Spring Boot calls the Python ML service which predicts the category automatically based on the expense title.

---

## ML Microservice

Located in `ml-service/` — a standalone Flask app that exposes a `/predict` endpoint.

**How it works:**
- Input: expense title (e.g. `"taxi to airport"`)
- Model: TF-IDF vectorizer + Logistic Regression pipeline
- Output: predicted category + confidence score

**Supported categories:** `FOOD`, `TRANSPORT`, `RENT`, `SHOPPING`, `OTHER`

**Example:**

```bash
curl -X POST http://localhost:5000/predict \
  -H "Content-Type: application/json" \
  -d '{"title": "taxi to airport"}'
```

```json
{
  "title": "taxi to airport",
  "category": "TRANSPORT",
  "confidence": 0.9821
}
```

**Prediction examples:**

| Title | Predicted Category |
|---|---|
| taxi to airport | TRANSPORT |
| grocery shopping | FOOD |
| monthly rent payment | RENT |
| new shoes nike | SHOPPING |
| netflix subscription | OTHER |

---

## Getting Started

### Prerequisites

- Java 17+
- Python 3.12+
- MySQL 8+
- Docker (optional)

### Run locally

**1. Clone the repo:**
```bash
git clone https://github.com/vusal466/expense-tracker-api.git
cd expense-tracker-api
```

**2. Set up environment variables:**
```bash
cp .env.example .env
# Edit .env with your values
```

**3. Start ML service:**
```bash
cd ml-service
pip install flask scikit-learn numpy
python app.py
```

**4. Start Spring Boot:**
```bash
./gradlew bootRun
```

API available at `http://localhost:8080/swagger-ui/index.html`

---

### Run with Docker

```bash
docker compose up --build
```

All services start automatically: MySQL, Python ML service, Spring Boot.

---

## API Endpoints

```
POST   /expenses/save         - Add expense (category auto-predicted by ML)
GET    /expenses/all          - List all expenses
GET    /expenses/{id}         - Get expense by ID
PUT    /expenses/update/{id}  - Update expense
DELETE /expenses/delete/{id}  - Delete expense
GET    /expenses/date/{date}  - Get expenses by date
GET    /expenses/summary      - Income vs expense summary
GET    /expenses/total        - Total expenses between dates
```

---

## Example Request

```json
POST /expenses/save

{
  "title": "taxi to airport",
  "amount": 15.0,
  "type": "EXPENSE"
}
```

```json
{
  "id": 1,
  "title": "taxi to airport",
  "amount": 15.0,
  "type": "EXPENSE",
  "category": "TRANSPORT",
  "date": "2026-05-31"
}
```

Category is automatically predicted — no need to send it manually.

---

## Future Improvements

- [ ] Retrain ML model with user feedback
- [ ] Budget alerts — notify when spending exceeds set limits
- [ ] CSV export for transactions
- [ ] JWT Authentication

---

## Author

**Vusal Jafarli** — Java Backend Developer
- GitHub: [@vusal466](https://github.com/vusal466)
- LinkedIn: [vusaljafarli](https://linkedin.com/in/vusaljafarli)
