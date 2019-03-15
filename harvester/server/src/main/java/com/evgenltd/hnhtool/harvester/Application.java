package com.evgenltd.hnhtool.harvester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 14-03-2019 22:06</p>
 */
@SpringBootApplication
@EnableScheduling
@EnableWebSocketMessageBroker
public class Application extends SpringBootServletInitializer implements WebSocketMessageBrokerConfigurer {

    public static final String BROKER_PATH = "/topic";
    public static final String APPLICATION_PREFIX = "/app";
    public static final String ENDPOINT = "/harvester";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(BROKER_PATH);
        registry.setApplicationDestinationPrefixes(APPLICATION_PREFIX);
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint(ENDPOINT).withSockJS();
    }

}
