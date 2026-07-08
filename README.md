# Money Manager

A Spring Boot REST API for personal finance tracking — log incomes and expenses by category, view a dashboard summary, filter transactions, and get a daily email reminder to log expenses.

## Tech Stack

- **Java 21**, **Spring Boot 4.1.0**
- Spring Web MVC, Spring Data JPA (MySQL), Spring Security (JWT, stateless)
- Spring Mail (`spring-boot-starter-mail`) for email notifications
- `jjwt` for JWT generation/validation
- Apache POI (`poi-ooxml`) — available for spreadsheet export/import
- Lombok
- Maven (via `mvnw` / `mvnw.cmd`)

## Project Structure

```
src/main/java/Money/Manager/
├── DemoApplication.java        # @SpringBootApplication, @EnableScheduling
├── config/
│   └── SecurityConfig.java     # JWT stateless security, CORS, password encoder
├── security/
│   └── JwtRequestFilter.java   # extracts & validates JWT on each request
├── util/
│   └── JwtUtil.java            # token generation/validation
├── entity/
│   ├── ProfileEntity.java      # user account (tbl_profile)
│   ├── CategoryEntity.java     # income/expense category (tbl_categories)
│   ├── ExpenseEntity.java      # tbl_expenses
│   └── IncomeEntity.java       # tbl_incomes
├── repository/                 # Spring Data JPA repositories for each entity
├── dto/                        # request/response DTOs (Profile, Auth, Category, Expense, Income, Filter, RecentTransaction)
├── service/
│   ├── ProfileService.java         # register/login/JWT issuance, current-user lookup
│   ├── AppUserDetailsService.java  # Spring Security UserDetailsService
│   ├── CategoryService.java        # CRUD for categories
│   ├── ExpenseService.java         # add/filter/delete expenses, totals
│   ├── IncomeService.java          # add/filter/delete incomes, totals
│   ├── DashBoardService.java       # aggregates balance + recent transactions
│   ├── EmailService.java           # sends email via JavaMailSender
│   └── NotificationService.java    # scheduled 11 PM expense reminder (see NOTIFICATIONS.md)
└── controller/
    ├── ProfileController.java          # /register, /login, /test
    ├── CategoryController.java         # /categories
    ├── ExpenseController.java          # /expenses
    ├── IncomeController.java           # /incomes
    ├── DashboardController.java        # /dashboard
    ├── FilterTransactionController.java# /filter
    └── HealthCheck.java                # /health
```

All API routes are served under the context path **`/api/v1.0`**.

## Authentication

- Registration (`POST /register`) and login (`POST /login`) are public; every other endpoint requires a valid JWT.
- Login returns a JWT (`jwt.secret` / `jwt.expiration` in config) plus the caller's public profile.
- Requests must send `Authorization: Bearer <token>`. `JwtRequestFilter` validates the token and populates the Spring Security context; `ProfileService.getCurrentProfile()` resolves the authenticated user by email for use in services.
- Sessions are stateless (`SessionCreationPolicy.STATELESS`); CORS is open to all origins for `GET/POST/PUT/DELETE/OPTIONS`.

## API Endpoints

| Method | Path | Description | Auth |
|---|---|---|---|
| POST | `/register` | Create a new profile | Public |
| POST | `/login` | Authenticate, get JWT | Public |
| GET | `/health` | Health check | Public |
| GET | `/categories` | List current user's categories | JWT |
| GET | `/categories/{type}` | List categories by type (`income`/`expense`) | JWT |
| POST | `/categories` | Create a category | JWT |
| PUT | `/categories/{categoryId}` | Update a category | JWT |
| POST | `/expenses` | Add an expense | JWT |
| GET | `/expenses` | Current month's expenses | JWT |
| GET | `/expenses/latest5` | Last 5 expenses | JWT |
| GET | `/expenses/total` | Total expense amount | JWT |
| DELETE | `/expenses/{expenseId}` | Delete an expense | JWT |
| POST | `/incomes` | Add an income | JWT |
| GET | `/incomes` | Current month's incomes | JWT |
| GET | `/incomes/latest5` | Last 5 incomes | JWT |
| GET | `/incomes/total` | Total income amount | JWT |
| DELETE | `/incomes/{incomeId}` | Delete an income | JWT |
| GET | `/dashboard` | Balance summary + recent transactions | JWT |
| GET | `/filter` | Filter incomes/expenses by date range, keyword, sort | JWT |

## Data Model

- **ProfileEntity** — `id, fullName, email (unique), password, profileImageUrl, isActive, createdAt, updatedAt`
- **CategoryEntity** — `id, name, icon, type, profile (owner), createdAt, updatedAt`
- **ExpenseEntity** / **IncomeEntity** — `id, name, icon, date, amount, category, profile, createdAt, updatedAt` (date defaults to today on persist)

All expenses/incomes/categories belong to a `ProfileEntity` (one-to-many via `profile_id`).

## Notifications

A scheduled job (`NotificationService`) runs daily at 11 PM and emails any active user who hasn't logged an expense that day. See [NOTIFICATIONS.md](NOTIFICATIONS.md) for details.

## Configuration

`src/main/resources/application.properties` reads from environment variables (optionally via a local `.env` file):

```
DB_URL, DB_USERNAME, DB_PASSWORD
MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD, MAIL_FROM
JWT_SECRET, JWT_EXPIRATION
```

Database: MySQL, `spring.jpa.hibernate.ddl-auto=update` (schema auto-updates on startup).

## Running Locally

```
./mvnw spring-boot:run       # macOS/Linux
mvnw.cmd spring-boot:run     # Windows
```

Ensure a MySQL instance is reachable at `DB_URL` and the required environment variables (above) are set, e.g. via a `.env` file in the project root.
