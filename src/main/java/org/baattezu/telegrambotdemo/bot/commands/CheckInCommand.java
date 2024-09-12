package org.baattezu.telegrambotdemo.bot.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.model.CheckIn;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.CheckInService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckInCommand implements Command {

    private final UserService userService;
    private final CheckInService checkInService;

    @Override
    public SendMessage execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();

        User user = userService.findById(userId);
        if (user == null) {
            return new SendMessage(String.valueOf(chatId), "Пожалуйста, зарегистрируйтесь через /register перед тем, как отмечаться.");
        }

        CheckIn checkIn = checkInService.checkInUser(user);

        return new SendMessage(String.valueOf(chatId), "Отметка успешна! Время отметки: " + checkIn.getCheckInTime());
    }
}