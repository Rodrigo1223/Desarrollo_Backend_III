package com.duoc.migracion.writer;

import com.duoc.migracion.model.CuentaAnual;
import com.duoc.migracion.repository.CuentaAnualRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CuentaAnualWriter implements ItemWriter<CuentaAnual> {

    @Autowired
    private CuentaAnualRepository cuentaAnualRepository;

    @Override
    public void write(@NonNull Chunk<? extends CuentaAnual> chunk) {
        cuentaAnualRepository.saveAll(chunk);
    }
}