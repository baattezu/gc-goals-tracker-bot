package org.baattezu.telegrambotdemo.bot.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.model.CheckIn;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.model.User;
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
@Slf4j
@RequiredArgsConstructor
public class SetGoalCommand implements Command{

    private final UserService userService;
    private final GoalService goalService;

    @Override
    public SendMessage execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();



        User user = userService.findById(userId);
        if (user == null) {
            return new SendMessage(String.valueOf(chatId), "Пожалуйста, зарегистрируйтесь через /register перед тем, как создать цель. \uD83D\uDCDD✅");
        }
        Goal blankGoal = goalService.createBlankGoal(user);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Нажмите кнопку ниже, чтобы установить цель на неделю. \uD83C\uDFAF\uD83D\uDC47");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton setGoalButton = new InlineKeyboardButton();
        String jsonCallback = JsonHandler.toJson(List.of(CallbackType.SET_GOAL, blankGoal.getId().toString()));
        setGoalButton.setText("Создать цель");
        setGoalButton.setCallbackData(jsonCallback);
        rowInline.add(setGoalButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        return message;
    }
}
