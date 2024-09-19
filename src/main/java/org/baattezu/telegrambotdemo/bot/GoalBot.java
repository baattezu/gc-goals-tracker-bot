package org.baattezu.telegrambotdemo.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbacksHandler;
import org.baattezu.telegrambotdemo.bot.commands.CommandsHandler;
import org.baattezu.telegrambotdemo.bot.input.UserInputHandlerWithoutDate;
import org.baattezu.telegrambotdemo.data.UserGoalData;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        var excmessage = new SendMessage();
        excmessage.setChatId(update.getMessage().getChatId());
        excmessage.setText("Упс, что то пошло не так(");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        TelegramBotHelper.deleteLastMessages(excmessage, update.getMessage().getMessageId(), keyboard);

        var markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);
        excmessage.setReplyMarkup(markupInline);
        execute(excmessage);
    }

    private void sendMessage(Object object) {
        try {
            if (object instanceof SendMessage sendMessage){
                execute(sendMessage);
            } else if (object instanceof EditMessageText editMessageText){
                execute(editMessageText);
            }
            else if (object instanceof DeleteMessage deleteMessage){
                execute(deleteMessage);
            }
            else if (object instanceof List list){
                for (DeleteMessage deleteMessage : (List<DeleteMessage>) list){
                    execute(deleteMessage);
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
        message.setChatId(String.valueOf(chatId));

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        TelegramBotHelper.addButtonsForGCWelcomeMessage(keyboard, botUrl);

        var markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);
        message.setReplyMarkup(markupInline);

        message.setText(BotMessagesEnum.WELCOME_GC_MESSAGE.getMessage());

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
            sendMessage(commandsHandler.handleCommand(update));
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
