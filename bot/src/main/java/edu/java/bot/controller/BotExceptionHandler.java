package edu.java.bot.controller;

import edu.java.bot.dto.response.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BotExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleIncorrectRequestException(Exception exception) {
        return new ApiErrorResponse(
            "Неккоректные параметры запроса",
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            exception.getClass().getName(),
            exception.getMessage(),
            Arrays.stream(exception.getStackTrace()).map(Objects::toString).toArray(String[]::new)
        );
    }
}
