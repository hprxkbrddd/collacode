package com.collacode.auth.controller;

import com.collacode.auth.dto.UserEntityDTO;
import com.collacode.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/collacode/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserEntityDTO> getUserById(@RequestParam String userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId).toDTO());
    }
}
