package org.baattezu.telegrambotdemo.bot;

import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
//import org.baattezu.telegrambotdemo.bot.callbacks.goal_creation.SetGoalCallback;
import org.baattezu.telegrambotdemo.bot.callbacks.goal_creation.EnterDeadlineCallback;
import org.baattezu.telegrambotdemo.bot.callbacks.goal_creation.StartGoalCreationCallback;
import org.baattezu.telegrambotdemo.bot.callbacks.view_and_edit_goals.CompleteGoalCallback;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class CallbacksHandler {

    private final UserService userService;
    private final Map<CallbackType, CallbackHandler> callbacks;

    public CallbacksHandler(
            UserService userService, StartGoalCreationCallback setGoalCallback,
            EnterDeadlineCallback enterDeadlineCallback,
            CompleteGoalCallback completeGoalCallback
    ) {
        this.userService = userService;
        this.callbacks = Map.of(
                CallbackType.SET_GOAL, setGoalCallback,
                CallbackType.ENTER_DEADLINE, enterDeadlineCallback,
                CallbackType.COMPLETE_GOAL, completeGoalCallback
        );
    }

    public Object handleCallbacks(Update update) {
        List<String> list = JsonHandler.toList(update.getCallbackQuery().getData());
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (list.isEmpty()) {
            return new SendMessage(String.valueOf(chatId), "Возникла ошибка при обработке ответа. \uD83D\uDE23");
        } else {
            Callback callback = Callback.builder()
                    .callbackType(CallbackType.valueOf(list.get(0)))
                    .data(list.get(1)).build();

            CallbackHandler<?> callbackBiFunction = callbacks.get(callback.getCallbackType());
            return callbackBiFunction.execute(callback, update);
        }
    }
}

