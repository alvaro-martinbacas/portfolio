package com.tfg.gestion_gimnasios.rest.dto;

import java.time.LocalTime;

public record DGimnasio(
    int id,
    String nombre,
    String direccion,
    String telefono,
    LocalTime horaApertura,
    LocalTime horaCierre
){
}
