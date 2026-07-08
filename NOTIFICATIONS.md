# Expense Reminder Notifications

Daily scheduled job that emails active users who haven't logged an expense yet that day.

## How it works

- `NotificationService.remindUsersToAddExpenses()` runs on a cron schedule (`0 0 23 * * *` — every day at 11:00 PM, server timezone).
- It fetches all active profiles via `ProfileRepository.findByIsActiveTrue()`.
- For each profile, it checks `ExpenseService.getExpensesForUserOnDate(profileId, today)`.
- If no expenses were logged today, it sends a reminder email through `EmailService.sendEmail(...)`.
- A failure sending one user's email is caught and logged to stderr so it doesn't block reminders to the rest of the batch.

## Relevant files

| File | Role |
|---|---|
| `service/NotificationService.java` | Scheduled job / reminder logic |
| `service/EmailService.java` | Sends the actual email via `JavaMailSender` |
| `repository/ProfileRepository.java` | `findByIsActiveTrue()` — active user list |
| `service/ExpenseService.java` | `getExpensesForUserOnDate()` — checks if user already logged an expense |
| `DemoApplication.java` | `@EnableScheduling` — required for `@Scheduled` to run |

## Configuration

Mail settings are read from environment variables (see `application.properties`):

```
MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD, MAIL_FROM
```

## Changing the reminder time

Edit the cron expression on `@Scheduled(cron = "0 0 23 * * *")` in `NotificationService`. Format: `second minute hour day month weekday`.

By default the cron runs in the server's local timezone. To pin it to a specific zone, add a `zone` attribute, e.g.:

```java
@Scheduled(cron = "0 0 23 * * *", zone = "Asia/Kolkata")
```
