package org.baattezu.telegrambotdemo.bot.input;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.data.UserGoalData;
import org.baattezu.telegrambotdemo.data.UserState;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.baattezu.telegrambotdemo.utils.BotMessagesEnum;
import org.baattezu.telegrambotdemo.utils.TelegramBotHelper;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.yaml.snakeyaml.error.Mark;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInputHandlerWithoutDate {


    private final UserService userService;
    private final GoalService goalService;

    public Object handleMessage(Update update) {
        var message = update.getMessage();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        int replyMessageId = getReplyMessageId(message);
        String messageText = message.getText();

        UserGoalData data = goalService.getUserState(userId);
        UserState state = data.userState();

        List<BotApiMethod> botApiMethodList = new ArrayList<>();
        botApiMethodList.add(TelegramBotHelper.deleteMessage(chatId, message.getMessageId()));
        if (message.getReplyToMessage() != null){
            botApiMethodList.add(deleteMessageFromReply(chatId, replyMessageId));
        }

        long goalId = data.goalId();
        Goal goal = goalService.getGoalById(goalId);

        switch (state) {
            case WAITING_FOR_TITLE:
                handleTitleState(goal, userId, chatId, replyMessageId, messageText, botApiMethodList);
                break;
            case WAITING_FOR_REWARD:
                handleRewardState(goal, userId, chatId, replyMessageId, messageText, botApiMethodList);
                break;
            case WAITING_FOR_RESULTS:
                handleResultsState(userId, chatId, messageText, data, botApiMethodList);
                break;
            case WAITING_FOR_CHANGING_NAME:
                handleChangingNameState(userId, chatId, replyMessageId, messageText, botApiMethodList);
                break;
            default:
                botApiMethodList.add(createSimpleMessage(chatId, "Неизвестное состояние. Пожалуйста, начните сначала."));
                break;
        }
        return botApiMethodList;
    }

    private int getReplyMessageId(Message message) {
        return message.getReplyToMessage() != null ? message.getReplyToMessage().getMessageId() : 0;
    }

    private void handleChangingNameState(long userId, long chatId, int replyMessageId, String messageText, List<BotApiMethod> botApiMethodList) {
        userService.changeName(userId, messageText);
        goalService.clearUserState(userId);
        var changeNameMadeMessage = new SendMessage();
        changeNameMadeMessage.setChatId(chatId);
        changeNameMadeMessage.setText(BotMessagesEnum.CHANGE_NAME_SUCCESS_MESSAGE.getMessage(messageText));
        changeNameMadeMessage.setReplyMarkup(TelegramBotHelper.okButton());
        botApiMethodList.add(changeNameMadeMessage);
    }

    private void handleTitleState(Goal goal, long userId, long chatId, int replyMessageId, String messageText, List<BotApiMethod> botApiMethodList) {
        goalService.setGoal(goal, messageText);
        goalService.clearUserState(userId);
        goalService.setUserState(userId, UserState.WAITING_FOR_REWARD, goal.getId());
//        botApiMethodList.add(deleteMessageFromReply(chatId, replyMessageId));
        botApiMethodList.add(createSimpleMessage(chatId, BotMessagesEnum.WAITING_FOR_REWARD.getMessage(), true));
    }

    private void handleRewardState(Goal goal, long userId, long chatId, int replyMessageId, String messageText, List<BotApiMethod> botApiMethodList) {
        goalService.setGoalReward(goal, messageText);
        goalService.setGoalDeadline(goal, getEndOfWeek());
        goalService.clearUserState(userId);

        var goalMadeMessage = new SendMessage();
        goalMadeMessage.setChatId(chatId);
        goalMadeMessage.setText(BotMessagesEnum.SETTING_GOAL_DONE.getMessage(
                BotMessagesEnum.GET_GOAL_DETAIL_MESSAGE_VER1.getMessage(0, "", goal.getGoal(), goal.getReward())
        ));
        goalMadeMessage.enableHtml(true);
        goalMadeMessage.setReplyMarkup(TelegramBotHelper.okButton());
        TelegramBotHelper.setMainMenuKeyboard(goalMadeMessage);
//        botApiMethodList.add(deleteMessageFromReply(chatId, replyMessageId));
        botApiMethodList.add(goalMadeMessage);
    }

    private void handleResultsState(long userId, long chatId, String messageText, UserGoalData data, List<BotApiMethod> botApiMethodList) {
        userService.writeResultsForWeek(userId, messageText);
        goalService.clearUserState(userId);

        var editMessage = new EditMessageText();
        editMessage.setText(BotMessagesEnum.MY_RESULTS.getMessage(messageText));
        editMessage.setMessageId(Math.toIntExact(data.goalId()));
        editMessage.setChatId(chatId);
        editMessage.setReplyMarkup(createResultsMarkup(userId));
        botApiMethodList.add(editMessage);
    }

    private LocalDateTime getEndOfWeek() {
        return LocalDateTime.now().with(ChronoField.DAY_OF_WEEK, 7).withHour(23).withMinute(59);
    }

    private SendMessage createSimpleMessage(long chatId, String text) {
        return new SendMessage(String.valueOf(chatId), text);
    }

    private SendMessage createSimpleMessage(long chatId, String text, boolean forceReply) {
        var message = createSimpleMessage(chatId, text);
        if (forceReply) {
            message.setReplyMarkup(new ForceReplyKeyboard());
        }
        return message;
    }

    private InlineKeyboardMarkup createResultsMarkup(long userId) {
        return Markup.keyboard().addRow(
                        Markup.Button.create("Переписать результаты", CallbackType.PUT_RESULTS, String.valueOf(userId)),
                        Markup.Button.create("Ok", CallbackType.DELETE_LAST_MESSAGES, "nothing")
                ).build();
    }

    private BotApiMethod deleteMessageFromReply(long chatId, int replyMessageId) {
        return TelegramBotHelper.deleteMessage(chatId, replyMessageId);
    }




}
