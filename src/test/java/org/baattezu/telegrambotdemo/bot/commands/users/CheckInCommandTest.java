package org.baattezu.telegrambotdemo.bot.commands.users;

import static org.junit.jupiter.api.Assertions.*;

import org.baattezu.telegrambotdemo.model.CheckIn;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.CheckInService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CheckInCommandTest {

    @Mock
    private UserService userService;

    @Mock
    private CheckInService checkInService;

    @InjectMocks
    private CheckInCommand checkInCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_UserNotRegistered() {
        // Мокируем Update и Message
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(12345L);
        message.setChat(chat);
        message.setFrom(createUser(67890L));
        update.setMessage(message);

        // Мокируем, что пользователь не найден
        when(userService.findById(67890L)).thenReturn(null);

        // Выполняем команду
        SendMessage response = checkInCommand.execute(update);

        // Проверяем корректность возвращаемого сообщения для незарегистрированного пользователя
        assertEquals(String.valueOf(chat.getId()), response.getChatId());
        assertEquals(BotMessagesEnum.REGISTER_BEFORE_.getMessage("отмечаться."), response.getText());

        // Проверяем, что checkInService не вызывается
        verify(checkInService, never()).checkInUser(any());
    }

    @Test
    void testExecute_UserRegistered() {
        // Мокируем Update и Message
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(12345L);
        message.setChat(chat);
        message.setFrom(createUser(67890L));
        update.setMessage(message);

        // Мокируем, что пользователь найден
        User user = new User();
        user.setId(67890L);
        when(userService.findById(67890L)).thenReturn(user);

        // Мокируем CheckInService
        CheckIn checkIn = new CheckIn();
        checkIn.setCheckInTime(LocalDateTime.of(2024,12,12,12,0));
        when(checkInService.checkInUser(user)).thenReturn(checkIn);

        // Выполняем команду
        SendMessage response = checkInCommand.execute(update);

        // Проверяем корректность возвращаемого сообщения для зарегистрированного пользователя
        assertEquals(String.valueOf(chat.getId()), response.getChatId());
        assertEquals(BotMessagesEnum.CHECKIN_MESSAGE.getMessage(LocalDateTime.of(2024,12,12,12,0)), response.getText());

        // Проверяем, что checkInService был вызван
        verify(checkInService).checkInUser(user);
    }

    // Вспомогательный метод для создания User
    private org.telegram.telegrambots.meta.api.objects.User createUser(long id) {
        org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User();
        user.setId(id);
        return user;
    }
}
