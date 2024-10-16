package org.baattezu.telegrambotdemo.results;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.service.ChatService;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
@Slf4j
public class ProgressImageGenerator {

    private final GoalService goalService;
    private final UserService userService;
    private final ChatService chatService;

    public void deleteResults(String chatId) {
        var chat = chatService.findById(Long.valueOf(chatId));
        var userList = userService.getAllUsersFromGroupChat(chat);

        for (User user : userList) {
            userService.clearResultsForWeek(user);
        }
        log.info("Результаты пользователей за неделю стерты.");
    }

    public String formatTable(String chatId, boolean isGroupMessage) {
        // Получаем данные для таблицы
        String[][] data = getData(chatId);

        // Формируем таблицу с использованием Markdown
        StringBuilder text = new StringBuilder();
        text.append("*Цели и результаты пользователей" +
                (!isGroupMessage ? " в вашей группе" : " в этой группе")
                + ":*\n");
        text.append("```\n");

        // Заголовок таблицы
        text.append(String.format("%-15s %-6s %-6s\n", "Имя", "Цели", "Рез-ы"));
        text.append(String.format("%-15s %-6s %-6s\n", "==============", "=====", "====="));

        for (int row = 1; row < data.length; row++) {
            // Проверяем, если имя превышает 20 символов, разбиваем его на несколько строк
            String username = data[row][0];
            if (username != null && username.length() > 15) {
                int splitIndex = 15;
                String part1 = username.substring(0, splitIndex);
                String part2 = username.substring(splitIndex);

                // Добавляем первую часть имени в таблицу
                text.append(String.format("%-15s %-5s %-5s\n", part1, data[row][1], data[row][2]));
                // Добавляем вторую часть имени в следующую строку с отступом
                if (part2.length() > 15) {
                    part2 = part2.substring(0, 12) + "..."; // Сокращаем до 7 символов + "..."
                }
                text.append(String.format("%-15s\n", part2));
            } else {
                // Если имя меньше 20 символов, просто добавляем его
                text.append(String.format("%-15s %-5s %-5s\n",
                        username != null ? username : "",
                        data[row][1] != null ? data[row][1] : "",
                        data[row][2] != null ? data[row][2] : ""));
            }
            text.append(String.format("%-15s %-6s %-6s\n", "--------------", "-----", "-----"));
        }
        text.append("```");

        return text.toString();
    }

    public String[][] getData(String chatId) {
        var chat = chatService.findById(Long.valueOf(chatId));
        var userList = userService.getAllUsersFromGroupChat(chat);
        String[][] data = new String[userList.size() + 1][3];
        data[0] = new String[]{"Имя", "Цели", "Результат"}; // Добавление заголовка
        int row = 1; // Начинаем с первой строки после заголовка
        for (User user : userList) {
            var username = user.getUsername();
            String statusGoals = goalService.isThereAnyGoals(user.getId())
                    ? "✅" : "❌";
            String statusResults = !user.getResultsForWeek().equals("Пока нет результатов")
                    ? "✅" : "❌";
            // Добавляем данные в массив
            data[row][0] = username;
            data[row][1] = statusGoals;
            data[row][2] = statusResults;
            row++; // Переход к следующей строке
        }
        return data;
    }


    private static String twoColumnFormatting(String sss) {
        int n = 2;
        int m = 6;
        int substringLength = 35;
        int substringBegin = 0;
        int substringEnd = substringLength;
        int sssLength = sss.length();
        String[][] strings = new String[n][m];
        String formattedString = sss.replace("\n", "   ");
        String[] strings1 = new String[m];

        // Инициализация массива strings1 пустыми строками
        for (int j = 0; j < m; j++) {
            strings1[j] = "";
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                // Если конец строки меньше длины, чтобы избежать ошибок
                if (substringBegin >= sssLength) {
                    break;
                }

                // Найти позицию пробела для переноса строки без обрезки слов
                if (substringEnd < sssLength) {
                    // Найти последний пробел до конца строки
                    int lastSpaceIndex = formattedString.lastIndexOf(' ', substringEnd);
                    // Если пробел найден и находится после начального индекса
                    if (lastSpaceIndex > substringBegin) {
                        substringEnd = lastSpaceIndex;
                    }
                } else {
                    // Если остаток строки меньше substringLength
                    substringEnd = sssLength;
                }

                // Получаем подстроку
                strings[i][j] = formattedString.substring(substringBegin, substringEnd).strip();
                // Заполняем строку пробелами до нужной длины
                StringBuilder lineBuilder = new StringBuilder(strings[i][j]);
                while (lineBuilder.length() < substringLength) {
                    lineBuilder.append(" ");
                }
                // Добавляем в массив строк с разделителем
                String space = j == 1 ? "" : "         ";
                strings1[j] += lineBuilder.toString() + space;

                // Обновление индексов для следующей подстроки
                substringBegin = substringEnd + 1; // Переход к следующему символу после пробела
                substringEnd = substringBegin + substringLength;
            }
        }

        // Объединяем строки
        StringBuilder result = new StringBuilder();
        for (int j = 0; j < m; j++) {
            result.append(strings1[j]).append("\n");
        }
        return result.toString();
    }

    private static String oneRowFormatting(String sss) {
        int lineWidth = 73; // Maximum number of characters per line
        sss = sss.replace("\n", "   ");
        StringBuilder result = new StringBuilder();
        int begin = 0;

        while (begin < sss.length()) {
            int end = begin + lineWidth;

            // If end is beyond the length of the string, set it to the string's end
            if (end > sss.length()) {
                end = sss.length();
            } else {
                // Find the last space within the line width to avoid breaking words
                int lastSpaceIndex = sss.lastIndexOf(' ', end);
                if (lastSpaceIndex > begin) {
                    end = lastSpaceIndex; // Move end to the last space
                }
            }

            // Append the substring and add a newline character
            result.append(sss, begin, end).append("\n");

            // Move to the next line's start
            begin = end + 1; // Skip the space or move to the next character
        }

        return result.toString();
    }

    public void generateResult(String chatId) throws IOException {
        // Данные таблицы
        String[][] data = getData(chatId);

        String[] columns = {"__", "__", "__"};
        // Создаем JTable
        JTable table = new JTable(data, columns);

        // Применяем кастомный рендерер для ячеек с переносом текста
        customizeTable(table);

        // Устанавливаем размер таблицы для рендеринга
        table.setSize(table.getPreferredSize());

        // Создаем изображение
        BufferedImage image = new BufferedImage(table.getWidth(), table.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Устанавливаем фон
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, table.getWidth(), table.getHeight());

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Рендерим таблицу на изображение
        table.print(g2d);
        g2d.dispose();

        File directory = new File("resultsimagefolder");
        if (!directory.exists()) {
            directory.mkdirs(); // Создаем директории, если их нет
        }
        // Сохраняем изображение
        ImageIO.write(image, "png", new File("resultsimagefolder/" + chatId + ".png"));
        log.info("created file somewhere");
    }

    private static void customizeTable(JTable table) {
        table.setRowHeight(50);
        // Устанавливаем рендерер для ячеек с переносом текста
        table.setDefaultRenderer(Object.class, new MultiLineTableCellRenderer());
        // Устанавливаем минимальную ширину колонок
        table.getColumnModel().getColumn(0).setMinWidth(250);

        table.getColumnModel().getColumn(1).setMinWidth(250);
        table.getColumnModel().getColumn(2).setMinWidth(250);

        // Фон и цвет текста
        table.setBackground(Color.BLACK);
        table.setForeground(Color.WHITE);
    }
}

class MultiLineTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JTextArea textArea = new JTextArea(String.valueOf(value));
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setFont(new Font("Serif", Font.BOLD, 22)); // Ensure consistent font
//        try {
//            Font emojiFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/fonts/NotoColorEmoji-Regular.ttf")).deriveFont(22f);
//            textArea.setFont(emojiFont);
//        } catch (FontFormatException | IOException e) {
//            e.printStackTrace();
//        }
        textArea.setOpaque(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add some padding

        if (isSelected) {
            textArea.setBackground(table.getSelectionBackground());
            textArea.setForeground(table.getSelectionForeground());
        } else {
            var chat = new GetChatMember();
            textArea.setBackground(table.getBackground());
            textArea.setForeground(table.getForeground());
        }

        return textArea;
    }
}
