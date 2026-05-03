package com.tfg.gestion_gimnasios.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TipoClase {
    @Id
    private String nombre;
    private String descripcion;
    private int maxPlazas;

    public TipoClase() {
    }
    public TipoClase(String nombre, String descripcion, int maxPlazas) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.maxPlazas = maxPlazas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getMaxPlazas() {
        return maxPlazas;
    }

    public void setMaxPlazas(int numeroMaximoPlazas) {
        this.maxPlazas = numeroMaximoPlazas;
    }
}
