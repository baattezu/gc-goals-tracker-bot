package org.baattezu.telegrambotdemo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "goals")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String goal;
    @Column(columnDefinition = "TEXT")
    private String reward;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private Boolean completed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // Метод для установки userId напрямую
    // Поле для хранения userId напрямую
    @Setter
    @Column(name = "user_id")
    private Long userId;

    // Метод для работы с объектом User
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
    }
}
