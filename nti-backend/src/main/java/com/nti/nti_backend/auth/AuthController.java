package com.nti.nti_backend.auth;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {
        String token = authService.register(request);
        return ResponseEntity.ok(token);
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // GET /api/auth/verify?token=UUID
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(
            @RequestParam String token) {
        String message = authService.verifyEmail(token);
        return ResponseEntity.ok(message);
    }

    // POST /api/auth/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody
            ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        // Завжди повертаємо один і той самий текст
        // щоб не видавати чи існує email
        return ResponseEntity.ok(
                "Якщо email існує — лист надіслано"
        );
    }

    // POST /api/auth/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody
            ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(
                "Пароль успішно змінено"
        );
    }
}
