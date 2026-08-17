package com.duoc.migracion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "intereses") // Asegúrate de que coincida con el nombre de tu tabla en MySQL
public class Interes {

    @Id
    private Long cuentaId; // Asumiendo que cuentaId es la clave primaria

    private String nombre;
    private Double saldo;
    private Integer edad;
    private String tipo;

    public Interes() {
    }

    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = saldo; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}