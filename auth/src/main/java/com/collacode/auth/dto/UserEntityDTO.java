package com.collacode.auth.dto;

public record UserEntityDTO(
        String id,
        String email,
        String firstName,
        String lastName,
        String username
) {
}
