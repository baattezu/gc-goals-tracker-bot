package org.baattezu.telegrambotdemo.bot.commands.goals;


import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WriteResultsCommand implements Command {

    @Value("${telegram.bot.url}")
    private String botUrl;
    private final UserService userService;

    @Override
    public SendMessage execute(Update update) {
        long chatId = update.getMessage().getChatId();
        var user = userService.findById(update.getMessage().getFrom().getId());
        var message = new SendMessage(
                String.valueOf(chatId),
                BotMessagesEnum.MY_RESULTS.getMessage(user.getResultsForWeek())
        );
        if (update.getMessage().isGroupMessage()){
            return TelegramBotHelper.justGoToPrivateMessage(chatId, BotMessagesEnum.PUT_RESULTS.getMessage(botUrl), botUrl);
        }
        message.setReplyMarkup(Markup.keyboard().addRow(
                Markup.Button.create("Переписать результаты за неделю", CallbackType.PUT_RESULTS, String.valueOf(user.getId()))
        ).build());
        return message;
    }



}
