package org.baattezu.telegrambotdemo.repository;

import org.baattezu.telegrambotdemo.model.CheckIn;
import org.baattezu.telegrambotdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    List<CheckIn> findByUserAndCheckInTimeBetween(User user, LocalDateTime start, LocalDateTime end);
    List<CheckIn> findByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);
}