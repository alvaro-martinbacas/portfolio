package com.tfg.gestion_gimnasios.rest;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.gestion_gimnasios.entidades.TipoClase;
import com.tfg.gestion_gimnasios.repositorios.RepositorioTipoClase;
import com.tfg.gestion_gimnasios.rest.dto.*;
import com.tfg.gestion_gimnasios.servicios.ServicioGimnasio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gestiongimnasios/tiposclases")
@CrossOrigin("/localhost:8080")
public class ControladorTipoClase {
    @Autowired
    Mapeador mapeador;
    @Autowired
    ServicioGimnasio servicio;
    @Autowired
    RepositorioTipoClase tipoClaseRepo;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DTipoClase crearTipoClase(@RequestBody DTipoClase dTipoClase) {
        return mapeador.toDTO(servicio.nuevoTipoClase(mapeador.toEntity(dTipoClase)));
    }

    @GetMapping("/{nombre}")
    @ResponseStatus(HttpStatus.OK)
    public DTipoClase buscarTipoClase(@PathVariable String nombre) {
        return mapeador.toDTO(servicio.buscarTipoClase(nombre).orElseThrow());
    }

    @GetMapping("/todos")
    @ResponseStatus(HttpStatus.OK)
    public List<DTipoClase> buscarClases(){
        List<TipoClase> tiposClases = tipoClaseRepo.findAll();
        List<DTipoClase> tipoClasesDTO = new LinkedList<>();
        for (TipoClase tipoClase : tiposClases) {
            tipoClasesDTO.add(mapeador.toDTO(tipoClase));
        }
        return tipoClasesDTO;
    }

    @PutMapping("/{nombre}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DTipoClase> actualizarTipoClase(@PathVariable String nombre, @RequestBody DTipoClase dTipoClase) {
        if (servicio.buscarTipoClase(nombre).isPresent()) {
            DTipoClase tipoConNombre = new DTipoClase(nombre, dTipoClase.descripcion(), dTipoClase.maxPlazas());
            TipoClase actualizado = servicio.actualizarTipoClase(mapeador.toEntity(tipoConNombre));
            return ResponseEntity.status(HttpStatus.OK).body(mapeador.toDTO(actualizado));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{nombre}")
    @ResponseStatus(HttpStatus.OK)
    public void eliminarTipoClase(@PathVariable String nombre) {
        servicio.eliminarTipoClase(nombre);
    }
}
