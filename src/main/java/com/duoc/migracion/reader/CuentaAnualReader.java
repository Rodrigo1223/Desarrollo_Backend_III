package com.duoc.migracion.reader;

import com.duoc.migracion.model.CuentaAnual;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class CuentaAnualReader extends FlatFileItemReader<CuentaAnual> {

    public CuentaAnualReader() {
        setName("cuentaAnualReader");
        setResource(new FileSystemResource("src/main/resources/data/cuentas_anuales.csv"));
        setLinesToSkip(1);

        DefaultLineMapper<CuentaAnual> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("cuentaId", "fecha", "transaccion", "monto", "descripcion");

        BeanWrapperFieldSetMapper<CuentaAnual> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(CuentaAnual.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        setLineMapper(lineMapper);
    }
}