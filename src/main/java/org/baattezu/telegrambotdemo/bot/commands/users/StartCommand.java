package org.baattezu.telegrambotdemo.bot.commands.users;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static org.baattezu.telegrambotdemo.utils.TelegramBotHelper.setMainMenuKeyboard;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {

    private final UserService userService;
    @Override
    public Object execute(Update update) {
        var chatId = update.getMessage().getChatId();
        var userId = update.getMessage().getFrom().getId();

        if (update.getMessage().isGroupMessage()){
            return null;
        }
        if (userService.isRegistered(userId)){
            return null;
        }
        var firstName = update.getMessage().getFrom().getFirstName();
        var lastName = update.getMessage().getFrom().getLastName();
        String username = firstName + (lastName != null ? " " + lastName : "");

        userService.registerUser(userId, username, chatId);

        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(BotMessagesEnum.REGISTER_SUCCESS_MESSAGE.getMessage(username));
        setMainMenuKeyboard(message);
        var infoMessage = new SendMessage();
        infoMessage.setChatId(update.getMessage().getChatId());
        infoMessage.setText(BotMessagesEnum.START_WORK_WITH_BOT.getMessage());

        List<BotApiMethod> botApiMethodList = new ArrayList<>();
        botApiMethodList.add(message);
        botApiMethodList.add(infoMessage);
        return botApiMethodList;
    }


}
