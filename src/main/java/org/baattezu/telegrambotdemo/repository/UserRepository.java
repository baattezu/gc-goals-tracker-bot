package org.baattezu.telegrambotdemo.repository;

import org.baattezu.telegrambotdemo.model.GroupChat;
import org.baattezu.telegrambotdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByGroupChat(GroupChat chat);
}
