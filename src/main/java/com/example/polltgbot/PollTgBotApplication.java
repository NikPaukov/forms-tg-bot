package com.example.polltgbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.util.*;

@SpringBootApplication
public class PollTgBotApplication {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    List<BotApiMethod<?>> emptyList() {
        return List.of();
    }

    public static void main(String[] args) {
        SpringApplication.run(PollTgBotApplication.class, args);
    }

}
