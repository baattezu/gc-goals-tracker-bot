package org.baattezu.telegrambotdemo.repository;

import org.baattezu.telegrambotdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatId(Long chatId);
}
