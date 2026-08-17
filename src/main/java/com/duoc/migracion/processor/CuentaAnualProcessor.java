package com.duoc.migracion.processor;

import com.duoc.migracion.dto.CuentaAnualCsvDto;
import com.duoc.migracion.model.CuentaAnual;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class CuentaAnualProcessor implements ItemProcessor<CuentaAnualCsvDto, CuentaAnual> {

    @Override
    public CuentaAnual process(CuentaAnualCsvDto item) throws Exception {
        CuentaAnual cuenta = new CuentaAnual();
        cuenta.setCuentaId(item.getCuentaId());
        cuenta.setFecha(item.getFecha());
        cuenta.setTransaccion(item.getTransaccion());
        cuenta.setMonto(item.getMonto());
        cuenta.setDescripcion(item.getDescripcion());
        return cuenta;
    }
}