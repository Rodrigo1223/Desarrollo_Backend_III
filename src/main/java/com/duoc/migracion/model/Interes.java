package com.duoc.migracion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "intereses")
public class Interes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuenta_id")
    private Long cuentaId;

    private String nombre;

    @Column(precision = 19, scale = 2)
    private BigDecimal saldo;

    private int edad;
    private String tipo;

    @Column(precision = 19, scale = 2)
    private BigDecimal tasa;

    @Column(name = "interes_generado", precision = 19, scale = 2)
    private BigDecimal interesGenerado;
}