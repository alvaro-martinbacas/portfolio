package com.tfg.gestion_gimnasios.rest.dto;

import java.time.LocalDate;

public record DUsuario (
    String nombre,
    String apellidos,
    String tlf,
    String email,
    String clave,
    Boolean cuotaPagada,
    LocalDate cuotaValidaHasta
){
}
