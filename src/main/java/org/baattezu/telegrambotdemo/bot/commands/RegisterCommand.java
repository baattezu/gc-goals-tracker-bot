package org.baattezu.telegrambotdemo.bot.commands;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

@Component
@RequiredArgsConstructor
public class RegisterCommand implements Command {

    private final UserService userService;

    @Override
    public SendMessage execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        String username = update.getMessage().getFrom().getUserName();

        String messageText = update.getMessage().getText();
        String[] parts = messageText.split(" ", 2);
        if (parts.length < 2 || parts[1].isEmpty()) {
            return new SendMessage(String.valueOf(chatId), "Пожалуйста, укажите свое имя через пробел после команды /register.");
        }
        String name = parts[1].trim();

        // Создаем или обновляем пользователя в базе данных
        userService.registerUser(userId, username, name);

        // Отправляем пользователю сообщение о том, что он успешно зарегистрирован
        return new SendMessage(String.valueOf(chatId), "\uD83D\uDC4B Привет, " + name + "! \uD83C\uDF89 Ты успешно зарегистрирован. ✅");
    }
}