package com.collacode.auth.service;

import com.collacode.auth.entity.UserEntity;
import com.collacode.auth.repository.UsersRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository usersRepository;

    public UserEntity getUserInfo(String id){
        return usersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entity not found"));
    }
}
