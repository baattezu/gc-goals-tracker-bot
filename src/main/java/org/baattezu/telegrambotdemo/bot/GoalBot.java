package org.baattezu.telegrambotdemo.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.input.UserInputHandler;
import org.baattezu.telegrambotdemo.bot.input.UserInputHandlerWithoutDate;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.data.UserGoalData;
import org.baattezu.telegrambotdemo.data.UserState;
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
    private final CommandsHandler commandsHandler;
    private final CallbacksHandler callbacksHandler;
    private final UserInputHandlerWithoutDate inputHandler;

    @Override
    public void onUpdateReceived(Update update) {
        // Проверка на добавление новых участников в группу
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update);
        }

        else if (update.getMessage() != null && update.getMessage().getNewChatMembers() != null) {
            handleNewChatMembers(update);
        }
        // Обработка коллбэков
        else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
        // Обработка текстовых сообщений
    }

    private void sendWelcomeMessage(Long chatId) {
        String welcomeMessage = "Привет! Я бот для ваших целей и задач. Чтобы зарегистрироваться, введи команду /register [Твое Имя].";
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(welcomeMessage);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
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
    private void handleNewChatMembers(Update update) {
        for (User newUser : update.getMessage().getNewChatMembers()) {
            // Если добавлен бот, отправляем приветственное сообщение
            if (newUser.getUserName().equals(botUsername)) {
                log.info("salam alaykum");
                sendWelcomeMessage(update.getMessage().getChatId());
            }
        }

        // Обработка команды, если это сообщение команда
        sendMessage(commandsHandler.handleCommand(update));
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
