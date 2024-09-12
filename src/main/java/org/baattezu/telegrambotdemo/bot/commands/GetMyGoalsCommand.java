package org.baattezu.telegrambotdemo.bot.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetMyGoalsCommand implements Command{

    private final UserService userService;
    private final GoalService goalService;

    @Override
    public SendMessage execute(Update update) {
        var userId = update.getMessage().getFrom().getId();
        var chatId = update.getMessage().getChatId();

        var user = userService.findById(userId);
        if (user == null) {
            return new SendMessage(String.valueOf(chatId), "Пожалуйста, зарегистрируйтесь через /register перед тем, как создать цель.");
        }
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

        var message =  new SendMessage(String.valueOf(chatId), response);
        message.setParseMode("Markdown");
        message.setReplyMarkup(markup);  // Устанавливаем разметку с кнопками
        return message;
    }
}
