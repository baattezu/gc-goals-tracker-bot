package org.baattezu.telegrambotdemo.bot.commands.users;

import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.service.GoalService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class ChangeNameCommandTest {

    @Mock
    private GoalService goalService;

    @InjectMocks
    private ChangeNameCommand changeNameCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute() {
        // Мокируем Update и Message
        Update update = new Update();
        Message message = new Message();

        // Создаем Chat с chatId
        Chat chat = new Chat();
        chat.setId(12345L);
        message.setChat(chat); // Устанавливаем chat в message

        // Устанавливаем userId через From
        message.setFrom(createUser(67890L));
        update.setMessage(message);

        // Выполняем команду
        SendMessage response = (SendMessage) changeNameCommand.execute(update);

        // Проверяем правильность установки текста и чата
        assertEquals(BotMessagesEnum.WAITING_FOR_CHANGING_NAME.getMessage(), response.getText());
        assertEquals(String.valueOf(chat.getId()), response.getChatId());

        // Проверяем взаимодействие с сервисом
        verify(goalService).setUserState(67890L, UserState.WAITING_FOR_CHANGING_NAME, chat.getId());
    }

    // Вспомогательный метод для создания User
    private org.telegram.telegrambots.meta.api.objects.User createUser(long id) {
        org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User();
        user.setId(id);
        return user;
    }
}

