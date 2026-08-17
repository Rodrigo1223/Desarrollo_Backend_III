package com.duoc.migracion.reader;

import com.duoc.migracion.model.Interes;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class InteresReader {

    @Bean
    public FlatFileItemReader<Interes> itemReaderInteres() {
        return new FlatFileItemReaderBuilder<Interes>()
                .name("interesItemReader")
                .resource(new ClassPathResource("data/intereses.csv"))
                .linesToSkip(1)
                .delimited()
                .names("cuentaId", "nombre", "saldo", "edad", "tipo")
                .fieldSetMapper(fieldSet -> {
                    Interes interes = new Interes();
                    interes.setCuentaId(fieldSet.readLong("cuentaId")); // <-- Usar readLong aquí
                    interes.setNombre(fieldSet.readString("nombre"));
                    interes.setSaldo(fieldSet.readDouble("saldo"));
                    interes.setEdad(fieldSet.readInt("edad"));
                    interes.setTipo(fieldSet.readString("tipo"));
                    return interes;
                })
                .build();
    }
}