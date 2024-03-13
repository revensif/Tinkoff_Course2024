package edu.java.exception;

public class ChatAlreadyRegisteredException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Чат уже был зарегистрирован";
    }
}
