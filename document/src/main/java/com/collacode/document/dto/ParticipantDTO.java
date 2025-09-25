package com.collacode.document.dto;

import com.collacode.document.enums.Role;

public record ParticipantDTO(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        Role role
) {
}
