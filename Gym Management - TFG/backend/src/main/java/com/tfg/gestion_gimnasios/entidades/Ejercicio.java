package com.tfg.gestion_gimnasios.entidades;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;

@Entity
public class Ejercicio {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @NotBlank
    private String nombre;
    @NotBlank
    private String grupoMuscular;
    @NotBlank
    private String equipo;
    
    private String video;


    public Ejercicio() {
    }

    public Ejercicio(int id, String nombre, String grupoMuscular, String equipo, String video) {
        this.id = id;
        this.nombre = nombre;
        this.grupoMuscular = grupoMuscular;
        this.equipo = equipo;
        this.video = video;
    }

    public Ejercicio(String nombre, String grupoMuscular, String equipo, String video) {
        this.nombre = nombre;
        this.grupoMuscular = grupoMuscular;
        this.equipo = equipo;
        this.video = video;
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

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
