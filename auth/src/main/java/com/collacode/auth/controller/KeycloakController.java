package com.collacode.auth.controller;

import com.collacode.auth.dto.AuthDTO;
import com.collacode.auth.dto.RegistrationDTO;
import com.collacode.auth.dto.UserEntityDTO;
import com.collacode.auth.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/collacode/v1/auth")
@RequiredArgsConstructor
public class KeycloakController {

    private final KeycloakService keycloakService;

    @GetMapping("/validate")
    public ResponseEntity<UserEntityDTO> validate(@RequestHeader("Authorization") String header){
        return ResponseEntity.ok(
                keycloakService.validate(header.substring(7)));
    }

    private void printValues(Map<?, ?> map){
        for (Object key : map.keySet()) {
            System.out.println(key + ": " + map.get(key));
        }
    }

    @PostMapping("/token")
    public Mono<ResponseEntity<String>> getToken(@RequestBody AuthDTO dto, @RequestHeader Map<String, String> headers) {
        System.out.println("received headers");
        printValues(headers);
        return keycloakService.getToken(dto.username(), dto.password())
                .map(tokenResponse -> ResponseEntity.ok(tokenResponse.access_token()));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register (@RequestBody RegistrationDTO dto){
        return keycloakService.register(dto);
    }
}
