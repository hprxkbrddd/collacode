package com.collacode.auth.service;

import com.collacode.auth.component.CCJwtDecoder;
import com.collacode.auth.dto.RegistrationDTO;
import com.collacode.auth.dto.TokenResponseDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class KeycloakService {
    @Value("${keycloak.url.public}")
    private String keycloakPublicUrl;
    @Value("${keycloak.url.admin}")
    private String keycloakAdminUrl;

    private WebClient webClientPublic;
    private WebClient webClientAdmin;
    private final CCJwtDecoder jwtDecoder;

    @PostConstruct
    public void init(){
        this.webClientPublic = WebClient.builder()
                .baseUrl(keycloakPublicUrl)
                .build();
        this.webClientAdmin = WebClient.builder()
                .baseUrl(keycloakAdminUrl)
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

                    return webClientAdmin.post()
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

        return webClientPublic.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(TokenResponseDTO.class);
    }

    public Boolean validate(String token){
        Instant exp = jwtDecoder.jwtDecoder().decode(token).getExpiresAt();
        return exp != null && !exp.isBefore(Instant.now());

    }
}
