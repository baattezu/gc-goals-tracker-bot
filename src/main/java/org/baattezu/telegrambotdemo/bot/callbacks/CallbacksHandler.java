package org.baattezu.telegrambotdemo.bot.callbacks;

import lombok.extern.slf4j.Slf4j;
//import org.baattezu.telegrambotdemo.bot.callbacks.goal_creation.SetGoalCallback;
import org.baattezu.telegrambotdemo.bot.callbacks.goals.*;
import org.baattezu.telegrambotdemo.bot.callbacks.message.DeleteMessageCallback;
import org.baattezu.telegrambotdemo.bot.callbacks.results.ViewResultsCallback;
import org.baattezu.telegrambotdemo.bot.callbacks.users.PinToChatFromWelcomeMessageCallback;
import org.baattezu.telegrambotdemo.bot.callbacks.users.PutResultsCallback;
import org.baattezu.telegrambotdemo.bot.callbacks.users.StartWorkCallback;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class CallbacksHandler {
    private final Map<CallbackType, CallbackHandler> callbacks;

    public CallbacksHandler(
            JustCallback justCallback,
            DeleteMessageCallback deleteMessageCallback,
            PinToChatFromWelcomeMessageCallback pinToChatFromWelcomeMessageCallback,
            PutResultsCallback putResultsCallback,
            ViewResultsCallback viewResultsCallback,
            CompleteInAllGoalsCallback completeInAllGoalsCallback,
            NavigationCallback nextGoalPageCallback,
            StartWorkCallback startWorkCallback
    ) {
        this.callbacks = new HashMap<>();
        callbacks.put(CallbackType.VIEW_USER_RESULTS_FROM_TOP, viewResultsCallback);
        callbacks.put(CallbackType.COMPLETE_IN_ALL_GOALS, completeInAllGoalsCallback);
        callbacks.put(CallbackType.GO_TO_PRIVATE_CHAT, justCallback);
        callbacks.put(CallbackType.DELETE_LAST_MESSAGES, deleteMessageCallback);
        callbacks.put(CallbackType.PIN_TO_CHAT, pinToChatFromWelcomeMessageCallback);
        callbacks.put(CallbackType.PUT_RESULTS, putResultsCallback);
        callbacks.put(CallbackType.NEXT_PAGE, nextGoalPageCallback);
        callbacks.put(CallbackType.START_WORK_WITH_BOT, startWorkCallback);
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

