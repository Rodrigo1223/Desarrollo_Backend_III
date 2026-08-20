package com.duoc.migracion.config;

import com.duoc.migracion.model.Interes;
import com.duoc.migracion.reader.InteresReader;
import com.duoc.migracion.repository.InteresRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class InteresBatchConfig {

    private final InteresReader interesReader;

    // Inyectamos el componente InteresReader mediante el constructor
    public InteresBatchConfig(InteresReader interesReader) {
        this.interesReader = interesReader;
    }

    @Bean
    public RepositoryItemWriter<Interes> interesWriter(InteresRepository repository) {
        RepositoryItemWriter<Interes> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step interesStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            RepositoryItemWriter<Interes> interesWriter) {
        return new StepBuilder("interesStep", jobRepository)
                .<Interes, Interes>chunk(10, transactionManager)
                .reader(interesReader.itemReaderInteres())
                .writer(interesWriter)
                .build();
    }

    @Bean("interesJob")
    public Job interesJob(JobRepository jobRepository, Step interesStep) {
        return new JobBuilder("interesJob", jobRepository)
                .start(interesStep)
                .build();
    }
}