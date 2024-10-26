package org.baattezu.telegrambotdemo.bot.commands.goals;

import org.baattezu.telegrambotdemo.model.GroupChat;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class GetAllUsersFromThisChatCommandTest {

    @Mock
    private UserService userService;
    @Mock
    private ChatService chatService;
    @InjectMocks
    private GetAllUsersFromThisChatCommand getAllUsersFromThisChatCommand;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void execute_ShouldReturnSendMessageWithUserList_WhenGroupMessage() {
        // Arrange
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setTitle("normisy");
        chat.setId(12345L);
        chat.setType("group");
        message.setChat(chat); // Указываем, что сообщение групповое
        update.setMessage(message);

        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        List<User> userList = List.of(user1, user2);

        GroupChat groupChat = new GroupChat();
        groupChat.setId(12345L);
        groupChat.setName("normisy");
        groupChat.setUsers(new HashSet<>(userList));

        when(chatService.findById(12345L)).thenReturn(groupChat);
        when(userService.getAllUsersFromGroupChat(groupChat)).thenReturn(userList);

        // Act
        SendMessage result = getAllUsersFromThisChatCommand.execute(update);

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getChatId());
        assertTrue(result.getText().contains("Список прикрепленных пользователей в этой группе:"));
        assertTrue(result.getText().contains("1. user1"));
        assertTrue(result.getText().contains("2. user2"));
        verify(chatService).findById(12345L);
        verify(userService).getAllUsersFromGroupChat(groupChat);
    }

    @Test
    void execute_ShouldReturnNull_WhenNotGroupMessage() {
        // Arrange
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setType("private");
        chat.setId(12345L);
        message.setChat(chat);
        update.setMessage(message);

        // Act
        SendMessage result = getAllUsersFromThisChatCommand.execute(update);

        // Assert
        assertNull(result);
    }
}