package org.baattezu.telegrambotdemo.bot.commands.results;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.results.ScheduledResultsGenerator;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

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
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        int index = 1;
        for (Map.Entry<User, Double> entry : resultsGenerator.getTop5UsersInGroup(chatId)) {
            User user = entry.getKey();

            // Создаем кнопку для каждого пользователя
            InlineKeyboardButton button = new InlineKeyboardButton();
//            button.setText("Посмотреть "+ index + "# " + user.getUsername());
            button.setText(index + "#");
            String jsonCallback = JsonHandler.toJson(List.of(CallbackType.VIEW_USER_RESULTS_FROM_TOP, user.getId()));
            button.setCallbackData(jsonCallback); // Уникальный идентификатор для коллбэка

            // Добавляем кнопку в ряд
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            if (index > 50){
                rowInline3.add(button);
            } else if (index > 69) {
                rowInline2.add(button);
            } else {
                rowInline1.add(button);
            }
            index++;
        }
        keyboard.add(rowInline1);
        keyboard.add(rowInline2);
        keyboard.add(rowInline3);
        var markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(markupInline);
    }
}
