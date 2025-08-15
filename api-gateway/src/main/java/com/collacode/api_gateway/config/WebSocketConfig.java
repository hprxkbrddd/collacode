package com.collacode.api_gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * Настраивает брокер сообщений для обработки подписок и рассылки сообщений
     * @param config Реестр для конфигурации брокера
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Включаем простой in-memory брокер для отправки сообщений клиентам
        // Префикс "/topic" указывает, что это адреса для broadcast-рассылки
        config.enableSimpleBroker("/topic");

        // Префикс для сообщений, которые направляются в @MessageMapping методы
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Регистрирует WebSocket endpoint для клиентских подключений
     * @param registry Реестр для регистрации endpoint'ов
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")  // URL для подключения WebSocket
                .setAllowedOrigins("*")  // Разрешаем все origins (для разработки)
                .withSockJS();  // Добавляем fallback для SockJS (на случай если WebSocket не доступен)
    }
}
