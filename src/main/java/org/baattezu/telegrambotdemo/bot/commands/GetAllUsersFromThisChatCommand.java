package org.baattezu.telegrambotdemo.bot.commands;

import lombok.RequiredArgsConstructor;
import org.baattezu.telegrambotdemo.model.Chat;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class GetAllUsersFromThisChatCommand implements Command{
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
        String usersText = "Список пользователей в этой группе: \n";
        for (User user : userList){
            usersText = usersText + index + ". " + user.getUsername() + " \n";
        }
        usersText = usersText + "\n _________________ \n";
        var allChats = chatService.getAllChats();
        for (Chat c : allChats){
            usersText = usersText + c.getName() + ": \n";
            var userList2 = userService.getAllUsersFromGroupChat(c);
            for (User user : userList2){
                usersText = usersText + index + ". " + user.getUsername() + " \n";
            }
        }

        message.setText(usersText);
        return message;
    }
}
