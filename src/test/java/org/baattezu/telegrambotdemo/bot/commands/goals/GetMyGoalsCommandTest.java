package org.baattezu.telegrambotdemo.bot.commands.goals;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.List;

class GetMyGoalsCommandTest {

    @Mock
    private UserService userService;

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GetMyGoalsCommand getMyGoalsCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_ShouldReturnGoalsMessage_WhenGroupMessage() {
        // Arrange
        Update update = createUpdate(123L, 456L, true);  // Групповое сообщение
        User user = new User();
        user.setId(123L);
        user.setUsername("test_user");
        when(userService.findById(123L)).thenReturn(user);
        when(goalService.getAllGoals(123L)).thenReturn(List.of(
                createGoal("Goal 1", false), createGoal("Goal 2", true)
        ));

        // Act
        SendMessage result = getMyGoalsCommand.execute(update);

        // Assert
        assertNotNull(result);
        assertEquals("456", result.getChatId());
        assertTrue(result.getText().contains("test_user"));
        assertTrue(result.getText().contains("Goal 1"));
        assertTrue(result.getText().contains("Goal 2"));
    }

    @Test
    void execute_ShouldReturnGoalsMessage_WhenPrivateMessage() {
        // Arrange
        Update update = createUpdate(123L, 456L, false);  // Личное сообщение
        User user = new User();
        user.setId(123L);
        user.setUsername("test_user");
        when(userService.findById(123L)).thenReturn(user);
        when(goalService.getAllGoals(123L)).thenReturn(List.of(
                createGoal("Goal 1", false), createGoal("Goal 2", true)
        ));

        // Act
        SendMessage result = getMyGoalsCommand.execute(update);

        // Assert
        assertNotNull(result);
        assertEquals("456", result.getChatId());
        assertTrue(result.getText().contains("Goal 1"));
        assertTrue(result.getText().contains("Goal 2"));
    }

    private Update createUpdate(Long userId, Long chatId, boolean isGroupMessage) {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(chatId);
        chat.setType(isGroupMessage ? "group" : "private");
        message.setChat(chat);
        var from = new org.telegram.telegrambots.meta.api.objects.User();
        from.setId(userId);
        message.setFrom(from);
        update.setMessage(message);
        return update;
    }

    private Goal createGoal(String goalText, boolean isCompleted) {
        Goal goal = new Goal();
        goal.setGoal(goalText);
        goal.setCompleted(isCompleted);
        return goal;
    }
}
