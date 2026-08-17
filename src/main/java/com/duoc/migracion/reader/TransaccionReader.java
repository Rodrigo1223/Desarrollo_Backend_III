package com.duoc.migracion.reader;

import com.duoc.migracion.dto.TransaccionCsvDto;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class TransaccionReader {

    @Bean
    public FlatFileItemReader<TransaccionCsvDto> itemReaderTransaccion() {
        return new FlatFileItemReaderBuilder<TransaccionCsvDto>()
                .name("transaccionItemReader")
                .resource(new ClassPathResource("data/transacciones.csv"))
                .linesToSkip(1)
                .delimited()
                .names("id", "fecha", "monto", "tipo")
                .targetType(TransaccionCsvDto.class)
                .build();
    }
}