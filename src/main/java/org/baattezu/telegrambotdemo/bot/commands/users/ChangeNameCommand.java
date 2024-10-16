package org.baattezu.telegrambotdemo.bot.commands.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeNameCommand implements Command {

    private final UserService userService;

    @Override
    public SendMessage execute(Update update) {
        String messageText = update.getMessage().getText();
        String[] parts = messageText.split(" ", 2);

        var message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());

        if (parts.length < 2 || parts[1].isEmpty()) {
            message.setText(BotMessagesEnum.PUT_NAME_AFTER_CHANGE_NAME_COMMAND.getMessage());
            return message;
        }

        var user = userService.findById(update.getMessage().getFrom().getId());
        String name = parts[1].trim();

        userService.changeName(user, name);

        message.setReplyMarkup(Markup.keyboard()
                .addRow(Markup.Button.create("Ок", CallbackType.DELETE_LAST_MESSAGES, String.valueOf(update.getMessage().getMessageId())))
                .build());
        message.setText(BotMessagesEnum.CHANGE_NAME_SUCCESS_MESSAGE.getMessage(name));
        return message;
    }
}