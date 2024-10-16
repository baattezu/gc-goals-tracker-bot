package org.baattezu.telegrambotdemo.bot.callbacks.users;

import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class StartWorkCallback implements CallbackHandler {
    @Override
    public List<BotApiMethod> execute(Callback callback, Update update) {
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();

        List<BotApiMethod> botApiMethods = new ArrayList<>();

        EditMessageText editMessage = new EditMessageText();
        editMessage.setText("Что может");
        editMessage.setMessageId(messageId);
        editMessage.setChatId(chatId);
        editMessage.setReplyMarkup(null);
        botApiMethods.add(editMessage);

        PinChatMessage pinChatMessage = new PinChatMessage();
        pinChatMessage.setMessageId(messageId);
        pinChatMessage.setChatId(chatId);
        pinChatMessage.setDisableNotification(true);
        botApiMethods.add(pinChatMessage);

        return botApiMethods;
    }
}
