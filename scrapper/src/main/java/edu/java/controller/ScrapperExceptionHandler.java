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

    @ExceptionHandler({ChatNotFoundException.class, LinkNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleChatAndLinkNotFoundException(Exception exception) {
        return constructErrorResponse(
            exception.getMessage(),
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            exception
        );
    }

    @ExceptionHandler({ChatAlreadyRegisteredException.class, LinkAlreadyTrackedException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleChatAndLinkAlreadyRegisteredException(Exception exception) {
        return constructErrorResponse(
            exception.getMessage(),
            String.valueOf(HttpStatus.CONFLICT.value()),
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
