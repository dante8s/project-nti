package com.nti.nti_backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Ім'я обов'язкове")
        String name,

        @Email(message = "Невірний формат email")
        @NotBlank(message = "Email обов'язковий")
        String email,

        @Size(min = 6, message = "Мінімум 6 символів")
        @NotBlank(message = "Пароль обов'язковий")
        String password,

        // STUDENT, FIRM, MENTOR
        @NotBlank(message = "Роль обов'язкова")
        String role,

        // Згода на обробку персональних даних (GDPR)
        boolean gdprConsent
) {}
