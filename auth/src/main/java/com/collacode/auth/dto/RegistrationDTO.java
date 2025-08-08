package com.collacode.auth.dto;

public record RegistrationDTO(
        String username,
        String password,
        String email,
        String firstName,
        String lastName,
        boolean isEnabled
) {
}
