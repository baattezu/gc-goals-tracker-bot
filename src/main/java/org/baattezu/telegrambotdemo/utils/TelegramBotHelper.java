package org.baattezu.telegrambotdemo.utils;

import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.model.Goal;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TelegramBotHelper {

    private static List<InlineKeyboardButton> addGoalCheckButton(int index, Goal goal, boolean pendingOrAll){
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText( "#"+ index + " " + (goal.getCompleted() ? "Отменить выполнение" : "Отметить выполненной"));
        CallbackType callbackType = pendingOrAll ? CallbackType.COMPLETE_GOAL : CallbackType.COMPLETE_IN_ALL_GOALS;
        String jsonCallback = JsonHandler.toJson(List.of(callbackType, goal.getId()));
        button.setCallbackData(jsonCallback); // Используем айди цели

        // Добавляем кнопку в разметку
        row.add(button);
        return row;
    }
    public static void setAllGoalsResponseAndKeyboard(StringBuilder response, List<Goal> myGoals, List<List<InlineKeyboardButton>> keyboard, boolean pendingOrAll){
        var index = 1;
        var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        for (var goal: myGoals) {
            var status = goal.getCompleted() ? "✅" : "";
            response.append(BotMessagesEnum.GET_GOAL_DETAIL_MESSAGE_VER1.getMessage(
                    index, status, goal.getGoalName(), goal.getReward()
            ));
//            response.append(BotMessagesEnum.GET_GOAL_DETAIL_MESSAGE_VER2.getMessage(
//                      goal.getGoalName(), goal.getReward(), status
//            ));
            keyboard.add(TelegramBotHelper.addGoalCheckButton(index, goal, pendingOrAll));
            index++;
        }
    }
    public static SendMessage justGoToPrivateMessage(String botMessageText, String botUrl ,Update update, boolean withDeleteOrNot) {
        var message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        message.setText(botMessageText);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        addButtonToPrivateMessageRow(botUrl, rowInline);
        if (withDeleteOrNot){
            deleteLastMessagesRow(message, update.getMessage().getMessageId(), rowInline);
        }
        keyboard.add(rowInline);
        var markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);
        message.setReplyMarkup(markupInline);
        message.enableMarkdown(true);
        return message;
    }
    public static SendMessage messageWithDeleteOption(String botMessageText, Update update, boolean deleteUserMessageOrNot){
        var message = new SendMessage();
        var messageId = update.getMessage().getMessageId();
        message.setChatId(update.getMessage().getChatId());
        message.setText(botMessageText);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        deleteLastMessages(message, messageId, keyboard);

        var markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);
        message.setReplyMarkup(markupInline);
        message.enableMarkdown(true);
        message.setReplyToMessageId(messageId);
        return message;
    }


    public static void deleteLastMessages(SendMessage message, Integer messageId, List<List<InlineKeyboardButton>> keyboard){

        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Хорошо, поняли");
        String jsonCallback = JsonHandler.toJson(List.of(CallbackType.DELETE_LAST_MESSAGES, messageId));
        button.setCallbackData(jsonCallback); // Используем айди цели
        rowInline.add(button);

        keyboard.add(rowInline);
    }

    public static void addButtonToPrivateMessageRow(String botUrl, List<InlineKeyboardButton> rowInline) {
        // Создание кнопки
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Перейти в личку");
        button.setUrl(botUrl);

        // Преобразование данных для callback
        String jsonCallback = JsonHandler.toJson(List.of(CallbackType.GO_TO_PRIVATE_CHAT, "someDate"));
        button.setCallbackData(jsonCallback);

        // Добавление кнопки в текущий ряд
        rowInline.add(button);
    }

    public static void deleteLastMessagesRow(SendMessage message, Integer messageId, List<InlineKeyboardButton> rowInline) {
        // Создание кнопки
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Хорошо, поняли");

        // Преобразование данных для callback
        String jsonCallback = JsonHandler.toJson(List.of(CallbackType.DELETE_LAST_MESSAGES, messageId));
        button.setCallbackData(jsonCallback);

        // Добавление кнопки в текущий ряд
        rowInline.add(button);
    }

    public static void addButtonsForGCWelcomeMessage(
            List<List<InlineKeyboardButton>> keyboard,
            String botUrl
    ){
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        TelegramBotHelper.addButtonToPrivateMessageRow(botUrl, rowInline);

        var button = new InlineKeyboardButton();
        button.setText("Прикрепиться");
        String jsonCallback = JsonHandler.toJson(List.of(CallbackType.PIN_TO_CHAT, botUrl));
        button.setCallbackData(jsonCallback); // Используем айди цели
        rowInline.add(button);

        keyboard.add(rowInline);
    }

    public static void addResultsButtons(Object message, Long userId){
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        var button = new InlineKeyboardButton();
        button.setText("Переписать результаты за неделю");
        String jsonCallback = JsonHandler.toJson(List.of(CallbackType.PUT_RESULTS, userId));
        button.setCallbackData(jsonCallback); // Используем айди цели
        rowInline.add(button);

        keyboard.add(rowInline);

        var markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);
        if (message instanceof SendMessage sendMessage){
            sendMessage.setReplyMarkup(markupInline);
        } else if (message instanceof EditMessageText editMessageText){
            editMessageText.setReplyMarkup(markupInline);
        }
    }


}
