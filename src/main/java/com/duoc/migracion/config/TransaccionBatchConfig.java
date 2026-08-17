package com.duoc.migracion.config;

import com.duoc.migracion.dto.TransaccionCsvDto;
import com.duoc.migracion.model.Transaccion;
import com.duoc.migracion.processor.TransaccionProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransaccionBatchConfig {

    @Bean
    public Step transaccionStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                FlatFileItemReader<TransaccionCsvDto> itemReaderTransaccion,
                                TransaccionProcessor processor,
                                RepositoryItemWriter<Transaccion> itemWriterTransaccion) {
        return new StepBuilder("transaccionStep", jobRepository)
                .<TransaccionCsvDto, Transaccion>chunk(10, transactionManager)
                .reader(itemReaderTransaccion)
                .processor(processor)
                .writer(itemWriterTransaccion)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public Job transaccionJob(JobRepository jobRepository, Step transaccionStep) {
        return new JobBuilder("transaccionJob", jobRepository)
                .start(transaccionStep)
                .build();
    }
}