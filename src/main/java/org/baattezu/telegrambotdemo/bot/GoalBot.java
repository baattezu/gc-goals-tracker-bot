package org.baattezu.telegrambotdemo.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbacksHandler;
import org.baattezu.telegrambotdemo.bot.commands.CommandsHandler;
import org.baattezu.telegrambotdemo.bot.input.UserInputHandlerWithoutDate;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.data.UserGoalData;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.yaml.snakeyaml.error.Mark;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.bot.name}")
    private String botUsername;
    @Value("${telegram.bot.url}")
    private String botUrl;


    private final GoalService goalService;
    private final ChatService chatService;
    private final CommandsHandler commandsHandler;
    private final CallbacksHandler callbacksHandler;
    private final UserInputHandlerWithoutDate inputHandler;
    @Override
    public void onUpdateReceived(Update update) {
        // Проверка на наличие сообщения
        try {
            if (update.hasMessage()) {
                // Проверка на пересланное сообщение
                if (update.getMessage().getForwardFrom() != null) {
                    log.info("взяли пересланное сообщение");
                    handleForwardedMessage(update);
                }
                // Проверка на текстовое сообщение
                else if (update.getMessage().hasText()) {
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
        } catch (Exception e){
            try {
                log.error(e.getMessage());
                handleException(update);
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    private void handleException(Update update) throws TelegramApiException {
        var exceptionMessage = new SendMessage();
        exceptionMessage.setChatId(update.getMessage().getChatId());
        exceptionMessage.setText("Упс, что то пошло не так(");

        exceptionMessage.setReplyMarkup(Markup.keyboard().addRow(
                Markup.Button.create("Ok", CallbackType.DELETE_LAST_MESSAGES, "nothing")
        ).build());
        execute(exceptionMessage);
    }
    private void sendMessage(Object object) {
        try {
            if (object instanceof BotApiMethod botApiMethod){
                execute(botApiMethod);
            } else if (object instanceof List list){ // in case if there is more than one api method
                for (BotApiMethod botApiMethod : (List<BotApiMethod>) list){
                    execute(botApiMethod);
                }
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    // Метод для обработки новых участников в группе
    private void handleAddingToGroupChat(Update update){
        var chatId = update.getMessage().getChatId();
        var chatName = update.getMessage().getChat().getTitle();

        var message = new SendMessage();
        message.setChatId(chatId);

        message.setReplyMarkup(Markup.keyboard().addRow(
                Markup.Button.create("Перейти в личку", botUrl, CallbackType.GO_TO_PRIVATE_CHAT, "someDate"),
                Markup.Button.create("Прикрепиться", CallbackType.PIN_TO_CHAT, botUrl)
        ).build());

        message.setText(BotMessagesEnum.WELCOME_GC_MESSAGE.getMessage());

        chatService.createGroupChat(chatId, chatName);
        sendMessage(message);
    }
    private void handleForwardedMessage(Update update){
        var chatId = update.getMessage().getChatId();
        var userId = update.getMessage().getFrom().getId();
        var goalName = update.getMessage().getText();

        var goal = goalService.createBlankGoal(userId);
        goalService.setGoal(goal, goalName);
        goalService.setUserState(userId, UserState.WAITING_FOR_REWARD, goal.getId());

        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(BotMessagesEnum.SET_GOAL_VIA_FORWARDED_MESSAGE.getMessage());
        message.setReplyMarkup(new ForceReplyKeyboard());
        sendMessage(TelegramBotHelper.deletePreviousUserMessage(update));
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
            sendMessage(commandsHandler.handleCommand(update));
            sendMessage(TelegramBotHelper.deletePreviousUserMessage(update));
        } else if (
                currentState != null
        ) {
            // Если это команда, обрабатываем команду
            Object responseMessage = inputHandler.handleMessage(update);
            sendMessage(responseMessage);
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
