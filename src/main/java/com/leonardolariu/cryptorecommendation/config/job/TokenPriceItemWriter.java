package com.leonardolariu.cryptorecommendation.config.job;

import com.leonardolariu.cryptorecommendation.entity.TokenPrice;
import com.leonardolariu.cryptorecommendation.repository.InMemoryTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenPriceItemWriter implements ItemWriter<TokenPrice> {

    private final InMemoryTokenRepository inMemoryTokenRepository;

    @Override
    public void write(List items) {
        inMemoryTokenRepository.addTokenPrices(items);
    }
}
