package Money.Manager.service;

import Money.Manager.dto.ExpenseDTO;
import Money.Manager.entity.ProfileEntity;
import Money.Manager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final ExpenseService expenseService;
    private final EmailService emailService;

    // Runs every day at 11 PM and reminds users who haven't logged an expense today
    @Scheduled(cron = "0 0 23 * * *")
    public void remindUsersToAddExpenses() {
        List<ProfileEntity> profiles = profileRepository.findByIsActiveTrue();

        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> todaysExpenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());

            if (todaysExpenses.isEmpty()) {
                try {
                    emailService.sendEmail(
                            profile.getEmail(),
                            "Don't forget to log today's expenses!",
                            "Hi " + profile.getFullName() + ",\n\n" +
                                    "It looks like you haven't added any expenses today. " +
                                    "Take a moment to log them so your records stay up to date.\n\n" +
                                    "- Money Manager"
                    );
                } catch (Exception e) {
                    System.err.println("Failed to send reminder email to " + profile.getEmail() + ": " + e.getMessage());
                }
            }
        }
    }
}
