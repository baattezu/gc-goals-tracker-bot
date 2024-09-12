package org.baattezu.telegrambotdemo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Chat {

    @Id
    private Long id;

    @Column(name = "chat_name")
    private String name;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

}
