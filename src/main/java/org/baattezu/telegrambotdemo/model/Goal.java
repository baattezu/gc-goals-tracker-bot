package org.baattezu.telegrambotdemo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "goals")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "goal_name")
    private String goalName;
    private String description;
    private String reward;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private Boolean completed;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
