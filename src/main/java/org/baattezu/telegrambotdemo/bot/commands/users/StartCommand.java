package org.baattezu.telegrambotdemo.bot.commands.users;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {

    private final UserService userService;
    @Override
    public SendMessage execute(Update update) {
        var chatId = update.getMessage().getChatId();
        var userId = update.getMessage().getFrom().getId();

        var message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        if (update.getMessage().isGroupMessage()){
            return null;
        }
        if (userService.isRegistered(userId)){
            message.setText(BotMessagesEnum.ALREADY_REGISTERED_MESSAGE.getMessage());
            return message;
        }
        String username = update.getMessage().getFrom().getUserName();

        userService.registerUser(userId, username, chatId);
        message.setText(BotMessagesEnum.REGISTER_SUCCESS_MESSAGE.getMessage(username));

        return message;
    }
}
