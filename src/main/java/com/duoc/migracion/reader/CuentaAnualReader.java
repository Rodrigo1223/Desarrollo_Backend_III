package com.duoc.migracion.reader;

import com.duoc.migracion.dto.CuentaAnualCsvDto;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CuentaAnualReader {

    @Bean
    public SynchronizedItemStreamReader<CuentaAnualCsvDto> cuentaAnualItemReader() {
        FlatFileItemReader<CuentaAnualCsvDto> delegateReader = new FlatFileItemReader<>();
        delegateReader.setName("cuentaAnualCsvReader");
        delegateReader.setResource(new ClassPathResource("data/cuentas_anuales.csv"));
        delegateReader.setLinesToSkip(1);

        DefaultLineMapper<CuentaAnualCsvDto> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("cuentaId", "fecha", "transaccion", "monto", "descripcion");

        BeanWrapperFieldSetMapper<CuentaAnualCsvDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(CuentaAnualCsvDto.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        delegateReader.setLineMapper(lineMapper);

        SynchronizedItemStreamReader<CuentaAnualCsvDto> synchronizedReader = new SynchronizedItemStreamReader<>();
        synchronizedReader.setDelegate(delegateReader);

        return synchronizedReader;
    }
}