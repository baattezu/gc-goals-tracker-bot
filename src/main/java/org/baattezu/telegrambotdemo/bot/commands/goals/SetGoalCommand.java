package org.baattezu.telegrambotdemo.bot.commands.goals;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.springframework.beans.factory.annotation.Value;
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
public class SetGoalCommand implements Command {

    @Value("${telegram.bot.url}")
    private String botUrl;

    private final UserService userService;
    private final GoalService goalService;


    @Override
    public SendMessage execute(Update update) {

        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();

        var message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        if (update.getMessage().isGroupMessage()){
            return TelegramBotHelper.justGoToPrivateMessage(
                    BotMessagesEnum.SET_GOAL_TO_PRIVATE_MESSAGE.getMessage(botUrl),
                    botUrl, update, true);
        }

        User user = userService.findById(userId);
        if (user == null) {
            message.setText(BotMessagesEnum.SET_GOAL_REGISTER_MESSAGE.getMessage());
            return message;
        }
        Goal blankGoal = goalService.createBlankGoal(user);

        message.setText(BotMessagesEnum.SET_GOAL_MESSAGE.getMessage());

        setReplyMarkup(blankGoal, message);

        return message;
    }

    private static void setReplyMarkup(Goal blankGoal, SendMessage message) {
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
    }
}
