package com.collacode.auth.service;

import com.collacode.auth.dto.AuthDTO;
import com.collacode.auth.dto.RegistrationDTO;
import com.collacode.auth.dto.TokenResponseDTO;
import jakarta.annotation.PostConstruct;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
public class KeycloakService {
    @Value("${keycloak.url.login}")
    private String keycloakLoginUrl;
    @Value("${keycloak.url.reg}")
    private String keycloakRegUrl;

    private WebClient webClientLogin;
    private WebClient webClientReg;

    @PostConstruct
    public void init(){
        this.webClientLogin = WebClient.builder()
                .baseUrl(keycloakLoginUrl)
                .build();
        this.webClientReg = WebClient.builder()
                .baseUrl(keycloakRegUrl)
                .build();
    }
    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;
    @Value("${keycloak.admin-username}")
    private String adminUsername;
    @Value("${keycloak.admin-password}")
    private String adminPassword;

    private Mono<TokenResponseDTO> getAdminToken() {
        return getToken(adminUsername, adminPassword);
    }

    public Mono<ResponseEntity<String>> register(RegistrationDTO dto){
        return getAdminToken()
                .flatMap(token ->{
                    UserRepresentation user = new UserRepresentation();
                    user.setUsername(dto.username());
                    user.setEmail(dto.email());
                    user.setFirstName(dto.firstName());
                    user.setLastName(dto.lastName());
                    user.setEnabled(dto.isEnabled());

                    CredentialRepresentation credential = new CredentialRepresentation();
                    credential.setType(CredentialRepresentation.PASSWORD);
                    credential.setValue(dto.password());
                    credential.setTemporary(false);

                    user.setCredentials(List.of(credential));

                    return webClientReg.post()
                            .uri("/users")
                            .header("Authorization", "Bearer " + token.access_token())
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(user)
                            .retrieve()
                            .toBodilessEntity()
                            .map(response -> {
                                if (response.getStatusCode().is2xxSuccessful())
                                    return ResponseEntity.ok("User registered successfully");
                                return ResponseEntity.status(response.getStatusCode())
                                        .body("Failed to register user");
                            });
                });

    }

    public Mono<TokenResponseDTO> getToken(String username, String password) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", username);
        formData.add("password", password);
        formData.add("scope", "openid");

        return webClientLogin.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(TokenResponseDTO.class);
    }
}
