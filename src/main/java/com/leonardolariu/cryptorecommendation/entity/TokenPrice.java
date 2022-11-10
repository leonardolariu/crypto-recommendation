package com.leonardolariu.cryptorecommendation.entity;

import lombok.Data;

@Data
public class TokenPrice {
    long timestamp;
    String symbol;
    double price;
}
