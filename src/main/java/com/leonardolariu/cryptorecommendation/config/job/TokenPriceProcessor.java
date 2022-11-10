package com.leonardolariu.cryptorecommendation.config.job;

import com.leonardolariu.cryptorecommendation.entity.TokenPrice;
import org.springframework.batch.item.ItemProcessor;


public class TokenPriceProcessor implements ItemProcessor<TokenPrice, TokenPrice> {

    @Override
    public TokenPrice process(TokenPrice tokenPrice) throws Exception {
        return tokenPrice;
    }
}
