# Money Manager – Backend API Documentation

For frontend integration. Base URL, auth flow, and every endpoint are documented below.

## Base URL

```
http://localhost:8080/api/v1.0
```

All endpoints below are relative to this base (context path `/api/v1.0` is set in `application.properties`). Port defaults to `8080`.

## Authentication

The API uses stateless JWT auth.

1. Call `POST /register` to create an account.
2. Call `POST /login` to receive a token.
3. Send the token on every subsequent request as a header:

```
Authorization: Bearer <token>
```

Endpoints `/health`, `/register`, and `/login` do **not** require the header. Everything else does.

Token expires after 24 hours (`jwt.expiration`, default 86400000 ms).

---

## Auth Endpoints

### `POST /register`
Create a new user profile.

**Body:**
```json
{
  "fullName": "string",
  "email": "string",
  "password": "string",
  "profileImageUrl": "string"
}
```

**Response — 201 Created:**
```json
{
  "id": 1,
  "fullName": "string",
  "email": "string",
  "password": null,
  "profileImageUrl": "string",
  "createdAt": "2026-07-08T10:00:00",
  "updatedAt": "2026-07-08T10:00:00"
}
```
> `password` is always returned as `null`.

**Errors:** If the email is already registered, the backend currently returns a raw `500 Internal Server Error` (no structured error body yet — flagged as a known gap, see "Known Issues" below).

---

### `POST /login`
**Body:**
```json
{
  "email": "string",
  "password": "string"
}
```

**Response — 200 OK:**
```json
{
  "token": "eyJhbGciOi...",
  "user": {
    "id": 1,
    "fullName": "string",
    "email": "string",
    "password": null,
    "profileImageUrl": "string",
    "createdAt": "...",
    "updatedAt": "..."
  }
}
```

**On invalid credentials:** `400 Bad Request` with an **empty body** (not JSON, no error message). The frontend should treat any non-200 response from this endpoint as "invalid email or password" and not attempt to parse a body.

---

### `GET /test`
Simple authenticated ping. Returns the plain string `"Test Successful"`. Requires the `Authorization` header. Useful for verifying a token is valid.

---

## Category Endpoints

Base path: `/categories`

### `POST /categories`
Create a category.

**Body:**
```json
{
  "name": "string",
  "icon": "string",
  "type": "income" | "expense"
}
```
**Response — 201 Created:** `CategoryDTO` (see shape below).
**Errors:** `409 Conflict` if a category with that name already exists for the user.

### `GET /categories`
List all categories for the current user. **Response — 200:** `CategoryDTO[]`

### `GET /categories/{type}`
List categories filtered by type. `{type}` = `income` or `expense` (free-text string, not a validated enum). **Response — 200:** `CategoryDTO[]`

### `PUT /categories/{categoryId}`
Update a category. **Body:** `CategoryDTO`. **Response — 200:** updated `CategoryDTO`.
**Errors:** `404 Not Found` if the category doesn't exist or doesn't belong to the current user.

**`CategoryDTO` shape:**
```json
{
  "id": 1,
  "name": "string",
  "icon": "string",
  "profileId": 1,
  "type": "income",
  "createdAt": "2026-07-08T10:00:00",
  "updatedAt": "2026-07-08T10:00:00"
}
```

---

## Expense Endpoints

Base path: `/expenses`

| Method | Path | Description |
|---|---|---|
| POST | `/expenses` | Create an expense |
| GET | `/expenses` | List **current month's** expenses for the user |
| DELETE | `/expenses/{expenseId}` | Delete an expense → `204 No Content` |
| GET | `/expenses/latest5` | Last 5 expenses |
| GET | `/expenses/total` | Total expense amount (⚠️ see Known Issues — currently broken) |

**Request body (POST):**
```json
{
  "name": "string",
  "icon": "string",
  "categoryId": 1,
  "amount": 1000,
  "date": "2026-07-08"
}
```

**`ExpenseDTO` (response shape):**
```json
{
  "id": 1,
  "name": "string",
  "icon": "string",
  "date": "2026-07-08",
  "amount": 1000,
  "categoryName": "string",
  "categoryId": 1,
  "createdAt": "2026-07-08T10:00:00",
  "updatedAt": "2026-07-08T10:00:00"
}
```
> `amount` is an integer (no decimals/paise/cents currently supported).

**Errors:** Invalid `categoryId` on create → `500 Internal Server Error` ("Category not found").

---

## Income Endpoints

Base path: `/incomes` — identical shape and behavior to Expenses.

| Method | Path | Description |
|---|---|---|
| POST | `/incomes` | Create an income |
| GET | `/incomes` | List current month's incomes |
| DELETE | `/incomes/{incomeId}` | Delete → `204 No Content` |
| GET | `/incomes/latest5` | Last 5 incomes |
| GET | `/incomes/total` | Total income amount |

**`IncomeDTO`:** same fields as `ExpenseDTO` (`id`, `name`, `icon`, `date`, `categoryName`, `categoryId`, `amount`, `createdAt`, `updatedAt`).

---

## Dashboard

### `GET /dashboard`
Aggregated summary for the home screen.

**Response — 200:**
```json
{
  "totalBalance": 5000,
  "totalIncome": 8000,
  "totalExpense": 3000,
  "recent5Incomes": [ /* IncomeDTO[] */ ],
  "recent5Expenses": [ /* ExpenseDTO[] */ ],
  "recentTransactions": [
    {
      "id": 1,
      "profileId": 1,
      "icon": "string",
      "name": "string",
      "amount": 1000,
      "date": "2026-07-08",
      "type": "income",
      "createdAt": "...",
      "updatedAt": "..."
    }
  ]
}
```
`recentTransactions` merges incomes and expenses, sorted newest first; `type` is `"income"` or `"expense"`.

---

## Filter (⚠️ not yet usable — see Known Issues)

### `GET /filter`
Intended to filter transactions by type/date range/keyword/sort. **Body:**
```json
{
  "type": "income" | "expense",
  "startDate": "2026-07-01",
  "endDate": "2026-07-08",
  "keyword": "string",
  "sortField": "string"
}
```
Would return `IncomeDTO[]` or `ExpenseDTO[]` depending on `type`, or a `400` with a plain string body if `type` is invalid.

**Do not integrate this endpoint yet** — it currently throws a `NullPointerException` (500) on every call due to a backend dependency-injection bug, and it also expects a body on a `GET` request which many HTTP clients don't support cleanly. Backend needs to fix this before frontend wires it up.

---

## Health Check

### `GET /health`
No auth required. Returns plain string `"Everything is OK"`. Useful for uptime checks.

---

## Known Issues / Gaps (for planning error handling on the frontend)

There is currently **no global error handler**, so error responses are inconsistent:

- Most "not found" / invalid-input errors from Expense, Income, and registration flows throw raw exceptions → surface as **`500`** with Spring's default error JSON (`{"timestamp","status","error","path"}`), not a friendly message.
- Category create/update correctly return **`409`** (duplicate name) and **`404`** (not found) respectively.
- **Login failure** returns **`400` with an empty body** — no error message to display; show a generic "invalid email or password" on any non-200 response.
- **`GET /expenses/total`** appears to have a backend query bug and may not return a usable value yet — confirm with backend before relying on it (use `/dashboard`'s `totalExpense` field instead in the meantime, which works correctly).
- **`GET /filter`** is broken (NPE) — do not integrate until fixed.
- No CORS restrictions currently (`*` origin allowed), so no proxy config needed for local dev.

Recommend confirming with the backend dev that `/expenses/total`, `/filter`, and the raw-500 error cases are on the fix list before the frontend depends on them.
