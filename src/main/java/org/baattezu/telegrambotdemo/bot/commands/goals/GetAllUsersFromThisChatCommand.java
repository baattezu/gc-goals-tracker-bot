package org.baattezu.telegrambotdemo.bot.commands.goals;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.bot.commands.Command;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.UserService;
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
        var chatId = update.getMessage().getChatId();
        var chat = chatService.findById(chatId);
        var userList = userService.getAllUsersFromGroupChat(chat);

        var message = new SendMessage();
        message.setChatId(chatId);
        int index = 1;
        StringBuilder usersText = new StringBuilder("Список прикрепленных пользователей в этой группе: \n");
        for (User user : userList){
            usersText.append(index).append(". ").append(user.getUsername()).append(" \n");
            index++;
        }

        message.setText(usersText.toString());
        return message;
    }
}
