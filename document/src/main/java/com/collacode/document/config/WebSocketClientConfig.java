package com.collacode.document.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Configuration
public class WebSocketClientConfig {
    @Bean
    public WebSocketStompClient stompClient(){
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient(); // websocket client
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient); // stomp client upon the websocket client
        stompClient.setMessageConverter(new MappingJackson2MessageConverter()); // message converter
        return stompClient;
    }
}
