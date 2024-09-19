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
    private final UserService userService;
    private final GoalService goalService;

    @Override
    public Object execute(Callback callback, Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var userId = update.getCallbackQuery().getFrom().getId();

        goalService.setUserState(userId, UserState.WAITING_FOR_RESULTS, Long.valueOf(update.getCallbackQuery().getMessage().getMessageId()));

        var editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        var newText = update.getCallbackQuery().getMessage().getText() + "\n\n⏳ Ожидаем ваш новый результат...";
        editMessage.setText(newText);
        editMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessage.setReplyMarkup(update.getCallbackQuery().getMessage().getReplyMarkup());
        return editMessage;
    }
}
