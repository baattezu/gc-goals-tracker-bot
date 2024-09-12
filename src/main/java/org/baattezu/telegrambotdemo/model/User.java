package org.baattezu.telegrambotdemo.model;

import jakarta.persistence.*;
import lombok.Data;
import org.baattezu.telegrambotdemo.data.UserState;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    private Long id;
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    private boolean isPinned = false; // Флаг, показывающий закреплен ли пользователь

}
