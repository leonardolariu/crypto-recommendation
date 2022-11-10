package com.leonardolariu.cryptorecommendation.exception;

import lombok.Data;

@Data
public class RequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final ErrorCode errorCode;

    public RequestException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
