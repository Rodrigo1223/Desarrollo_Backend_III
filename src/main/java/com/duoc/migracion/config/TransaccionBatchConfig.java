package com.duoc.migracion.config;

import com.duoc.migracion.dto.TransaccionCsvDto;
import com.duoc.migracion.exception.InvalidCsvRecordException;
import com.duoc.migracion.exception.InvalidDateException;
import com.duoc.migracion.exception.InvalidMontoException;
import com.duoc.migracion.exception.RegistroMalClasificadoException;
import com.duoc.migracion.listener.BancoJobListener;
import com.duoc.migracion.listener.BatchSkipListener;
import com.duoc.migracion.model.Transaccion;
import com.duoc.migracion.processor.TransaccionProcessor;
import org.hibernate.StaleObjectStateException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransaccionBatchConfig {

    @Bean
    public Step transaccionStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                SynchronizedItemStreamReader<TransaccionCsvDto> itemReaderTransaccion,
                                TransaccionProcessor processor,
                                RepositoryItemWriter<Transaccion> itemWriterTransaccion,
                                BancoJobListener bancoJobListener,
                                BatchSkipListener batchSkipListener,
                                TaskExecutor taskExecutor) { // <-- 1. Inyectar el TaskExecutor
        return new StepBuilder("transaccionStep", jobRepository)
                .<TransaccionCsvDto, Transaccion>chunk(5, transactionManager)
                .reader(itemReaderTransaccion)
                .processor(processor)
                .writer(itemWriterTransaccion)
                .faultTolerant()
                .skip(InvalidCsvRecordException.class)
                .skip(InvalidMontoException.class)
                .skip(InvalidDateException.class)
                .skip(RegistroMalClasificadoException.class)
                .skip(ObjectOptimisticLockingFailureException.class)
                .skip(StaleObjectStateException.class)
                .skipLimit(5000)
                .retry(IllegalArgumentException.class)
                .retryLimit(2)
                .listener(bancoJobListener)
                .listener(batchSkipListener)
                .taskExecutor(taskExecutor) // <-- 2. Habilitar multi-threading para cumplir el requerimiento 5
                .build();
    }

    // 3. Definir el Pool de Hilos para el escalamiento
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(15);
        executor.setThreadNamePrefix("BatchThread-");
        executor.initialize();
        return executor;
    }

    @Bean("transaccionJob")
    public Job transaccionJob(JobRepository jobRepository, Step transaccionStep, BancoJobListener bancoJobListener) {
        return new JobBuilder("transaccionJob", jobRepository)
                .start(transaccionStep)
                .listener(bancoJobListener)
                .build();
    }
}