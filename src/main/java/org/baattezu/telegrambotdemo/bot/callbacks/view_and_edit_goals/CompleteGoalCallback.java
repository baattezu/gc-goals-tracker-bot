package org.baattezu.telegrambotdemo.bot.callbacks.view_and_edit_goals;


import jakarta.persistence.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.callbacks.Callback;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbackHandler;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CompleteGoalCallback implements CallbackHandler {

    private final GoalService goalService;
    private final UserService userService;


    @Override
    public EditMessageText execute(Callback callback, Update update) {
        var userId = update.getCallbackQuery().getFrom().getId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();

        var goalId = Long.valueOf(callback.getData());
        Goal completedGoal = goalService.getGoalById(goalId);

        var user = userService.findById(userId);
        goalService.changeGoalCompletion(completedGoal);
        // Формируем новый текст сообщения
        var myGoals = goalService.getAllGoals(user);

        String response = "✨ *Мои цели на эту неделю:* \n \n";
        int index = 1;
        var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        myGoals.sort(Comparator
                .comparing(Goal::getCreatedAt)              // Сначала по дате
                .thenComparing(Goal::getCompleted));


        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (Goal goal: myGoals) {
            String status = goal.getCompleted() ? "✅" : "";
            response += String.format(
                    "🎯 *Цель #%d:* %s \n\"%s\" \n📅 *Дедлайн:* %s \n🏆 *Вознаграждение:* \"%s\" \n\n",
                    index, status, goal.getGoalName(), goal.getDeadline().format(formatter), goal.getReward()
            );
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText( "#"+ index + " " + (goal.getCompleted() ? "Отменить выполнение" : "Отметить выполненной"));
            String jsonCallback = JsonHandler.toJson(List.of(CallbackType.COMPLETE_GOAL, goal.getId()));
            button.setCallbackData(jsonCallback); // Используем айди цели

            // Добавляем кнопку в разметку
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
            index++;
        }
        // Добавляем клавиатуру в сообщение
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        // Отправляем измененное сообщение
        var editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(messageId);  // Указываем ID изменяемого сообщения
        editMessage.setText(response);
        editMessage.setParseMode("Markdown");
        editMessage.setReplyMarkup(markup);   // Добавляем обновленные кнопки

        return editMessage;
    }
}
