package org.baattezu.telegrambotdemo.utils;

import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.yaml.snakeyaml.error.Mark;

import java.util.ArrayList;
import java.util.List;

public class TelegramBotHelper {

    public static InlineKeyboardMarkup setAllGoalsResponseAndKeyboard(
            StringBuilder response, List<Goal> myGoals,
            int currentPage, boolean isGroupChat) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        int goalsPerPage = 4;
        int totalPages = (int) Math.ceil((double) myGoals.size() / goalsPerPage);

        // Проверка текущей страницы
        if (currentPage > totalPages) {
            currentPage = totalPages;
        } else if (currentPage < 1) {
            currentPage = 1;
        }

        int startIndex = (currentPage - 1) * goalsPerPage;
        int endIndex = Math.min(startIndex + goalsPerPage, myGoals.size());

        int index = startIndex + 1; // Стартовый индекс для текущей страницы
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        response.append(String.format("Страница %d из %d\n\n", currentPage, totalPages));

        // Добавляем цели текущей страницы
        for (int i = startIndex; i < endIndex; i++) {
            Goal goal = myGoals.get(i);

            response.append("<blockquote expandable>");
            var status = goal.getCompleted() ? "✅" : "";
            response.append(BotMessagesEnum.GET_GOAL_DETAIL_MESSAGE_VER1.getMessage(
                    index, status, goal.getGoal(), goal.getReward()
            ));
            if (!isGroupChat) {
                var text = "#" + index + " " + (goal.getCompleted() ? "❌" : "⭕");
                currentRow.add(Markup.Button.create(text, CallbackType.COMPLETE_IN_ALL_GOALS,  goal.getId() + ":" + currentPage));
                // Добавляем текущий ряд в клавиатуру, если он заполнен или если это последний элемент
                if (currentRow.size() == 4 || i == endIndex - 1) {
                    keyboard.add(new ArrayList<>(currentRow));
                    currentRow.clear(); // очищаем для следующего ряда
                }
            }
            response.append("</blockquote>");
            index++;
        }

        // Добавляем кнопки навигации по страницам
        List<InlineKeyboardButton> paginationRow = new ArrayList<>();

        if (currentPage > 1) {
            InlineKeyboardButton prevPageButton = new InlineKeyboardButton();
            if (currentPage == totalPages) {
                prevPageButton.setText("⬅️ Предыдущая страница");
            } else {
                prevPageButton.setText("⬅️ Пред. страница");
            }
            prevPageButton.setCallbackData(JsonHandler.toJson(List.of(CallbackType.NEXT_PAGE, currentPage - 1)));
            paginationRow.add(prevPageButton);
        }

        if (currentPage < totalPages) {
            InlineKeyboardButton nextPageButton = new InlineKeyboardButton();
            if (currentPage == 1) {
                nextPageButton.setText("Следующая страница ➡️");
            } else {
                nextPageButton.setText("След. страница ➡️");
            }
            nextPageButton.setCallbackData(JsonHandler.toJson(List.of(CallbackType.NEXT_PAGE, currentPage + 1)));
            paginationRow.add(nextPageButton);
        }
        if (!paginationRow.isEmpty()) {
            keyboard.add(paginationRow);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    public static SendMessage justGoToPrivateMessage(Long chatId, String botMessageText, String botUrl) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(botMessageText);
        message.setReplyMarkup( Markup.keyboard().addRow(
                Markup.Button.create("Перейти в личку", botUrl, CallbackType.GO_TO_PRIVATE_CHAT, "someDate"),
                Markup.Button.create("Ok", CallbackType.DELETE_LAST_MESSAGES, "nothing")
        ).build());
        message.enableMarkdown(true);
        return message;
    }

    public static SendMessage messageWithDeleteOption(String botMessageText, Update update, boolean deleteUserMessageOrNot) {
        var message = new SendMessage();
        var messageId = update.getMessage().getMessageId();
        message.setChatId(update.getMessage().getChatId());
        message.setText(botMessageText);
        message.setReplyMarkup(Markup.keyboard().addRow(
                Markup.Button.create("Ok", CallbackType.DELETE_LAST_MESSAGES, "nothing")
        ).build());
        message.enableMarkdown(true);
        message.setReplyToMessageId(messageId);
        return message;
    }
    public static SendMessage registerBeforeMessage(Long chatId, String before) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(BotMessagesEnum.REGISTER_BEFORE_.getMessage(before));
        message.setReplyMarkup(Markup.keyboard().addRow(
                Markup.Button.create("Ok", CallbackType.DELETE_LAST_MESSAGES, "nothing")
        ).build());
        message.enableMarkdown(true);
        return message;
    }

    public static DeleteMessage deletePreviousUserMessage(Update update){
        var deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(update.getMessage().getMessageId());
        deleteMessage.setChatId(update.getMessage().getChatId());
        return deleteMessage;
    }


}
