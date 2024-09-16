package org.baattezu.telegrambotdemo.repository;

import org.baattezu.telegrambotdemo.model.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<GroupChat, Long> {

}
