package org.baattezu.telegrambotdemo.bot.commands.goals;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class GetAllUsersFromThisChatCommand implements Command {
    private final UserService userService;
    private final ChatService chatService;

    @Override
    public SendMessage execute(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId();
        if (!message.isGroupMessage()){
            return null;
        }
        var chat = chatService.findById(chatId);
        var userList = userService.getAllUsersFromGroupChat(chat);

        int index = 1;
        var usersText = new StringBuilder("Список прикрепленных пользователей в этой группе: \n");
        for (User user : userList){
            usersText.append(index).append(". ").append(user.getUsername()).append(" \n");
            index++;
        }

        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(usersText.toString());
        sendMessage.setReplyMarkup(TelegramBotHelper.okButton());
        return sendMessage;
    }
}
