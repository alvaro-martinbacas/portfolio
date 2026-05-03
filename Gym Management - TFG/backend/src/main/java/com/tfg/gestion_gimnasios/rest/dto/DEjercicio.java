package com.tfg.gestion_gimnasios.rest.dto;

public record DEjercicio(
    int id,
    String nombre,
    String grupoMuscular,
    String equipo,
    String video
) {
    
}
