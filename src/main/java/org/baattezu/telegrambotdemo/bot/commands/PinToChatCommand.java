package org.baattezu.telegrambotdemo.bot.commands;


import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PinToChatCommand implements Command{

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

        if (user.getChat() != null){
            String alreadyPinnedText = user.getChat().getId().equals(chatId) ?
                    user.getUsername() + " уже закреплен в этом чате. \uD83D\uDE2E\u200D\uD83D\uDCA8" :
                    user.getUsername() + " уже закреплен в каком то чате. \uD83E\uDEE0";
            message.setText(alreadyPinnedText);
            return message;
        }
        userService.pinToChat(user, chat);
        message.setText(user.getUsername() + " успешно закреплен в этом групповом чате! \uD83C\uDF89\uD83D\uDC65");
        return message;
    }
}
