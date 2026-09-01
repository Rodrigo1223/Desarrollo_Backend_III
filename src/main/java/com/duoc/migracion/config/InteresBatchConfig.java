package com.duoc.migracion.config;

import com.duoc.migracion.model.Interes;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class InteresBatchConfig {

    @Bean
    public Job interesJob(JobRepository jobRepository, @Qualifier("interesStep") Step interesStep) {
        return new JobBuilder("interesJob", jobRepository)
                .start(interesStep)
                .build();
    }

    @Bean
    public Step interesStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            @Qualifier("itemReaderInteres") ItemReader<Interes> interesReader, // <--- Apuntamos al nombre correcto del componente Reader
                            ItemProcessor<Interes, Interes> interesProcessor,
                            ItemWriter<Interes> interesWriter) {
        return new StepBuilder("interesStep", jobRepository)
                .<Interes, Interes>chunk(50, transactionManager)
                .reader(interesReader)
                .processor(interesProcessor)
                .writer(interesWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(5000)
                .build();
    }

    @Bean
    public ItemWriter<Interes> interesWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Interes>()
                .dataSource(dataSource)
                .sql("INSERT INTO intereses (cuenta_id, saldo, tasa, interes_generado) VALUES (:cuentaId, :saldo, :tasa, :interesGenerado)")
                .beanMapped()
                .build();
    }
}