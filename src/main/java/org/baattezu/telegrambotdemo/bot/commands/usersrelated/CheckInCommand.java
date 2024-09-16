package org.baattezu.telegrambotdemo.bot.commands.usersrelated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.model.CheckIn;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.CheckInService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
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
        var chatId = update.getMessage().getChatId();
        var userId = update.getMessage().getFrom().getId();

        var message = new SendMessage();
        message.setChatId(chatId);

        User user = userService.findById(userId);

        if (user == null) {
            message.setText(BotMessagesEnum.REGISTER_BEFORE_CHECKIN_MESSAGE.getMessage());
            return message;
        }

        CheckIn checkIn = checkInService.checkInUser(user);

        message.setText(BotMessagesEnum.CHECKIN_MESSAGE.getMessage(checkIn.getCheckInTime()));
        return message;
    }
}