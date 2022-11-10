package com.leonardolariu.cryptorecommendation.service;

import com.leonardolariu.cryptorecommendation.config.properties.TokenProperties;
import com.leonardolariu.cryptorecommendation.domain.IntervalTokenData;
import com.leonardolariu.cryptorecommendation.entity.TokenPrice;
import com.leonardolariu.cryptorecommendation.exception.NotFoundException;
import com.leonardolariu.cryptorecommendation.exception.RequestException;
import com.leonardolariu.cryptorecommendation.repository.InMemoryTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.leonardolariu.cryptorecommendation.exception.ErrorCode.CONSTRAINT_VIOLATION_ERROR;
import static com.leonardolariu.cryptorecommendation.exception.ErrorCode.NOT_FOUND_ERROR;

@Service
public class CryptoRecommendationService {

    private final TokenProperties tokenProperties;
    private final InMemoryTokenRepository inMemoryTokenRepository;

    @Autowired
    public CryptoRecommendationService(TokenProperties tokenProperties, InMemoryTokenRepository inMemoryTokenRepository) {
        this.tokenProperties = tokenProperties;
        this.inMemoryTokenRepository = inMemoryTokenRepository;
    }

    public List<String> getADescendingSortedListOfAllTheTokensComparingTheNormalizedRange(long start_timestamp, long end_timestamp) {
        if (end_timestamp < start_timestamp) {
            throw new RequestException("end_timestamp cannot be lower than start_timestamp", CONSTRAINT_VIOLATION_ERROR);
        }

        List<String> symbols = tokenProperties.getSymbols();
        List<IntervalTokenData> intervalTokenDataList = new ArrayList<>();

        for (String symbol : symbols) {
            int left = getIndexOfTheOldestTimestampGreaterOrEqualThan(symbol, start_timestamp);
            int right = getIndexOfTheNewestTimestampLowerOrEqualThan(symbol, end_timestamp);

            IntervalTokenData[][] lookupTable = inMemoryTokenRepository.getLookupTablesMap().get(symbol);
            int len = right - left + 1;
            int log2len = (int) (Math.log(len) / Math.log(2));
            double min = Math.min(lookupTable[left][log2len].getMin(), lookupTable[right - (1 << log2len) + 1][log2len].getMin());
            double max = Math.max(lookupTable[left][log2len].getMax(), lookupTable[right - (1 << log2len) + 1][log2len].getMax());

            IntervalTokenData intervalTokenData = IntervalTokenData.builder()
                    .symbol(symbol)
                    .oldest(lookupTable[left][0].getOldest())
                    .newest(lookupTable[right][0].getNewest())
                    .min(min)
                    .max(max)
                    .normalizedRange((max - min) / min)
                    .build();

            intervalTokenDataList.add(intervalTokenData);
        }

        return intervalTokenDataList.stream()
                .sorted(((o1, o2) -> Double.compare(o2.getNormalizedRange(), o1.getNormalizedRange())))
                .map(IntervalTokenData::getSymbol)
                .collect(Collectors.toList());
    }

    public IntervalTokenData getIntervalTokenDataFor(String symbol, long start_timestamp, long end_timestamp) {
        if (end_timestamp < start_timestamp) {
            throw new RequestException("end_timestamp cannot be lower than start_timestamp", CONSTRAINT_VIOLATION_ERROR);
        }

        int left = getIndexOfTheOldestTimestampGreaterOrEqualThan(symbol, start_timestamp);
        int right = getIndexOfTheNewestTimestampLowerOrEqualThan(symbol, end_timestamp);

        IntervalTokenData[][] lookupTable = inMemoryTokenRepository.getLookupTablesMap().get(symbol);
        int len = right - left + 1;
        int log2len = (int) (Math.log(len) / Math.log(2));
        double min = Math.min(lookupTable[left][log2len].getMin(), lookupTable[right - (1 << log2len) + 1][log2len].getMin());
        double max = Math.max(lookupTable[left][log2len].getMax(), lookupTable[right - (1 << log2len) + 1][log2len].getMax());

        return IntervalTokenData.builder()
                .oldest(lookupTable[left][0].getOldest())
                .newest(lookupTable[right][0].getNewest())
                .min(min)
                .max(max)
                .normalizedRange((max - min) / min)
                .build();
    }

    private int getIndexOfTheOldestTimestampGreaterOrEqualThan(String symbol, long start_timestamp) {
        List<Long> tokenTimestamps = inMemoryTokenRepository.getTokenPricesMap().get(symbol).stream()
                .map(TokenPrice::getTimestamp)
                .collect(Collectors.toList());

        int left = Collections.binarySearch(tokenTimestamps, start_timestamp);

        if (left < 0) {
            left = Math.abs(left+1);
        }

        if (left > tokenTimestamps.size() - 1) {
            throw new NotFoundException("Could not find a data point older than start_timestamp", NOT_FOUND_ERROR);
        }

        return left;
    }

    private int getIndexOfTheNewestTimestampLowerOrEqualThan(String symbol, long end_timestamp) {
        List<Long> tokenTimestamps = inMemoryTokenRepository.getTokenPricesMap().get(symbol).stream()
                .map(TokenPrice::getTimestamp)
                .collect(Collectors.toList());

        int right = Collections.binarySearch(tokenTimestamps, end_timestamp);

        if (right == -1) {
            throw new NotFoundException("Could not find a data point newer than end_timestamp", NOT_FOUND_ERROR);
        }

        if (right < 0) {
            right = Math.abs(right+2);
        }

        return right;
    }

    public String getTheTokenWithTheHighestNormalizedRange(long start_timestamp, long end_timestamp) {
        return this.getADescendingSortedListOfAllTheTokensComparingTheNormalizedRange(start_timestamp, end_timestamp).get(0);
    }
}
