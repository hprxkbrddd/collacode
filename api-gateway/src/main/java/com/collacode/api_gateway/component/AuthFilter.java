package com.collacode.api_gateway.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static org.apache.tomcat.websocket.server.UpgradeUtil.isWebSocketUpgradeRequest;

@Component
@Setter
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
    private final WebClient webClient;
    @Value("${url.auth}")
    private String authPath;

    public AuthFilter() {
        super(Config.class);
        this.webClient = WebClient.builder().baseUrl(authPath).build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            System.out.println("=== JWT Auth Filter started ===");

            // Логируем путь запроса
            String path = exchange.getRequest().getPath().toString();
            System.out.println("Request path: " + path);

            // 1. Пропускаем все WebSocket и SockJS запросы
            if (isWebSocketOrSockJsRequest(path, exchange.getRequest().getHeaders())) {
                System.out.println("🔄 Skipping auth for WebSocket/SockJS request: " + path);
                return chain.filter(exchange);
            }

            // Пропускаем аутентификацию для эндпоинтов auth-service
            if (path.startsWith("/collacode/v1/auth")) {
                System.out.println("Skipping auth for auth-service endpoints");
                return chain.filter(exchange);
            }

            // Проверяем наличие Authorization header
            String header = exchange.getRequest().getHeaders().getFirst("Authorization");
            System.out.println("Authorization header: " + header);

            if (header == null || !header.startsWith("Bearer ")) {
                System.out.println("No valid Bearer token found, returning 401");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Извлекаем токен
            String token = header.substring(7);
            System.out.println("Extracted token: " + token.substring(0, Math.min(token.length(), 10)) + "..."); // Логируем первые 10 символов

            System.out.println("Validating token via auth-service...");

            return webClient.get()
                    .uri("/validate")
                    .header("Authorization", header)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> {
                        System.out.println("Token validation successful, proceeding with request");
                        return chain.filter(exchange);
                    })
                    .onErrorResume(e -> {
                        System.out.println("Token validation failed: " + e.getMessage());
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });

        });
    }

    private boolean isWebSocketOrSockJsRequest(String path, HttpHeaders headers) {
        // WebSocket upgrade request
        String connection = headers.getFirst("Connection");
        String upgrade = headers.getFirst("Upgrade");
        boolean isWebSocketUpgrade = "websocket".equalsIgnoreCase(upgrade) &&
                connection != null && connection.toLowerCase().contains("upgrade");

        // SockJS endpoints (точное совпадение)
        boolean isSockJsEndpoint = path.equals("/ws") ||
                path.startsWith("/ws/") ||
                path.contains("/info") ||
                path.contains("/websocket") ||
                path.contains("/xhr") ||
                path.contains("/jsonp") ||
                path.contains("/iframe") ||
                path.contains("/send");

        return isWebSocketUpgrade || isSockJsEndpoint;
    }

    @Getter
    @Setter
    public static class Config {
        private String authServiceUrl;
    }
}
