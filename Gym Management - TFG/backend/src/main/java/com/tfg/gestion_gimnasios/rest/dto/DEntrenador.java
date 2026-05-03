package com.tfg.gestion_gimnasios.rest.dto;

public record DEntrenador(
    String nombre,
    String email,
    String clave,
    String tlf,
    Boolean activo
){
}