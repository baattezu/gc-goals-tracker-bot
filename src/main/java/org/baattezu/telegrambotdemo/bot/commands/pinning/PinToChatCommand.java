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
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PinToChatCommand implements Command {

    private final UserService userService;
    private final ChatService chatService;

    @Override
    public SendMessage execute(Update update) {
        var chatId = update.getMessage().getChatId();
        var user = userService.findById(update.getMessage().getFrom().getId());
        if (user == null){
            return null;
        }
        var chat = chatService.findById(chatId);
        userService.pinToChat(user, chat);
        var message = new SendMessage(String.valueOf(chatId), BotMessagesEnum.PINNED_SUCCESS_MESSAGE.getMessage(user.getUsername()));
        message.setReplyMarkup(Markup.keyboard().addRow(
                Markup.Button.create("Ok", CallbackType.DELETE_LAST_MESSAGES, "nothing")
        ).build());
        return message;
    }
}
