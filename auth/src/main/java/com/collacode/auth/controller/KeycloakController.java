package com.collacode.auth.controller;

import com.collacode.auth.dto.AuthDTO;
import com.collacode.auth.dto.RegistrationDTO;
import com.collacode.auth.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/collacode/v1/auth")
@RequiredArgsConstructor
public class KeycloakController {

    private final KeycloakService keycloakService;

    @GetMapping("/validate")
    public Boolean validate(@RequestHeader("Authorization") String header){
        return keycloakService.validate(header.substring(7));
    }

    @PostMapping("/token")
    public Mono<ResponseEntity<String>> getToken(@RequestBody AuthDTO dto) {
        return keycloakService.getToken(dto.username(), dto.password())
                .map(tokenResponse -> ResponseEntity.ok(tokenResponse.access_token()));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register (@RequestBody RegistrationDTO dto){
        return keycloakService.register(dto);
    }
}
