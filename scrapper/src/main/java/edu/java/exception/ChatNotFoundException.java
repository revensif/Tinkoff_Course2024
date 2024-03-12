package edu.java.exception;

public class ChatNotFoundException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Чат не существует";
    }
}
