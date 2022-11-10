package com.leonardolariu.cryptorecommendation.exception;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorCode {

    MISSING_REQUEST_HEADER_ERROR("MISSING_REQUEST_HEADER_ERROR"),
    INVALID_REQUEST_HEADER_ERROR("INVALID_REQUEST_HEADER_ERROR"),
    MISSING_REQUEST_BODY_ERROR("MISSING_REQUEST_BODY_ERROR"),
    INVALID_REQUEST_BODY_ERROR("INVALID_REQUEST_BODY_ERROR"),
    MISSING_REQUEST_PARAM_ERROR("MISSING_REQUEST_PARAM_ERROR"),
    INVALID_REQUEST_PARAM_ERROR("INVALID_REQUEST_PARAM_ERROR"),
    CONSTRAINT_VIOLATION_ERROR("CONSTRAINT_VIOLATION_ERROR"),
    NOT_FOUND_ERROR("NOT_FOUND_ERROR");

    private final String errorDescription;

    @JsonValue
    public String getErrorDescription() {
        return this.errorDescription;
    }

}
