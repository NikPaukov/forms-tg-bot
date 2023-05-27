package com.example.polltgbot.bot;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class BotConfig {

    private final String name;
    private final String token;
    public BotConfig(Environment env) {
        this.name = env.getProperty("bot.name");
        this.token = env.getProperty("bot.key");
    }


    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

}
