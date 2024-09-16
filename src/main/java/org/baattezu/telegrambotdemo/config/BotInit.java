package org.baattezu.telegrambotdemo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.GoalBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotInit {

    private final GoalBot telegramBot;


    public void registerCommands() {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/help", "Получить список доступных команд и их описание."));
        commands.add(new BotCommand("/info", "Получить информацию о текущем статусе или состоянии бота."));
        commands.add(new BotCommand("/change_name", "Изменение вашего имени."));
        commands.add(new BotCommand("/pin_to_chat", "Закрепить пользователя в чате"));
        commands.add(new BotCommand("/set_goal", "Установить новую цель."));
        commands.add(new BotCommand("/my_goals", "Просмотр ваших целей."));
        commands.add(new BotCommand("/results", "Отправить результаты."));
        commands.add(new BotCommand("/list_users", "Показать всех пользователей чата."));

        SetMyCommands setMyCommands = SetMyCommands.builder()
                .commands(commands) // Добавление списка команд
                .scope(new BotCommandScopeDefault()) // Установка области видимости команд
                .languageCode("ru") // Установка кода языка (например, "ru" для русского)
                .build();
        SetMyCommands setMyCommands1 = new SetMyCommands(commands, null , null);
        try {
            telegramBot.execute(setMyCommands1);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(telegramBot);
            this.registerCommands();
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}