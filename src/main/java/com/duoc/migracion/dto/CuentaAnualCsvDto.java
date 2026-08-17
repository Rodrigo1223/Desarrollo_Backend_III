package com.duoc.migracion.dto;

import lombok.Data;

@Data
public class CuentaAnualCsvDto {
    private Long cuentaId;
    private String fecha;
    private String transaccion;
    private Double monto;
    private String descripcion;
}