package com.leonardolariu.cryptorecommendation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class IntervalTokenData {
    @JsonIgnore
    String symbol;

    double oldest;
    double newest;
    double min;
    double max;
    double normalizedRange;
}
