package org.baattezu.telegrambotdemo.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.input.UserInputHandler;
import org.baattezu.telegrambotdemo.bot.input.UserInputHandlerWithoutDate;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.data.UserGoalData;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.utils.JsonHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.bot.name}")
    private String botUsername;

    private final GoalService goalService;
    private final ChatService chatService;
    private final CommandsHandler commandsHandler;
    private final CallbacksHandler callbacksHandler;
    private final UserInputHandlerWithoutDate inputHandler;
    @Override
    public void onUpdateReceived(Update update) {
        // Проверка на наличие сообщения
        if (update.hasMessage()) {
            // Проверка на текстовое сообщение
            if (update.getMessage().hasText()) {
                handleMessage(update);
            }
            // Проверка на групповое сообщение и добавление чата в базу
            else if (update.getMessage().getChat().isGroupChat() &&
                    !chatService.isChatExists(update.getMessage().getChat().getId())) {
                handleAddingToGroupChat(update);
            }
        }
        // Обработка коллбэков
        else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
    }


    private void sendMessage(Object object) {
        try {
            if (object instanceof SendMessage sendMessage){
                execute(sendMessage);
            } else if (object instanceof EditMessageText editMessageText){
                execute(editMessageText);
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    // Метод для обработки новых участников в группе
    private void handleAddingToGroupChat(Update update){
        var chatId = update.getMessage().getChatId();
        var chatName = update.getMessage().getChat().getTitle();

        String welcomeMessage =
                "Привет! Я бот для ваших целей и задач. " +
                        "Чтобы зарегистрироваться, введи команду /рег [Твое Имя].";
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(welcomeMessage);

        chatService.createGroupChat(chatId, chatName);
        sendMessage(message);
    }

    // Метод для обработки коллбэков
    private void handleCallbackQuery(Update update) {
        // Обрабатываем коллбэк и отправляем ответ
        sendMessage(callbacksHandler.handleCallbacks(update));
    }

    // Метод для обработки текстовых сообщений
    private void handleMessage(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        UserGoalData data = goalService.getUserState(userId);

        UserState currentState = null;
        if (data != null){
            currentState = data.userState();
        }
        // Если пользователь находится в процессе создания цели
        if (update.getMessage().getText().startsWith("/")) {
            // -s
            sendMessage(commandsHandler.handleCommand(update));
        } else if (
                currentState != null ||
                currentState == UserState.WAITING_FOR_TITLE ||
                currentState == UserState.WAITING_FOR_DESCRIPTION ||
                currentState == UserState.WAITING_FOR_DEADLINE
        ) {
            // Если это команда, обрабатываем команду
            SendMessage responseMessage = inputHandler.handleMessage(update);
            sendMessage(responseMessage);
        } else {
            // Отправляем сообщение, если команда не распознана
            sendMessage(new SendMessage(update.getMessage().getChatId().toString(), "Неизвестная команда."));
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
