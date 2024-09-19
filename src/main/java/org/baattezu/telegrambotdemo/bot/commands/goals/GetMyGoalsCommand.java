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
        var userId = update.getMessage().getFrom().getId();
        var chatId = update.getMessage().getChatId();

        var message = new SendMessage();
        message.setChatId(chatId);
        
        var user = userService.findById(userId);
        if (user == null) {
            return TelegramBotHelper.messageWithDeleteOption(
                    BotMessagesEnum.REGISTER_BEFORE_GET_GOAL_MESSAGE.getMessage(), update, true
            );
        }
        var myGoals = goalService.getAllGoals(userId, false);
        StringBuilder response = new StringBuilder("✨ *Мои цели на эту неделю:* \n \n");
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        TelegramBotHelper.setAllGoalsResponseAndKeyboard(response, myGoals, keyboard);

        var markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        message.setText(response.toString());
        message.setParseMode("Markdown");
        message.setReplyMarkup(markup);  // Устанавливаем разметку с кнопками
        return message;
    }
}
