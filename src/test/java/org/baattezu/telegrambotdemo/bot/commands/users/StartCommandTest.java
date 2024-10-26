package org.baattezu.telegrambotdemo.bot.commands.users;

import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StartCommandTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private StartCommand startCommand;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_NewUser() {
        // Мокируем Update и Message
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(12345L);
        chat.setType("private");
        message.setChat(chat);

        org.telegram.telegrambots.meta.api.objects.User from = new org.telegram.telegrambots.meta.api.objects.User();
        from.setId(67890L);
        from.setFirstName("John");
        from.setLastName("Doe");
        message.setFrom(from);
        update.setMessage(message);

        // Мокируем поведение userService
        when(userService.isRegistered(67890L)).thenReturn(false);

        // Выполняем команду
        List<BotApiMethod> response = (List<BotApiMethod>) startCommand.execute(update);

        // Проверяем, что в ответе два сообщения
        assertEquals(2, response.size());

        // Проверяем, что первое сообщение - это регистрационное сообщение
        SendMessage registerMessage = (SendMessage) response.get(0);
        String username = from.getFirstName() + (from.getLastName() != null ? " " + from.getLastName() : "");
        assertEquals(String.valueOf(chat.getId()), registerMessage.getChatId());
        assertEquals(BotMessagesEnum.REGISTER_SUCCESS_MESSAGE.getMessage(username), registerMessage.getText());

        // Проверяем, что второе сообщение - это сообщение о начале работы с ботом
        SendMessage startMessage = (SendMessage) response.get(1);
        assertEquals(String.valueOf(chat.getId()), startMessage.getChatId());
        assertEquals(BotMessagesEnum.START_WORK_WITH_BOT.getMessage(), startMessage.getText());

        // Проверяем, что userService вызван для регистрации нового пользователя
        verify(userService).registerUser(67890L, username, chat.getId());
    }

    @Test
    void testExecute_AlreadyRegisteredUser() {
        // Мокируем Update и Message
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(12345L);
        chat.setType("private");
        message.setChat(chat);

        org.telegram.telegrambots.meta.api.objects.User from = new org.telegram.telegrambots.meta.api.objects.User();
        from.setId(67890L);
        message.setFrom(from);
        update.setMessage(message);

        // Мокируем поведение userService для уже зарегистрированного пользователя
        when(userService.isRegistered(67890L)).thenReturn(true);

        // Выполняем команду
        Object response = startCommand.execute(update);

        // Проверяем, что результат null, так как пользователь уже зарегистрирован
        assertEquals(null, response);

        // Убедимся, что метод регистрации пользователя не был вызван
        verify(userService, never()).registerUser(anyLong(), anyString(), anyLong());
    }

    @Test
    void testExecute_GroupMessage() {
        // Мокируем Update и Message
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(12345L);
        chat.setType("group");
        message.setChat(chat);

        org.telegram.telegrambots.meta.api.objects.User from = new org.telegram.telegrambots.meta.api.objects.User();
        from.setId(67890L);
        message.setFrom(from);
        update.setMessage(message);

        // Выполняем команду
        Object response = startCommand.execute(update);

        // Проверяем, что результат null, так как это групповое сообщение
        assertEquals(null, response);

        // Убедимся, что метод регистрации пользователя не был вызван
        verify(userService, never()).registerUser(anyLong(), anyString(), anyLong());
    }
}
