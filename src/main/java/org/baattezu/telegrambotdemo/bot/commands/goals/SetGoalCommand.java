package org.baattezu.telegrambotdemo.bot.commands.goals;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.data.UserState;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
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
        var message = update.getMessage();
        var chatId = message.getChatId();
        var userId = message.getFrom().getId();
        String text = null;

        if (update.getMessage().isGroupMessage()){
            return TelegramBotHelper.justGoToPrivateMessage(chatId, BotMessagesEnum.SET_GOAL_TO_PRIVATE_MESSAGE.getMessage(botUrl), botUrl);
        }

        User user = userService.findById(userId);
        if (user == null) {
            return TelegramBotHelper.registerBeforeMessage(chatId, "создать цель. \uD83D\uDCDD✅");
        }
        Goal blankGoal = goalService.createBlankGoal(userId);
        goalService.setUserState(userId, UserState.WAITING_FOR_TITLE, blankGoal.getId());
        text = BotMessagesEnum.SET_GOAL_START_MESSAGE.getMessage();


        var sendMessage = new SendMessage(String.valueOf(chatId), text);
        sendMessage.setReplyMarkup(new ForceReplyKeyboard());
        return sendMessage;
    }
}
