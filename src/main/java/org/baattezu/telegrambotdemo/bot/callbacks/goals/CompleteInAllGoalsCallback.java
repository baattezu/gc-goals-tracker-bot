package org.baattezu.telegrambotdemo.bot.callbacks.goals;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CompleteInAllGoalsCallback implements CallbackHandler {

    private final GoalService goalService;
    private final UserService userService;


    @Override
    public EditMessageText execute(Callback callback, Update update) {
        var userId = update.getCallbackQuery().getFrom().getId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();

        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var callbackData = callback.getData().split(":");
        var goalId = Long.valueOf(callbackData[0]);
        var currentPage = Integer.parseInt(callbackData[1]);
        Goal completedGoal = goalService.getGoalById(goalId);

        var user = userService.findById(userId);
        goalService.changeGoalCompletion(completedGoal);
        // Формируем новый текст сообщения
        var myGoals = goalService.getAllGoals(user.getId());

        var response = new StringBuilder(
                BotMessagesEnum.MY_GOALS_ON_THIS_WEEK.getMessage());

        // Отправляем измененное сообщение
        var editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(messageId);  // Указываем ID изменяемого сообщения
        editMessage.enableHtml(true);
        editMessage.setReplyMarkup(TelegramBotHelper
                .setAllGoalsResponseAndKeyboard(response, myGoals, currentPage, false));   // Добавляем обновленные кнопки
        editMessage.setText(response.toString());
        return editMessage;
    }
}
