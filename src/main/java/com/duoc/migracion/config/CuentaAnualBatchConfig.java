package com.duoc.migracion.config;

import com.duoc.migracion.model.CuentaAnual;
import com.duoc.migracion.reader.CuentaAnualReader;
import com.duoc.migracion.writer.CuentaAnualWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CuentaAnualBatchConfig {

    @Bean
    public Step cuentaAnualStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                CuentaAnualReader cuentaAnualReader,
                                CuentaAnualWriter cuentaAnualWriter) {
        return new StepBuilder("cuentaAnualStep", jobRepository)
                .<CuentaAnual, CuentaAnual>chunk(10, transactionManager)
                .reader(cuentaAnualReader)
                .writer(cuentaAnualWriter)
                .build();
    }

    @Bean("cuentaAnualJob")
    public Job cuentaAnualJob(JobRepository jobRepository, Step cuentaAnualStep) {
        return new JobBuilder("cuentaAnualJob", jobRepository)
                .start(cuentaAnualStep)
                .build();
    }
}