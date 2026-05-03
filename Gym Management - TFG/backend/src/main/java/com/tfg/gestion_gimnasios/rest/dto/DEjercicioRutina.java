package com.tfg.gestion_gimnasios.rest.dto;

public record DEjercicioRutina(
    int id,
    int series,
    int repeticiones,
    int descanso,
    String indicaciones,
    String nombreEjercicio
){
    
}
