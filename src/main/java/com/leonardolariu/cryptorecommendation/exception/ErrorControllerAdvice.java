package com.leonardolariu.cryptorecommendation.exception;

import com.leonardolariu.cryptorecommendation.payload.response.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorControllerAdvice {

    @ExceptionHandler(RequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> requestException(final RequestException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(exception.getMessage(), exception.getErrorCode());
        logError(apiErrorResponse);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> requestException(final ConstraintViolationException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(exception.getMessage(), ErrorCode.INVALID_REQUEST_PARAM_ERROR);
        logError(apiErrorResponse);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> requestException(final MissingServletRequestParameterException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(exception.getMessage(), ErrorCode.MISSING_REQUEST_PARAM_ERROR);
        logError(apiErrorResponse);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorResponse> notFoundException(final NotFoundException exception) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(exception.getMessage(), exception.getErrorCode());
        logError(apiErrorResponse);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }

    private static void logError(ApiErrorResponse error) {
        log.error("{} - {}", error.getErrorDescription(), error.getMessage());
    }
}
