package com.collacode.auth.entity;

import com.collacode.auth.dto.UserEntityDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.Id;

@Entity
@Data
@Table(name = "user_entity")
public class UserEntity {
    @Id
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String username;

    public UserEntityDTO toDTO() {
        return new UserEntityDTO(
                id,
                username
        );
    }
}