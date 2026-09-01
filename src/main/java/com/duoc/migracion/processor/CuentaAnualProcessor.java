package com.duoc.migracion.processor;

import com.duoc.migracion.dto.CuentaAnualCsvDto;
import com.duoc.migracion.exception.InvalidDateException;
import com.duoc.migracion.exception.InvalidMontoException;
import com.duoc.migracion.exception.RegistroMalClasificadoException;
import com.duoc.migracion.model.CuentaAnual;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class CuentaAnualProcessor implements ItemProcessor<CuentaAnualCsvDto, CuentaAnual> {

    @Override
    public CuentaAnual process(CuentaAnualCsvDto item) throws Exception {
        // Validar que el ítem no sea nulo
        if (item == null) {
            throw new IllegalArgumentException("El registro anual no puede ser nulo.");
        }

        // Validar que el identificador de cuenta exista
        if (item.getCuentaId() == null) {
            throw new IllegalArgumentException("El campo cuenta_id es obligatorio en el CSV anual.");
        }

        // Validar presencia de la fecha
        if (item.getFecha() == null || item.getFecha().isBlank()) {
            throw new InvalidDateException("La fecha es obligatoria para la cuenta: " + item.getCuentaId());
        }

        // Validar y parsear la fecha de forma segura (evita doble parsing posterior)
        LocalDate fechaParseada;
        try {
            fechaParseada = LocalDate.parse(item.getFecha().trim());
        } catch (DateTimeParseException e) {
            throw new InvalidDateException("Formato de fecha inválido ('" + item.getFecha() + "') para la cuenta: " + item.getCuentaId());
        }

        // Validar que el monto sea mayor a cero
        if (item.getMonto() == null || item.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidMontoException("El monto debe ser mayor a cero para la cuenta: " + item.getCuentaId() + ", valor recibido: " + item.getMonto());
        }

        // Normalizar y validar el tipo de transacción
        String tipo = item.getTransaccion() == null ? "" : item.getTransaccion().trim().toLowerCase();
        if (!tipo.equals("deposito") && !tipo.equals("retiro") && !tipo.equals("compra")) {
            throw new RegistroMalClasificadoException("Tipo de transacción no soportado o desconocido: '" + item.getTransaccion() + "'");
        }

        // Preparar el monto normalizado con 2 decimales
        BigDecimal montoValido = item.getMonto().abs().setScale(2);

        // Mapeo de DTO a Entidad
        CuentaAnual cuenta = new CuentaAnual();
        cuenta.setCuentaId(item.getCuentaId());
        cuenta.setFecha(fechaParseada.toString());
        cuenta.setTransaccion(tipo);
        cuenta.setMonto(montoValido);
        cuenta.setDescripcion(item.getDescripcion());
        cuenta.setAnio(fechaParseada.getYear());
        cuenta.setTipoRegistro(tipo);
        cuenta.setSaldoAnual(montoValido);

        return cuenta;
    }
}