package com.duoc.migracion.config;

import com.duoc.migracion.dto.CuentaAnualCsvDto;
import com.duoc.migracion.exception.InvalidCsvRecordException;
import com.duoc.migracion.exception.InvalidDateException;
import com.duoc.migracion.exception.InvalidMontoException;
import com.duoc.migracion.exception.RegistroMalClasificadoException;
import com.duoc.migracion.listener.BancoJobListener;
import com.duoc.migracion.listener.BatchSkipListener;
import com.duoc.migracion.model.CuentaAnual;
import com.duoc.migracion.processor.CuentaAnualProcessor;
import com.duoc.migracion.writer.CuentaAnualWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CuentaAnualBatchConfig {

    @Bean
    public Step cuentaAnualStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                SynchronizedItemStreamReader<CuentaAnualCsvDto> cuentaAnualReader,
                                CuentaAnualProcessor cuentaAnualProcessor,
                                CuentaAnualWriter cuentaAnualWriter,
                                BancoJobListener bancoJobListener,
                                BatchSkipListener batchSkipListener) {
        return new StepBuilder("cuentaAnualStep", jobRepository)
                .<CuentaAnualCsvDto, CuentaAnual>chunk(5, transactionManager)
                .reader(cuentaAnualReader)
                .processor(cuentaAnualProcessor)
                .writer(cuentaAnualWriter)
                .faultTolerant()
                .skip(InvalidCsvRecordException.class)
                .skip(InvalidMontoException.class)
                .skip(InvalidDateException.class)
                .skip(RegistroMalClasificadoException.class)
                .skipLimit(5000)
                .retry(IllegalArgumentException.class)
                .retryLimit(2)
                .listener(bancoJobListener)
                .listener(batchSkipListener)
                .build();
    }

    @Bean
    public Tasklet consolidacionTasklet(JdbcTemplate jdbcTemplate) {
        return (contribution, chunkContext) -> {
            // 1. Agrupa y consolida usando los nombres de columnas reales de la tabla cuentas_anuales
            jdbcTemplate.execute("CREATE TEMPORARY TABLE temp_cuenta_anual AS " +
                    "SELECT MIN(id) as id, cuenta_id, anio, " +
                    "SUM(total_depositos) as total_depositos, SUM(total_retiros) as total_retiros, " +
                    "MAX(saldo_anual) as saldo_anual FROM cuentas_anuales " +
                    "GROUP BY cuenta_id, anio");

            // 2. Limpia los registros parciales de la tabla principal
            jdbcTemplate.execute("TRUNCATE TABLE cuentas_anuales");

            // 3. Inserta de vuelta los registros consolidados manteniendo los campos clave
            jdbcTemplate.execute("INSERT INTO cuentas_anuales (id, cuenta_id, anio, total_depositos, total_retiros, saldo_anual) " +
                    "SELECT id, cuenta_id, anio, total_depositos, total_retiros, saldo_anual FROM temp_cuenta_anual");

            // 4. Elimina la tabla temporal
            jdbcTemplate.execute("DROP TEMPORARY TABLE temp_cuenta_anual");

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step consolidacionStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  Tasklet consolidacionTasklet) {
        return new StepBuilder("consolidacionStep", jobRepository)
                .tasklet(consolidacionTasklet, transactionManager)
                .build();
    }

    @Bean("cuentaAnualJob")
    public Job cuentaAnualJob(JobRepository jobRepository,
                              Step cuentaAnualStep,
                              Step consolidacionStep,
                              BancoJobListener bancoJobListener) {
        return new JobBuilder("cuentaAnualJob", jobRepository)
                .start(cuentaAnualStep)
                .next(consolidacionStep)
                .listener(bancoJobListener)
                .build();
    }
}