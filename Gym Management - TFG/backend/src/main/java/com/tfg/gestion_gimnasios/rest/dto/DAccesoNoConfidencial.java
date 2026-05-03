package com.tfg.gestion_gimnasios.rest.dto;

import java.time.LocalDateTime;

public record DAccesoNoConfidencial(
    int id, 
    LocalDateTime horaEntrada, 
    LocalDateTime horaSalida) {
}
