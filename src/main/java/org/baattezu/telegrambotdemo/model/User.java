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

    @Column(name = "private_chat_id")
    private Long privateChatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_chat_id")
    private GroupChat groupChat;

    @Column(name = "results_for_week")
    private String resultsForWeek;

}
