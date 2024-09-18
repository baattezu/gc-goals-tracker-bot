package org.baattezu.telegrambotdemo.bot.callbacks.goals;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartGoalCreationCallback implements CallbackHandler {
    private final GoalService goalService;

    @Override
    public SendMessage execute(Callback callback, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long userId = update.getCallbackQuery().getFrom().getId();

        var message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        message.setText(BotMessagesEnum.SET_GOAL_START_MESSAGE.getMessage());
        goalService.setUserState(userId, UserState.WAITING_FOR_TITLE, Long.valueOf(callback.getData()));

        return message;
    }
}

