package org.baattezu.telegrambotdemo.bot.commands.goals;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
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
public class GetMyGoalsCommand implements Command {

    private final UserService userService;
    private final GoalService goalService;

    @Override
    public SendMessage execute(Update update) {
        var message = update.getMessage();
        var userId = message.getFrom().getId();
        var chatId = message.getChatId();
        var isGroupMessage = message.isGroupMessage();

        var user = userService.findById(userId);
        if (user == null) {
            return TelegramBotHelper.registerBeforeMessage(chatId,"работать с целями.");
        }
        var myGoals = goalService.getAllGoals(userId);

        var whoseGoals = isGroupMessage ?
                BotMessagesEnum.GOALS_OF_USER_THIS_WEEK.getMessage(userId, userId, user.getUsername()):
                BotMessagesEnum.MY_GOALS_ON_THIS_WEEK.getMessage();
        var response = new StringBuilder(whoseGoals);

        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(true);
        sendMessage.setReplyMarkup(TelegramBotHelper
                .setAllGoalsResponseAndKeyboard(response, myGoals,1, isGroupMessage));  // Устанавливаем разметку с кнопками
        sendMessage.setText(response.toString());
        return sendMessage;
    }
}
