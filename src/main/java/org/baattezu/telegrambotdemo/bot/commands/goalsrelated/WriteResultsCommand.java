package org.baattezu.telegrambotdemo.bot.commands.goalsrelated;


import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.data.UserState;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WriteResultsCommand implements Command {

    @Value("${telegram.bot.url}")
    private String botUrl;
    private final UserService userService;
    private final GoalService goalService;

    @Override
    public SendMessage execute(Update update) {
        var chatId = update.getMessage().getChatId();
        var userId = update.getMessage().getFrom().getId();

        var message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        if (update.getMessage().isGroupMessage()){
            return TelegramBotHelper.justGoToPrivateMessage(BotMessagesEnum.PUT_RESULTS.getMessage(botUrl), botUrl, update, true);
        }

        var user = userService.findById(userId);
        var messageText = BotMessagesEnum.MY_RESULTS.getMessage(user.getResultsForWeek());

        TelegramBotHelper.addResultsButtons(message, userId);
        message.setText(messageText);
        return message;
    }



}
