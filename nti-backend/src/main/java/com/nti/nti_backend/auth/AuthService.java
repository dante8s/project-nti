package com.nti.nti_backend.auth;

import com.nti.nti_backend.email.EmailService;
import com.nti.nti_backend.jwt.JwtUtil;
import com.nti.nti_backend.user.Role;
import com.nti.nti_backend.user.User;
import com.nti.nti_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final EmailService emailService;

    // -----------------------------------------------
    // РЕЄСТРАЦІЯ
    // -----------------------------------------------
    public String register(RegisterRequest request) {

        // Перевірка GDPR згоди
        if (!request.gdprConsent()) {
            throw new RuntimeException(
                    "Потрібна згода на обробку даних"
            );
        }

        // Перевірка дозволених ролей при реєстрації
        List<String> allowedRoles =
                List.of("STUDENT", "FIRM", "MENTOR");
        if (!allowedRoles.contains(request.role())) {
            throw new RuntimeException(
                    "Недозволена роль при реєстрації"
            );
        }

        // Перевірка чи email вже зайнятий
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException(
                    "Email вже зареєстрований"
            );
        }

        // Генеруємо токен для підтвердження email
        String verificationToken = UUID.randomUUID().toString();

        // Створюємо юзера
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(
                        passwordEncoder.encode(request.password())
                )
                .role(Role.valueOf(request.role()))
                .emailVerified(false)
                .verificationToken(verificationToken)
                .onboardingCompleted(false)
                .build();

        userRepository.save(user);

        // Відправляємо email підтвердження
        emailService.sendVerificationEmail(
                user.getEmail(),
                verificationToken
        );

        // Повертаємо токен
        return jwtUtil.generateToken(user.getEmail());
    }

    // -----------------------------------------------
    // ЛОГІН
    // -----------------------------------------------
    public AuthResponse login(LoginRequest request) {

        // Spring Security перевіряє email і пароль
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new RuntimeException("Користувача не знайдено")
                );

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.isEmailVerified(),
                user.isOnboardingCompleted()
        );
    }

    // -----------------------------------------------
    // ПІДТВЕРДЖЕННЯ EMAIL
    // -----------------------------------------------
    public String verifyEmail(String token) {

        User user = userRepository
                .findByVerificationToken(token)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Невірний токен підтвердження"
                        )
                );

        if (user.isEmailVerified()) {
            return "Email вже підтверджений";
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        // Відправляємо вітальний лист
        emailService.sendWelcomeEmail(
                user.getEmail(),
                user.getName()
        );

        return "Email успішно підтверджено";
    }

    // -----------------------------------------------
    // ЗАПИТ НА СКИДАННЯ ПАРОЛЯ
    // -----------------------------------------------
    public void forgotPassword(
            ForgotPasswordRequest request) {

        // Якщо email не знайдено — не кажемо про це
        // (захист від перебору email)
        userRepository
                .findByEmail(request.email())
                .ifPresent(user -> {
                    String resetToken =
                            UUID.randomUUID().toString();

                    user.setResetPasswordToken(resetToken);
                    // Токен дійсний 1 годину
                    user.setResetTokenExpiry(
                            LocalDateTime.now().plusHours(1)
                    );
                    userRepository.save(user);

                    emailService.sendResetPasswordEmail(
                            user.getEmail(),
                            resetToken
                    );
                });
    }

    // -----------------------------------------------
    // СКИДАННЯ ПАРОЛЯ
    // -----------------------------------------------
    public void resetPassword(
            ResetPasswordRequest request) {

        User user = userRepository
                .findByResetPasswordToken(request.token())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Невірний або прострочений токен"
                        )
                );

        // Перевіряємо чи токен не прострочений
        if (user.getResetTokenExpiry() == null
                || user.getResetTokenExpiry()
                .isBefore(LocalDateTime.now())) {
            throw new RuntimeException(
                    "Токен прострочений. "
                            + "Запросіть скидання знову."
            );
        }

        // Зберігаємо новий пароль
        user.setPassword(
                passwordEncoder.encode(request.newPassword())
        );
        user.setResetPasswordToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}