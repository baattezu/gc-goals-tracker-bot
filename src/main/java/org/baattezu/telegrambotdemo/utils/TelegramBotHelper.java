package org.baattezu.telegrambotdemo.utils;

import org.baattezu.telegrambotdemo.data.CallbackType;
import org.baattezu.telegrambotdemo.model.Goal;
import org.baattezu.telegrambotdemo.utils.keyboard.Markup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static org.baattezu.telegrambotdemo.config.BotConfig.BOT_URL;

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
            var goalName = goal.getGoal();
            var reward = goal.getReward();
            goalName = (goalName.length() > 900) ? goalName.substring(0, 900) + "..." : goalName;
            reward = (reward.length() > 900) ? reward.substring(0, 900) + "..." : reward;

            response.append("<blockquote expandable>");
            var status = goal.getCompleted() ? "✅" : "";
            response.append(BotMessagesEnum.GET_GOAL_DETAIL_MESSAGE_VER1.getMessage(
                    index, status, goalName, reward
            ));
            if (!isGroupChat) {
                var text = "#" + index + " " + (goal.getCompleted() ? "❌" : "⭕");
                currentRow.add(Markup.Button.create(text, CallbackType.COMPLETE_IN_ALL_GOALS, goal.getId() + ":" + currentPage));
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

    public static SendMessage justGoToPrivateMessage(Long chatId, String botMessageText) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(botMessageText);
        message.setReplyMarkup(Markup.keyboard().addRow(
                Markup.Button.create("Перейти в личку", BOT_URL, CallbackType.GO_TO_PRIVATE_CHAT, "someDate"),
                Markup.Button.create("Ok", CallbackType.DELETE_LAST_MESSAGES, "nothing")
        ).build());
        message.enableMarkdown(true);
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

    public static void setMainMenuKeyboard(SendMessage sendMessage) {
        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true); // Клавиатура будет адаптироваться под экран
        replyKeyboardMarkup.setOneTimeKeyboard(false); // Клавиатура останется открытой

        // Создаем список строк с кнопками
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Создаем первую строку клавиатуры с кнопками
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/set_goal"));
        row1.add(new KeyboardButton("/goals"));

        // Создаем вторую строку
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/progress"));
        row2.add(new KeyboardButton("/results"));

        // Добавляем строки клавиатуры
        keyboard.add(row1);
        keyboard.add(row2);

        // Устанавливаем клавиатуру в объект
        replyKeyboardMarkup.setKeyboard(keyboard);

        // Присоединяем клавиатуру к сообщению
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    public static InlineKeyboardMarkup okButton() {
        return Markup.keyboard()
                .addRow(Markup.Button.create("Ok", CallbackType.DELETE_LAST_MESSAGES, "nothing"))
                .build();
    }

    public static DeleteMessage deleteMessage(long chatId, long messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId((int) messageId);
        return deleteMessage;
    }

}
