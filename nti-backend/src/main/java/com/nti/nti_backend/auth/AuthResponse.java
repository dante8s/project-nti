package com.nti.nti_backend.auth;

public record AuthResponse(
        String token,
        String name,
        String email,
        String role,
        boolean emailVerified,
        boolean onboardingCompleted
) {}