package com.leonardolariu.cryptorecommendation.payload.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.leonardolariu.cryptorecommendation.utils.Constants.MINIMUM_TIMESTAMP;

/**
 * Stopped using this request model, due to OpenAPI 3.0 no longer supporting
 * {@link org.springframework.web.bind.annotation.RequestBody} for {@link org.springframework.web.bind.annotation.GetMapping}
 */

@Deprecated
@Data
public class GetIntervalTokenDataRequest {

    @NotNull
    @Min(MINIMUM_TIMESTAMP)
    private Long start_timestamp;

    @NotNull
    @Min(MINIMUM_TIMESTAMP)
    private Long end_timestamp;
}
