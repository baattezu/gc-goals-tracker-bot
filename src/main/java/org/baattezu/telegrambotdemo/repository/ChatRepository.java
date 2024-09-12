package org.baattezu.telegrambotdemo.repository;

import org.baattezu.telegrambotdemo.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
