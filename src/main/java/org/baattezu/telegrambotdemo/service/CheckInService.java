package org.baattezu.telegrambotdemo.service;

import org.baattezu.telegrambotdemo.model.CheckIn;
import org.baattezu.telegrambotdemo.model.User;
import org.baattezu.telegrambotdemo.repository.CheckInRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CheckInService {

    private final CheckInRepository checkInRepository;

    public CheckInService(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
    }

    public CheckIn checkInUser(User user) {
        CheckIn checkIn = new CheckIn();
        checkIn.setUser(user);
        checkIn.setCheckInTime(LocalDateTime.now());
        return checkInRepository.save(checkIn);
    }
}
