package org.baattezu.telegrambotdemo.bot.commands.pinning;


import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PinToChatCommand implements Command {

    private final UserService userService;
    private final ChatService chatService;

    @Override
    public SendMessage execute(Update update) {
        Message message = update.getMessage();
        var chatId = message.getChatId();
        if (!message.isGroupMessage()){
            return null;
        }

        var user = userService.findById(message.getFrom().getId());
        if (user == null){
            return null;
        }

        var chat = chatService.findById(chatId);
        userService.pinToChat(user, chat);
        var sendMessage = new SendMessage(String.valueOf(chatId), BotMessagesEnum.PINNED_SUCCESS_MESSAGE.getMessage(user.getUsername()));
        sendMessage.setReplyMarkup(TelegramBotHelper.okButton());
        return sendMessage;
    }
}
