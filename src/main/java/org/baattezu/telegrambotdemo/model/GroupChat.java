package org.baattezu.telegrambotdemo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Table(name = "groupchat")
public class GroupChat {

    @Id
    private Long id;

    @Column(name = "chat_name")
    private String name;

    @OneToMany(mappedBy = "groupChat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupChat chat = (GroupChat) o;
        return Objects.equals(id, chat.id) && Objects.equals(name, chat.name) && Objects.equals(users, chat.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, users);
    }
}
