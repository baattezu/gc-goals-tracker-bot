package org.baattezu.telegrambotdemo.results;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baattezu.telegrambotdemo.service.GoalService;
import org.baattezu.telegrambotdemo.service.UserService;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
@Slf4j
public class ResultGenerator {

    private final GoalService goalService;
    private final UserService userService;

    public void generateResult() throws IOException {
        // Данные таблицы
        var chatId =
        String[][] data = {
                {"Имя", "Цели", "Результат"}
        };


        String[] columns = {"Имя", "Цели", "Результат"};

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

        // Рендерим таблицу на изображение
        table.print(g2d);
        g2d.dispose();

        // Сохраняем изображение
        ImageIO.write(image, "png", new File("table_image.png"));

        System.out.println("Изображение таблицы сохранено как table_image.png");
    }

    // Метод для кастомизации таблицы
    private static void customizeTable(JTable table) {
        // Устанавливаем шрифты для таблицы
        Font cellFont = new Font("Arial", Font.PLAIN, 25);
        table.setFont(cellFont);
        table.setRowHeight(80);
        // Устанавливаем рендерер для ячеек с переносом текста
        table.setDefaultRenderer(Object.class, new MultiLineTableCellRenderer());
        // Устанавливаем минимальную ширину колонок
        table.getColumnModel().getColumn(0).setMinWidth(250);

        table.getColumnModel().getColumn(1).setMinWidth(350);
        table.getColumnModel().getColumn(1).setMaxWidth(250);
        table.getColumnModel().getColumn(2).setMinWidth(500);

        // Фон и цвет текста
        table.setBackground(Color.BLACK);
        table.setForeground(Color.WHITE);
    }

    // Рендерер для переноса текста и динамической высоты строк

}
class MultiLineTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JTextArea textArea = new JTextArea(value.toString());
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);

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
