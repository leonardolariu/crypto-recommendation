package com.leonardolariu.cryptorecommendation.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.leonardolariu.cryptorecommendation.exception.ErrorCode;
import lombok.Data;

@Data
public class ApiErrorResponse {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String errorDescription;

    public ApiErrorResponse(String message, ErrorCode errorCode) {
        this.message = message;
        this.errorDescription = errorCode.getErrorDescription();
    }

}
