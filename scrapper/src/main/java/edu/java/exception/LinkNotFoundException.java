package edu.java.exception;

public class LinkNotFoundException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Ссылка не существует";
    }
}
