package com.tfg.gestion_gimnasios.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.gestion_gimnasios.entidades.ClaseColectiva;
import com.tfg.gestion_gimnasios.repositorios.RepositorioSolicitud;
import com.tfg.gestion_gimnasios.rest.dto.*;
import com.tfg.gestion_gimnasios.servicios.ServicioGimnasio;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gestiongimnasios/solicitudes")
@CrossOrigin("/localhost:8080")
public class ControladorSolicitud {
    @Autowired
    Mapeador mapeador;
    @Autowired
    ServicioGimnasio servicio;
    @Autowired
    RepositorioSolicitud solicitudRepo;

    @GetMapping("/{idSolicitud}/clase")
    public DClaseColectiva buscarClasePorSolicitud(@PathVariable int idSolicitud) {
        ClaseColectiva clase = servicio.buscarClasePorSolicitud(idSolicitud);
        return mapeador.toDTO(clase);

    }
}