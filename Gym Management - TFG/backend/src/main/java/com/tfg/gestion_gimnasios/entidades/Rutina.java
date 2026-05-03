package com.tfg.gestion_gimnasios.entidades;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Rutina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "rutina_id")
    private List<EjercicioRutina> ejerciciosRutina;

    @ManyToOne
    @JoinColumn(name = "entrenador_id")
    private Entrenador entrenador;


    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Rutina() {
    }

    public Rutina(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ejerciciosRutina = new LinkedList<>();
    }

    public Rutina(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ejerciciosRutina = new LinkedList<>();
    }
    public Rutina(int id, String nombre, String descripcion, Entrenador entrenador) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ejerciciosRutina = new LinkedList<>();
        this.entrenador = entrenador;
    }

    public void aniadirEjercicioRutina(EjercicioRutina ejercicioRutina) {
        if (ejercicioRutina != null) {
            this.ejerciciosRutina.add(ejercicioRutina);
        }
    }

    public void eliminarEjercicioRutina(EjercicioRutina ejercicioRutina) {
        if (ejercicioRutina != null) {
            this.ejerciciosRutina.remove(ejercicioRutina);
        }
    }

    public void eliminarEntrenador(){
        entrenador.eliminarRutina(this);
        this.entrenador = null;
    }

    @PreRemove
    private void desvincularEntrenador() {
        if (entrenador != null) {
            entrenador.eliminarRutina(this);
        }
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

    public List<EjercicioRutina> getEjerciciosRutina() {
        return ejerciciosRutina;
    }


    public Entrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Entrenador entrenador) {
        this.entrenador = entrenador;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    

}
