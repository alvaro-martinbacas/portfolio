package com.tfg.gestion_gimnasios.rest.dto;

import java.time.LocalDate;

public record DSolicitud(
    int id,
    LocalDate fechaSolicitud,
    Boolean confReserva,
    String emailUsuario,
    String nombreClase
) {
    
}
