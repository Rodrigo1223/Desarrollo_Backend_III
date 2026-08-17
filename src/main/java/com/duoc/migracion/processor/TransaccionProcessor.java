package com.duoc.migracion.processor;

import com.duoc.migracion.dto.TransaccionCsvDto;
import com.duoc.migracion.model.Transaccion;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TransaccionProcessor implements ItemProcessor<TransaccionCsvDto, Transaccion> {

    @Override
    public Transaccion process(TransaccionCsvDto item) {
        if (item.getMonto() == null || item.getMonto() < 0) {
            throw new IllegalArgumentException("Monto inválido o negativo: " + item.getMonto());
        }

        Transaccion transaccion = new Transaccion();
        transaccion.setId(item.getId());
        transaccion.setFecha(item.getFecha());
        transaccion.setMonto(item.getMonto());
        transaccion.setTipo(item.getTipo());

        return transaccion;
    }
}