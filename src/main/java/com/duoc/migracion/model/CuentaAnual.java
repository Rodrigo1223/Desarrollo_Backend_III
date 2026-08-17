package com.duoc.migracion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "cuentas_anuales")
public class CuentaAnual {
    @Id
    private Long cuentaId;
    private String fecha;
    private String transaccion;
    private Double monto;
    private String descripcion;
}