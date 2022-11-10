package com.leonardolariu.cryptorecommendation.config.job;

import com.leonardolariu.cryptorecommendation.domain.IntervalTokenData;
import com.leonardolariu.cryptorecommendation.entity.TokenPrice;
import com.leonardolariu.cryptorecommendation.repository.InMemoryTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link ComputeLookupTablesTasklet#execute} method creates the lookup tables used for
 * improving the performance of future RMQ (Range Minimum/ Maximum Queries)
 *
 * Memory Complexity - O(NlogN)
 * Time Complexity - O(1)
 *
 * This approach leverages the fact that we work with static arrays of data points
 * It is not possible to accept new data points without recomputing the entire data structure
 *
 * To understand this solution, check {@link <a href="https://medium.com/nybles/sparse-table-f3981fbb1bc8">Sparse Tables</a>}
 */

@Component
@RequiredArgsConstructor
public class ComputeLookupTablesTasklet implements Tasklet {

    private final InMemoryTokenRepository inMemoryTokenRepository;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        Map<String, IntervalTokenData[][]>  lookupTablesMap = new HashMap<>();
        Map<String, List<TokenPrice>>tokenPricesMap = inMemoryTokenRepository.getTokenPricesMap();

        for (String symbol : tokenPricesMap.keySet()) {
            List<TokenPrice> tokenPrices = tokenPricesMap.get(symbol);

            int len = tokenPrices.size();
            int log2len = (int) (Math.log(len) / Math.log(2));

            IntervalTokenData[][] lookupTable = new IntervalTokenData[len][log2len + 1];


            for (int i = 0; i < len; ++i) {
                IntervalTokenData intervalTokenData = IntervalTokenData.builder()
                        .oldest(tokenPrices.get(i).getPrice())
                        .newest(tokenPrices.get(i).getPrice())
                        .min(tokenPrices.get(i).getPrice())
                        .max(tokenPrices.get(i).getPrice())
                        .normalizedRange(0D)
                        .build();

                lookupTable[i][0] = intervalTokenData;
            }

            for (int j = 1; j <= log2len; ++j) {
                for (int i = 0; i + (1 << j) <= len; ++i) {
                    double min = Math.min(lookupTable[i][j-1].getMin(), lookupTable[i + (1 << (j - 1))][j - 1].getMin());
                    double max = Math.max(lookupTable[i][j-1].getMax(), lookupTable[i + (1 << (j - 1))][j - 1].getMax());
                    IntervalTokenData intervalTokenData = IntervalTokenData.builder()
                            .oldest(tokenPrices.get(i).getPrice())
                            .newest(tokenPrices.get(i + (1 << j) - 1).getPrice())
                            .min(min)
                            .max(max)
                            .normalizedRange((max - min) / min)
                            .build();

                    lookupTable[i][j] = intervalTokenData;
                }
            }

            lookupTablesMap.put(symbol, lookupTable);
        }

        inMemoryTokenRepository.setLookupTablesMap(lookupTablesMap);

        return null;
    }
}
