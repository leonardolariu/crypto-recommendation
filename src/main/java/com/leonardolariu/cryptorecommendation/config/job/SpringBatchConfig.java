package com.leonardolariu.cryptorecommendation.config.job;

import com.leonardolariu.cryptorecommendation.config.properties.TokenProperties;
import com.leonardolariu.cryptorecommendation.entity.TokenPrice;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TokenPriceItemWriter tokenPriceItemWriter;
    private final ComputeLookupTablesTasklet computeLookupTablesTasklet;

    private final TokenProperties tokenProperties;

    @Bean
    public Map<String, FlatFileItemReader<TokenPrice>> readers() {
        Map<String, FlatFileItemReader<TokenPrice>> itemReaderMap = new HashMap<>();

        for (int i = 0; i < tokenProperties.getSymbols().size(); ++i) {
            String symbol = tokenProperties.getSymbols().get(i);
            String resource = tokenProperties.getResources().get(i);

            FlatFileItemReader<TokenPrice> itemReader = new FlatFileItemReader<>();
            itemReader.setResource(new FileSystemResource(resource));
            itemReader.setName(symbol + "DATA-READER");
            itemReader.setLinesToSkip(1);
            itemReader.setLineMapper(lineMapper());

            itemReaderMap.put(symbol, itemReader);
        }

        return itemReaderMap;
    }

    private LineMapper<TokenPrice> lineMapper() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames(tokenProperties.getHeaders().toArray(new String[0]));

        BeanWrapperFieldSetMapper<TokenPrice> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TokenPrice.class);

        DefaultLineMapper<TokenPrice> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public TokenPriceProcessor processor() {
        return new TokenPriceProcessor();
    }

    @Bean
    public Map<String, Step> steps() {
        Map<String, Step> stepMap = new HashMap<>();

        for (int i = 0; i < tokenProperties.getSymbols().size(); ++i) {
            String symbol = tokenProperties.getSymbols().get(i);

            Step step = stepBuilderFactory.get("LOAD-" + symbol + "-DATA").<TokenPrice, TokenPrice>chunk(10)
                    .reader(readers().get(symbol))
                    .processor(processor())
                    .writer(tokenPriceItemWriter)
                    .build();

            stepMap.put(symbol, step);
        }

        return stepMap;
    }

    @Bean
    public Job loadData() {
        Map<String, Step> stepMap = steps();
        SimpleJobBuilder jobBuilder = jobBuilderFactory.get("LOAD-CRYPTO-DATA")
                .incrementer(new RunIdIncrementer())
                .start(stepMap.get(tokenProperties.getSymbols().get(0)));

        for (int i = 1; i < tokenProperties.getSymbols().size(); ++i) {
            jobBuilder.next(stepMap.get(tokenProperties.getSymbols().get(i)));
        }

        jobBuilder.next(stepBuilderFactory.get("COMPUTE-LOOKUP-TABLES")
                .tasklet(computeLookupTablesTasklet)
                .build());

        return jobBuilder.build();
    }
}
