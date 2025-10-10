package com.collacode.document.dto;

import com.collacode.document.enums.Role;

public record ParticipantDTO(
        UserEntityDTO userEntity,
        Role role
) {
}
