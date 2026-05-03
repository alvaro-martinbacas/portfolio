package com.tfg.gestion_gimnasios.rest.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;


public record DClaseColectiva(
    int id,
    DayOfWeek diaSemana,
    LocalTime horaIni,
    LocalTime horaFin,
    int plazasOcupadas,
    String nombreTipoClase,
    String nombreEntrenador
) {
    
}
