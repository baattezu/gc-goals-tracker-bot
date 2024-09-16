package org.baattezu.telegrambotdemo.bot.commands.pinning;


import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
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
        var userId = update.getMessage().getFrom().getId();
        var chatId = update.getMessage().getChatId();

        var user = userService.findById(userId);
        var chat = chatService.findById(chatId);

        var message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

//        if (user.getGroupChat() != null){
//            String alreadyPinnedText = user.getGroupChat().getId().equals(chatId) ?
//                    BotMessagesEnum.ALREADY_PINNED_THIS_GC_MESSAGE.getMessage(user.getUsername()) :
//                    BotMessagesEnum.ALREADY_PINNED_SOMEWHERE_MESSAGE.getMessage(user.getUsername());
//            return TelegramBotHelper.messageWithDeleteOption(alreadyPinnedText, update, true);
//        }

        userService.pinToChat(user, chat);
        return TelegramBotHelper.messageWithDeleteOption(
                BotMessagesEnum.PINNED_SUCCESS_MESSAGE.getMessage(user.getUsername()), update, true);
    }
}
