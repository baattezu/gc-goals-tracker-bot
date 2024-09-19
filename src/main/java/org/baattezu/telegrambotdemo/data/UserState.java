package org.baattezu.telegrambotdemo.data;

public enum UserState {
    NONE,
    SETTING_GOAL,
    CONFIRMING_GOAL,
    WAITING_FOR_GOAL,
    WAITING_FOR_TITLE,
    WAITING_FOR_DESCRIPTION,
    WAITING_FOR_REWARD,
    WAITING_FOR_DEADLINE,
    WAITING_FOR_RESULTS// Добавь другие состояния по необходимости
}