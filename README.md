# SpendSense

A distributed personal finance backend that solves the 
universal problem of passive overspending.

> "You check your account at month end and have no idea 
> where the money went. SpendSense fixes that."

---

## The Problem

Every person faces this:
```
Expected: ₹8,000 remaining
Actual:   ₹1,200 remaining
Reason:   No idea
```

Existing apps require manual entry. People abandon them 
in 3 days. SpendSense works passively — just send your 
transactions and it tracks everything automatically.

---

## Architecture

```
POST /transactions
       ↓
[Transaction API] → PostgreSQL (persist)
       ↓
[Groq AI] → auto-categorize description
       ↓
[Budget Engine] → Redis atomic counter update
       ↓
[Alert Service] → WebSocket push to user
       ↓
Browser receives alert instantly (no refresh)
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17 + Spring Boot 3.2 |
| Database | PostgreSQL 15 |
| Cache | Redis 7 |
| AI | Groq API (Llama3) |
| Real-time | WebSockets (STOMP) |
| Security | JWT + Spring Security + BCrypt |
| Container | Docker + Docker Compose |

---

## Key Features

- **AI Categorization** — Groq AI reads raw UPI descriptions
  and categorizes them automatically
  (`"SWIGGY ORDER 984723"` → `"Food Delivery"`)

- **Idempotency** — Same UPI reference never stored twice,
  preventing duplicate transactions

- **Real-time Alerts** — WebSocket pushes budget warnings
  instantly when thresholds cross 70%, 90%, 100%

- **Distributed Budget Tracking** — Redis atomic counters
  track spending per category, safe under concurrent load

- **JWT Security** — Every endpoint protected,
  users only access their own data

---

## API Reference

### Auth
```
POST /auth/register
{
  "userId": "rahul_01",
  "email": "rahul@gmail.com",
  "password": "secret123"
}

POST /auth/login
{
  "userId": "rahul_01",
  "password": "secret123"
}
```

### Transactions
```
POST /transactions
Authorization: Bearer <token>
{
  "amount": 340,
  "description": "SWIGGY ORDER 984723",
  "upiRef": "TXN84729"
}

GET /transactions/user
Authorization: Bearer <token>
```

### Budgets
```
POST /budgets
Authorization: Bearer <token>
{
  "userId": "rahul_01",
  "category": "Food Delivery",
  "limitAmount": 3000,
  "month": "2026-06"
}
```

### Summary
```
GET /summary/2026-06
Authorization: Bearer <token>

Response:
{
  "userId": "rahul_01",
  "month": "2026-06",
  "totalSpent": 3080,
  "categories": [
    {
      "category": "Food Delivery",
      "spent": 2280,
      "limit": 3000,
      "percentage": 76.00,
      "status": "WARNING"
    }
  ]
}
```

### WebSocket Alerts
```
Connect: ws://localhost:8080/ws
Subscribe: /topic/alerts/{userId}

Alert payload:
{
  "type": "BUDGET_EXCEEDED",
  "category": "Food Delivery",
  "spent": 3200,
  "limit": 3000,
  "percentage": 106.67,
  "message": "You have exceeded your Food Delivery budget!"
}
```

---

## How to Run

### Prerequisites
- Java 17+
- Docker + Docker Compose
- Groq API key (free at console.groq.com)

### Setup

**1. Clone the repo:**
```bash
git clone https://github.com/YOUR_USERNAME/spendsense.git
cd spendsense
```

**2. Configure environment:**
```bash
cp application.properties.example application.properties
# Edit application.properties with your values
```

**3. Start Docker services:**
```bash
docker-compose up -d
```

**4. Run the application:**
```bash
./mvnw spring-boot:run
```

**5. Test WebSocket alerts:**
```
Open: http://localhost:8080/websocket-test.html
```

---

## Concepts Demonstrated

- REST API design with idempotency
- JPA entity mapping and custom repository queries
- External AI API integration with fallback logic
- Redis as atomic counter store for distributed state
- WebSockets with STOMP for real-time push notifications
- JWT authentication with Spring Security filter chain
- BCrypt password hashing
- Docker Compose for local development
- Event-driven architecture (transaction save → budget update → alert)

---

## Author

Rucha Sinkar
