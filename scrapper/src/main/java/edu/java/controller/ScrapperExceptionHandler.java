package edu.java.controller;

import edu.java.dto.response.ApiErrorResponse;
import edu.java.exception.ChatAlreadyRegisteredException;
import edu.java.exception.ChatNotFoundException;
import edu.java.exception.LinkAlreadyTrackedException;
import edu.java.exception.LinkNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ScrapperExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleIncorrectRequestException(Exception exception) {
        return constructErrorResponse(
            "Некорректные параметры запроса",
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            exception
        );
    }

    @ExceptionHandler(ChatNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleChatNotFoundException(ChatNotFoundException exception) {
        return constructErrorResponse(
            "Чат не сущесвует",
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            exception
        );
    }

    @ExceptionHandler(ChatAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleChatAlreadyRegisteredException(ChatAlreadyRegisteredException exception) {
        return constructErrorResponse(
            "Чат уже был зарегестрирован",
            String.valueOf(HttpStatus.CONFLICT.value()),
            exception
        );
    }

    @ExceptionHandler(LinkAlreadyTrackedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleLinkAlreadyTrackedException(LinkAlreadyTrackedException exception) {
        return constructErrorResponse(
            "Ссылка уже отслеживается",
            String.valueOf(HttpStatus.CONFLICT.value()),
            exception
        );
    }

    @ExceptionHandler(LinkNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleLinkNotFoundException(LinkNotFoundException exception) {
        return constructErrorResponse(
            "Ссылка не сущесвует",
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            exception
        );
    }

    private ApiErrorResponse constructErrorResponse(String description, String code, Exception exception) {
        return new ApiErrorResponse(
            description,
            code,
            exception.getClass().getName(),
            exception.getMessage(),
            Arrays.stream(exception.getStackTrace()).map(Objects::toString).toArray(String[]::new)
        );
    }
}
