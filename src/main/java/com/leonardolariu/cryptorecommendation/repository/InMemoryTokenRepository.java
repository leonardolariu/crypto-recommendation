package com.leonardolariu.cryptorecommendation.repository;

import com.leonardolariu.cryptorecommendation.domain.IntervalTokenData;
import com.leonardolariu.cryptorecommendation.entity.TokenPrice;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Getter
public class InMemoryTokenRepository {
    private final Map<String, List<TokenPrice>> tokenPricesMap = new HashMap<>();
    private Map<String, IntervalTokenData[][]> lookupTablesMap;

    public void addTokenPrices(List<TokenPrice> items) {
        String symbol = items.get(0).getSymbol();
         if (!tokenPricesMap.containsKey(symbol)) {
             tokenPricesMap.put(symbol, new ArrayList<>());
         }

         tokenPricesMap.get(symbol).addAll(items);
    }

    public void setLookupTablesMap(Map<String, IntervalTokenData[][]> lookupTablesMap) {
        this.lookupTablesMap = lookupTablesMap;
    }

}
