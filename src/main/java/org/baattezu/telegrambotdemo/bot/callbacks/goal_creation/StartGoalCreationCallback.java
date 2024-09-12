package org.baattezu.telegrambotdemo.bot.callbacks.goal_creation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
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
public class StartGoalCreationCallback implements CallbackHandler {
    private final GoalService goalService;

    @Override
    public SendMessage execute(Callback callback, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long userId = update.getCallbackQuery().getFrom().getId();

        SendMessage message = new SendMessage();
        message.setText("\uD83D\uDE80 Начинаем создание цели. Пожалуйста, опишите свою цель:");
        message.setChatId(String.valueOf(chatId));

        goalService.setUserState(userId, UserState.WAITING_FOR_TITLE, Long.valueOf(callback.getData()));
        log.info("Переход к обработке сообщений");
        return message;
    }
}

