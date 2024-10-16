package org.baattezu.telegrambotdemo.bot.commands.results;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.results.ScheduledResultsGenerator;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.yaml.snakeyaml.error.Mark;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Top5Results implements Command {
    private final ScheduledResultsGenerator resultsGenerator;

    @Override
    public SendMessage execute(Update update) {
        var chatId = String.valueOf(update.getMessage().getChatId());
        var message = new SendMessage(chatId, resultsGenerator.getTop5UsersInGroupMessage(chatId));
        message.enableMarkdown(true);
        addButtons(chatId, message);
        return message;
    }
    private void addButtons(String chatId, SendMessage sendMessage){
        var topUsers = resultsGenerator.getTop5UsersInGroup(chatId);
        InlineKeyboardButton[] buttons = new InlineKeyboardButton[topUsers.size()];
        int index = 1;
        for (Map.Entry<User, Double> entry : topUsers) {
            User user = entry.getKey();
            buttons[index] = Markup.Button.create(index + "#", CallbackType.VIEW_USER_RESULTS_FROM_TOP, user.getId() + ":" + index);
            index++;
        }
        sendMessage.setReplyMarkup(Markup.keyboard().addRow(buttons).build());
    }
}
