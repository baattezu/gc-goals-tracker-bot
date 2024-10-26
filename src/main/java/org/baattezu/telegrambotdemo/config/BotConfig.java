package org.baattezu.telegrambotdemo.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@Slf4j
public class BotConfig {

    private final Environment env;

    // Статические переменные для глобального доступа
    public static String BOT_URL;
    public static String BOT_NAME;
    public static String BOT_TOKEN;

    @Autowired
    public BotConfig(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() {
        BOT_URL = env.getProperty("TELEGRAM_BOT_URL", "https://default-url.com");
        BOT_NAME = env.getProperty("TELEGRAM_BOT_NAME", "default_bot_name");
        BOT_TOKEN = env.getProperty("TELEGRAM_BOT_TOKEN", "default_token");
        if (BOT_TOKEN.equals("default_token")){
            Dotenv dotenv = Dotenv.load();
            BOT_URL = dotenv.get("TELEGRAM_BOT_URL", "https://default-url.com");
            BOT_NAME = dotenv.get("TELEGRAM_BOT_NAME", "default_bot_name");
            BOT_TOKEN = dotenv.get("TELEGRAM_BOT_TOKEN", "default_token");
        }
    }
}

