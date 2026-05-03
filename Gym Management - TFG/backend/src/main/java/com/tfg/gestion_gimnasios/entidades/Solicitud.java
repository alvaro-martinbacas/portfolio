package com.tfg.gestion_gimnasios.entidades;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Solicitud {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)   
    private int id;
    private LocalDate fechaSolicitud;
    private Boolean confReserva;


    @NotNull
    @ManyToOne(optional = true)
    @JoinColumn(name="usuario_id")
    private Usuario usuario;

    public Solicitud() {
    }

    public Solicitud(int id, LocalDate fechaSolicitud, Boolean confReserva) {
        this.id = id;
        this.fechaSolicitud = fechaSolicitud;
        this.confReserva = confReserva;
        this.usuario = null;
    }

    public Solicitud(LocalDate fechaSolicitud, Usuario usuario) {
        this.fechaSolicitud = fechaSolicitud;
        this.confReserva = false; 
        this.usuario = usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Boolean getConfReserva() {
        return confReserva;
    }

    public void setConfReserva(Boolean confReserva) {
        this.confReserva = confReserva;
    }

    public Usuario getUsuario() {
        return usuario;
    }

}
