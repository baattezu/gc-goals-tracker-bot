package org.baattezu.telegrambotdemo.bot.commands.users;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChangeNameCommand implements Command {

    private final UserService userService;

    @Override
    public SendMessage execute(Update update) {
        var chatId = update.getMessage().getChatId();
        var userId = update.getMessage().getFrom().getId();

        var message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        String username = update.getMessage().getFrom().getUserName();

        String messageText = update.getMessage().getText();
        String[] parts = messageText.split(" ", 2);
        if (parts.length < 2 || parts[1].isEmpty()) {
            message.setText(BotMessagesEnum.PUT_NAME_AFTER_CHANGE_NAME_COMMAND.getMessage());
            return message;
        }
        String name = parts[1].trim();

        userService.changeName(userId, username);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        TelegramBotHelper.deleteLastMessages(message, update.getMessage().getMessageId(), keyboard);

        var markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);
        message.setReplyMarkup(markupInline);
        message.setText(BotMessagesEnum.CHANGE_NAME_SUCCESS_MESSAGE.getMessage(name));
        return message;
    }
}