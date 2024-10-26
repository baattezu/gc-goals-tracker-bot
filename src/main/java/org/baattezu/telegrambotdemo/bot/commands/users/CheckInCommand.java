package org.baattezu.telegrambotdemo.bot.commands.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.model.CheckIn;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.CheckInService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
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
public class CheckInCommand implements Command {

    private final UserService userService;
    private final CheckInService checkInService;

    @Override
    public SendMessage execute(Update update) {
        long chatId = update.getMessage().getChatId();

        User user = userService.findById(update.getMessage().getFrom().getId());
        if (user == null) {
            return TelegramBotHelper.registerBeforeMessage(chatId,"отмечаться.");
        }
        CheckIn checkIn = checkInService.checkInUser(user);

        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(BotMessagesEnum.CHECKIN_MESSAGE.getMessage(checkIn.getCheckInTime()));
        message.setReplyMarkup(TelegramBotHelper.okButton());
        return message;
    }
}