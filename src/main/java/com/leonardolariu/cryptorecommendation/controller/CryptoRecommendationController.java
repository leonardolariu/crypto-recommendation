package com.leonardolariu.cryptorecommendation.controller;

import com.leonardolariu.cryptorecommendation.config.properties.TokenProperties;
import com.leonardolariu.cryptorecommendation.domain.IntervalTokenData;
import com.leonardolariu.cryptorecommendation.exception.RequestException;
import com.leonardolariu.cryptorecommendation.service.CryptoRecommendationService;
import com.leonardolariu.cryptorecommendation.utils.HeadersUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

import static com.leonardolariu.cryptorecommendation.exception.ErrorCode.CONSTRAINT_VIOLATION_ERROR;
import static com.leonardolariu.cryptorecommendation.utils.Constants.*;

@Validated
@RestController
@RequestMapping("/tokens")
public class CryptoRecommendationController {

    private final TokenProperties tokenProperties;
    private final CryptoRecommendationService cryptoRecommendationService;

    @Autowired
    public CryptoRecommendationController(TokenProperties tokenProperties, CryptoRecommendationService cryptoRecommendationService) {
        this.tokenProperties = tokenProperties;
        this.cryptoRecommendationService = cryptoRecommendationService;
    }

    @Operation(summary = "Get a descending sorted list of all the supported tokens, comparing the normalized range for a given time interval",
            description = "The normalized range is computed as (max-min)/min. All data points within the given (start_timestamp, end_timestamp) interval will be considered.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getADescendingSortedListOfAllTheTokensComparingTheNormalizedRange(
            @RequestHeader Map<String, String> headers,
            @RequestParam @NotNull @Min(MINIMUM_TIMESTAMP) @Schema(description = "Oldest timestamp to be considered (in Unix format)", example = "1641009600000") Long start_timestamp,
            @RequestParam @NotNull @Min(MINIMUM_TIMESTAMP) @Schema(description = "Newest timestamp to be considered (in Unix format)", example = "1643659200000") Long end_timestamp) {

        HeadersUtils.validate(headers);

        List<String> response = cryptoRecommendationService.getADescendingSortedListOfAllTheTokensComparingTheNormalizedRange(start_timestamp, end_timestamp);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the oldest/newest/min/max values for a given token and time interval",
            description = "All data points within the given (start_timestamp, end_timestamp) interval will be considered.")
    public ResponseEntity<?> getIntervalTokenDataFor(
            @RequestHeader Map<String, String> headers,
            @PathVariable @Schema(description = "Token symbol", example = "BTC") String symbol,
            @RequestParam @NotNull @Min(MINIMUM_TIMESTAMP) @Schema(description = "Oldest timestamp to be considered (in Unix format)", example = "1641045600000") long start_timestamp,
            @RequestParam @NotNull @Min(MINIMUM_TIMESTAMP) @Schema(description = "Newest timestamp to be considered (in Unix format)", example = "1641553200000") Long end_timestamp) {

        HeadersUtils.validate(headers);

        if (!tokenProperties.getSymbols().contains(symbol)) {
            throw new RequestException(
                    "The provided symbol is not supported. Try one of the following: " + tokenProperties.getSymbols().toString(),
                    CONSTRAINT_VIOLATION_ERROR
            );
        }

        IntervalTokenData response = cryptoRecommendationService.getIntervalTokenDataFor(symbol, start_timestamp, end_timestamp);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/greatest-value")
    @Operation(summary = "Get the token with the greatest value for a given metric and time interval",
            description = "All data points within the given (start_timestamp, end_timestamp) interval will be considered.")
    public ResponseEntity<?> getTheTokenWithTheHighestNormalizedRange(
            @RequestHeader Map<String, String> headers,
            @RequestParam @NotBlank @Pattern(regexp = METRIC_REGEX, flags = Pattern.Flag.CASE_INSENSITIVE)
            @Schema(description = "Criteria used for sorting. Currently only the normalized-range metric is supported", example = "normalized-range") String metric,
            @RequestParam @NotNull @Min(MINIMUM_TIMESTAMP) @Schema(description = "Oldest timestamp to be considered (in Unix format)", example = "1641009600000") Long start_timestamp,
            @RequestParam @NotNull @Min(MINIMUM_TIMESTAMP) @Schema(description = "Newest timestamp to be considered (in Unix format)", example = "1643659200000") Long end_timestamp) {

        HeadersUtils.validate(headers);

        String response = cryptoRecommendationService.getTheTokenWithTheHighestNormalizedRange(start_timestamp, end_timestamp);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
