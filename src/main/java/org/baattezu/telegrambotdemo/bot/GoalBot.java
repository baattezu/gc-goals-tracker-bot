package org.baattezu.telegrambotdemo.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.bot.callbacks.CallbacksHandler;
import org.baattezu.telegrambotdemo.bot.commands.CommandsHandler;
import org.baattezu.telegrambotdemo.bot.input.UserInputHandlerWithoutDate;
import org.baattezu.telegrambotdemo.config.BotConfig;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.data.UserGoalData;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static org.baattezu.telegrambotdemo.config.BotConfig.BOT_URL;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalBot extends TelegramLongPollingBot {

    private final GoalService goalService;
    private final ChatService chatService;
    private final CommandsHandler commandsHandler;
    private final CallbacksHandler callbacksHandler;
    private final UserInputHandlerWithoutDate inputHandler;



    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                handleMessage(update);
            } else if (update.hasCallbackQuery()) {
                handleCallbackQuery(update);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            handleException(update);
        }
    }

    private void handleException(Update update) {
        try {
            var exceptionMessage = new SendMessage();
            exceptionMessage.setChatId(update.getMessage().getChatId());
            exceptionMessage.setText("Упс, что то пошло не так(");
            exceptionMessage.setReplyMarkup(Markup.keyboard().addRow(
                    Markup.Button.create("Ok", CallbackType.DELETE_LAST_MESSAGES, "nothing")
            ).build());
            execute(exceptionMessage);
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void sendMessage(Object object) {
        try {
            if (object instanceof BotApiMethod botApiMethod) {
                execute(botApiMethod);
            } else if (object instanceof List list) { // in case if there is more than one api method
                for (BotApiMethod botApiMethod : (List<BotApiMethod>) list) {
                    execute(botApiMethod);
                }
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void handleAddingToGroupChat(Update update) {
        var chatId = update.getMessage().getChatId();
        var chatName = update.getMessage().getChat().getTitle();

        var message = new SendMessage();
        message.setChatId(chatId);

        message.setReplyMarkup(Markup.keyboard().addRow(
                Markup.Button.create("Перейти в личку", BOT_URL, CallbackType.GO_TO_PRIVATE_CHAT, "someDate"),
                Markup.Button.create("Прикрепиться", CallbackType.PIN_TO_CHAT, BOT_URL)
        ).build());

        message.setText(BotMessagesEnum.WELCOME_GC_MESSAGE.getMessage());

        chatService.createGroupChat(chatId, chatName);
        sendMessage(message);
    }

    private void handleForwardedMessage(Update update) {
        Message message = update.getMessage();
        var chatId = message.getChatId();
        var userId = message.getFrom().getId();

        var goal = goalService.createBlankGoal(userId);
        goalService.setGoal(goal, message.getText());
        goalService.setUserState(userId, UserState.WAITING_FOR_REWARD, goal.getId());

        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(BotMessagesEnum.SET_GOAL_VIA_FORWARDED_MESSAGE.getMessage());
        sendMessage.setReplyMarkup(new ForceReplyKeyboard());

        sendMessage(TelegramBotHelper.deleteMessage(chatId, message.getMessageId()));
        sendMessage(sendMessage);
    }

    private void handleCallbackQuery(Update update) {
        sendMessage(callbacksHandler.handleCallbacks(update));
    }

    private void handleMessage(Update update) {
        var message = update.getMessage();

        var userId = message.getFrom().getId();
        UserGoalData data = goalService.getUserState(userId);
        UserState currentState = data != null ? data.userState() : null;

        if (message.getText() != null && message.getText().startsWith("/")) {
            sendMessage(commandsHandler.handleCommand(update));
            sendMessage(TelegramBotHelper.deleteMessage(message.getChatId(), message.getMessageId()));
        } else if (currentState != null) {
            sendMessage(inputHandler.handleMessage(update));
        } else if (message.getChat().isGroupChat() && !chatService.isChatExists(message.getChatId())) {
            handleAddingToGroupChat(update);
        } else if (message.getForwardFrom() != null && !message.isGroupMessage()) {
            handleForwardedMessage(update);
        }
    }


    @Override
    public String getBotUsername() {
        return BotConfig.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

}
