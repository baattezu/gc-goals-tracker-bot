package org.baattezu.telegrambotdemo.bot.callbacks.message;


import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeleteMessageCallback implements CallbackHandler {
    @Override
    public Object execute(Callback callback, Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var botMessageId = update.getCallbackQuery().getMessage().getMessageId();

        List<DeleteMessage> deleteMessages = new ArrayList<>();

        var deleteMessageFromBot = new DeleteMessage();
        deleteMessageFromBot.setChatId(chatId);
        deleteMessageFromBot.setMessageId(botMessageId);
        deleteMessages.add(deleteMessageFromBot);

        if (!callback.getData().equals("nothing")){
            var deleteMessageFromUser = new DeleteMessage();
            deleteMessageFromUser.setChatId(chatId);
            deleteMessageFromUser.setMessageId(Integer.valueOf(callback.getData()));
            deleteMessages.add(deleteMessageFromUser);
        }

        return deleteMessages;
    }
}
