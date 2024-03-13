package edu.java.exception;

public class LinkAlreadyTrackedException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Ссылка уже отслеживается";
    }
}
