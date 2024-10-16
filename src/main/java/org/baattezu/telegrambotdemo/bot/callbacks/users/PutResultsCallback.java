package org.baattezu.telegrambotdemo.bot.callbacks.users;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.GoalBot;
import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PutResultsCallback implements CallbackHandler {
    private final GoalService goalService;

    @Override
    public Object execute(Callback callback, Update update) {
        var userId = update.getCallbackQuery().getFrom().getId();
        var message = update.getCallbackQuery().getMessage();
        var chatId = message.getChatId();
        var text = message.getText();

        var newText = text + "\n\n⏳ Ожидаем ваш новый результат...";
        goalService.setUserState(userId, UserState.WAITING_FOR_RESULTS, Long.valueOf(message.getMessageId()));

        var editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setText(newText);
        editMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        return editMessage;
    }
}
