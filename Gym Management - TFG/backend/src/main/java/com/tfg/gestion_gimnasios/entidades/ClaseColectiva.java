package com.tfg.gestion_gimnasios.entidades;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.DayOfWeek;

import java.time.LocalTime;
import java.util.List;

@Entity
public class ClaseColectiva {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)   
    private int id;

    @NotNull
    private DayOfWeek diaSemana;

    @NotNull
    private LocalTime horaIni;

    @NotNull
    private LocalTime horaFin;

    @PositiveOrZero
    private int plazasOcupadas;

    @ManyToOne
    @JoinColumn(name="tipo_clase_id")
    private TipoClase tipoClase;

    @ManyToOne
    @JoinColumn(name="entrenador_id")
    Entrenador entrenador;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clase_id")
    List<Solicitud> solicitudes;
    

    public ClaseColectiva() {
    }

    public ClaseColectiva(int id, DayOfWeek diaSemana, LocalTime horaIni, LocalTime horaFin) {
        this.id = id;
        this.diaSemana = diaSemana;
        this.horaIni = horaIni;
        this.horaFin = horaFin;
        this.plazasOcupadas = 0;
        this.entrenador = null;
    }
    public ClaseColectiva(DayOfWeek diaSemana, LocalTime horaIni, LocalTime horaFin) {

        this.diaSemana = diaSemana;
        this.horaIni = horaIni;
        this.horaFin = horaFin;
        this.plazasOcupadas = 0;
        this.entrenador = null;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public void aniadirSolicitud(Solicitud solicitud) {
        if (solicitud != null && !solicitudes.contains(solicitud)) {
            solicitudes.add(solicitud);
            if (solicitud.getConfReserva()){
                plazasOcupadas++;
            }

        }
    }

    public void eliminarSolicitud(Solicitud solicitud) {
        if (solicitud != null && solicitudes.contains(solicitud)) {
            solicitudes.remove(solicitud);
            if (solicitud.getConfReserva()){
                plazasOcupadas--;
            }        
        }
    }

    public void aceptarPrimeraSolicitudAutomaticamente(){
        for (Solicitud solicitud : solicitudes) {
            if (!solicitud.getConfReserva()) {
                solicitud.setConfReserva(true);
                plazasOcupadas++;
                break; // Aceptamos solo la primera solicitud pendiente
            }
        }
    }

    public void aniadirTipoClase(TipoClase tipoClase) {
        if (tipoClase != null) {
            this.tipoClase = tipoClase;
        }
    }

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DayOfWeek diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraIni() {
        return horaIni;
    }

    public void setHoraIni(LocalTime horaIni) {
        this.horaIni = horaIni;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public int getMaxPlazas() {
        return tipoClase.getMaxPlazas();
    }

    public int getPlazasOcupadas() {
        return plazasOcupadas;
    }

    public void setPlazasOcupadas(int plazasOcupadas) {
        this.plazasOcupadas = plazasOcupadas;
    }

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public String getTipoClase() {
        return tipoClase.getNombre();
    }

    public void aniadirEntrenador(Entrenador entrenador) {
        if (entrenador != null) {
            this.entrenador = entrenador;
        }
    }
    public void quitarEntrenador() {
        this.entrenador = null;
    }

    public boolean eliminarEntrenadorSiCoincide(String email) {
    if (this.entrenador != null && this.entrenador.getEmail().equals(email)) {
        this.entrenador = null;
        return true;
    }
    return false;
    }
    public boolean comprobarHoras() {
        return horaIni.isBefore(horaFin);
    }
}
