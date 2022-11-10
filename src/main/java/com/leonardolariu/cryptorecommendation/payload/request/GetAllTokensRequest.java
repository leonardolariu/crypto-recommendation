package com.leonardolariu.cryptorecommendation.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class GetAllTokensRequest {

    @NotNull
    @Min(MINIMUM_TIMESTAMP)
    private Long start_timestamp;

    @Schema(
            description = "Datetime of the accept, default to now if not provided",
            type = "Long",
            format = "Long",
            example = "1641553200000"
    )
    @NotNull
    @Min(MINIMUM_TIMESTAMP)
    private Long end_timestamp;
}
