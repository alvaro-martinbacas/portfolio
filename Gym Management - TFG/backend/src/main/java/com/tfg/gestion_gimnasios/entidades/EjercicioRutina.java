package com.tfg.gestion_gimnasios.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
public class EjercicioRutina {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Positive
    private int series;

    @Positive
    private int repeticiones;

    @PositiveOrZero
    private int descanso;

    private String indicaciones;

    @ManyToOne
    @JoinColumn(name = "ejercicio_id")
    private Ejercicio ejercicio;

    public EjercicioRutina() {
    }

    public EjercicioRutina(int id, int series, int repeticiones, int descanso, String indicaciones) {
        this.id = id;
        this.series = series;
        this.repeticiones = repeticiones;
        this.descanso = descanso;
        this.indicaciones = indicaciones;
        this.ejercicio = null;
    }

    public EjercicioRutina(int series, int repeticiones, int descanso, String indicaciones) {
        this.series = series;
        this.repeticiones = repeticiones;
        this.descanso = descanso;
        this.indicaciones = indicaciones;
        this.ejercicio = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public int getDescanso() {
        return descanso;
    }

    public void setDescanso(int descanso) {
        this.descanso = descanso;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }
}
