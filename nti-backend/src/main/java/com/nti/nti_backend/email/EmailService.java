package com.nti.nti_backend.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // Базовий метод відправки
    private void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@nti.sk");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    // Підтвердження email після реєстрації
    public void sendVerificationEmail(
            String to, String token) {
        String link = "http://localhost:8080"
                + "/api/auth/verify?token=" + token;
        send(
                to,
                "NTI — Підтвердіть вашу email адресу",
                "Вітаємо у NTI!\n\n"
                        + "Для підтвердження вашої email адреси "
                        + "перейдіть за посиланням:\n\n"
                        + link + "\n\n"
                        + "Посилання дійсне 24 години.\n\n"
                        + "Якщо ви не реєструвались — "
                        + "проігноруйте цей лист."
        );
    }

    // Скидання пароля
    public void sendResetPasswordEmail(
            String to, String token) {
        String link = "http://localhost:5173"
                + "/reset-password?token=" + token;
        send(
                to,
                "NTI — Скидання пароля",
                "Ви запросили скидання пароля.\n\n"
                        + "Перейдіть за посиланням щоб "
                        + "встановити новий пароль:\n\n"
                        + link + "\n\n"
                        + "Посилання дійсне 1 годину.\n\n"
                        + "Якщо ви не запитували скидання — "
                        + "проігноруйте цей лист."
        );
    }

    // Вітальний лист після підтвердження email
    public void sendWelcomeEmail(String to, String name) {
        send(
                to,
                "NTI — Ласкаво просимо!",
                "Вітаємо, " + name + "!\n\n"
                        + "Ваш акаунт успішно підтверджено.\n\n"
                        + "Тепер заповніть ваш профіль щоб "
                        + "подати заявку на програму.\n\n"
                        + "http://localhost:5173/onboarding\n\n"
                        + "Команда NTI"
        );
    }

    // Нотифікація про зміну статусу заявки
    // (використовує Розробник 1 пізніше)
    public void sendApplicationStatusChanged(
            String to,
            String applicantName,
            String newStatus,
            String comment) {
        send(
                to,
                "NTI — Статус вашої заявки змінено",
                "Вітаємо, " + applicantName + "!\n\n"
                        + "Статус вашої заявки змінено на: "
                        + newStatus + "\n\n"
                        + (comment != null && !comment.isBlank()
                        ? "Коментар: " + comment + "\n\n"
                        : "")
                        + "Деталі у вашому кабінеті:\n"
                        + "http://localhost:5173/student/applications\n\n"
                        + "Команда NTI"
        );
    }
}