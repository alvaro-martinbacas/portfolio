package com.tfg.gestion_gimnasios.entidades;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Medalla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String descripcion;
    private String icono; 

    private String tipo;

    private int objetivo;

    @PreRemove
    private void removeUsuariosAssociations() {
        for (Usuario usuario : this.usuarios) {
            usuario.getMedallas().remove(this);
        }
    }

    @ManyToMany(mappedBy = "medallas")
    private List<Usuario> usuarios = new ArrayList<>();

    public Medalla() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(int objetivo) {
        this.objetivo = objetivo;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}