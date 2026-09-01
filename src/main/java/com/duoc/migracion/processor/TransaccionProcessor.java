package com.duoc.migracion.processor;

import com.duoc.migracion.dto.TransaccionCsvDto;
import com.duoc.migracion.exception.InvalidDateException;
import com.duoc.migracion.exception.InvalidMontoException;
import com.duoc.migracion.exception.RegistroMalClasificadoException;
import com.duoc.migracion.model.Transaccion;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class TransaccionProcessor implements ItemProcessor<TransaccionCsvDto, Transaccion> {

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    };

    @Override
    public Transaccion process(@NonNull TransaccionCsvDto item) {
        validarItem(item);
        parsearFecha(item.getFecha(), item.getId());
        validarMonto(item.getMonto(), item.getId());
        String tipo = validarYNormalizarTipo(item.getTipo());

        return construirTransaccion(item, tipo);
    }

    private void validarItem(TransaccionCsvDto item) {
        if (item == null) {
            throw new IllegalArgumentException("Registro de transacción nulo");
        }
        if (item.getId() == null) {
            throw new IllegalArgumentException("id obligatorio para la transacción");
        }
        if (item.getFecha() == null || item.getFecha().isBlank()) {
            throw new InvalidDateException("Fecha obligatoria para el registro " + item.getId());
        }
    }

    private void parsearFecha(String fechaStr, Long idTransaccion) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                LocalDate.parse(fechaStr, formatter);
                return;
            } catch (DateTimeParseException ignored) {
                // Se intenta con el siguiente formato disponible
            }
        }
        throw new InvalidDateException("Fecha inválida en la transacción " + idTransaccion + ": " + fechaStr);
    }

    private void validarMonto(BigDecimal monto, Long idTransaccion) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidMontoException("Monto inválido para la transacción " + idTransaccion + ": " + monto);
        }
    }

    private String validarYNormalizarTipo(String tipoRaw) {
        String tipo = tipoRaw == null ? "" : tipoRaw.trim().toLowerCase();
        if (!tipo.equals("débito") && !tipo.equals("debito") &&
                !tipo.equals("crédito") && !tipo.equals("credito") &&
                !tipo.equals("deposito") && !tipo.equals("depósito")) {
            throw new RegistroMalClasificadoException("Tipo de transacción no soportado: " + tipoRaw);
        }
        return tipo;
    }

    private Transaccion construirTransaccion(TransaccionCsvDto item, String tipo) {
        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaId(item.getId());
        transaccion.setFecha(item.getFecha());
        transaccion.setMonto(item.getMonto().setScale(2, RoundingMode.HALF_UP));
        transaccion.setTipo(tipo);
        return transaccion;
    }
}