package com.tfg.gestion_gimnasios.rest;


import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.tfg.gestion_gimnasios.entidades.*;
import com.tfg.gestion_gimnasios.excepciones.EjercicioNoSePuedeEliminar;
import com.tfg.gestion_gimnasios.repositorios.*;
import com.tfg.gestion_gimnasios.rest.dto.*;
import com.tfg.gestion_gimnasios.servicios.ServicioGimnasio;


@RestController
@RequestMapping("/gestiongimnasios/ejercicios")
@CrossOrigin("/localhost:8080")
public class ControladorEjercicios {
    @Autowired
    ServicioGimnasio servicio;
    @Autowired
    RepositorioGimnasio gimnasioRepo;
    @Autowired
    RepositorioUsuario usuarioRepo;
    @Autowired
    RepositorioClaseColectiva claseRepo;
    @Autowired
    RepositorioAcceso accesoRepo;
    @Autowired
    RepositorioSolicitud solicitudRepo;
    @Autowired
    RepositorioEntrenador entrenadorRepo;
    @Autowired
    RepositorioRutina rutinaRepo;
    @Autowired
    RepositorioEjercicio ejercicioRepo;
    @Autowired
    RepositorioEjercicioRutina ejercicioRutinaRepo;    
    @Autowired
    Mapeador mapeador;

        /**
     * Endpoint para crear un nuevo ejercicio.
     * @param dEjercicio El DTO del ejercicio a crear.
     * @return El DTO del ejercicio creado.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DEjercicio crearEjercicio(@RequestBody DEjercicio dEjercicio) {
        Ejercicio ejercicio = mapeador.toEntity(dEjercicio);
        Ejercicio nuevoEjercicio = servicio.nuevoEjercicio(ejercicio);
        return mapeador.toDTO(nuevoEjercicio);
    }

    @PutMapping("/{idEjercicio}")
    public void actualizarEjercicio(@PathVariable int idEjercicio, @RequestBody DEjercicio dEjercicio) {
        Ejercicio ejercicio = mapeador.toEntity(dEjercicio);
        ejercicio.setId(idEjercicio);
        servicio.actualizarEjercicio(ejercicio);
    }

    @GetMapping("/todos")
    public Iterable<DEjercicio> buscarTodos() {
        List<Ejercicio> ejercicios = servicio.buscarEjercicios();
        List<DEjercicio> ejerciciosDTO = new LinkedList<>();
        for (Ejercicio ejercicio : ejercicios) {
            ejerciciosDTO.add(mapeador.toDTO(ejercicio));
        }
        return ejerciciosDTO;
    }

    @DeleteMapping("/{idEjercicio}")
    @ResponseStatus(HttpStatus.OK)
    public void eliminarEjercicio(@PathVariable int idEjercicio) {
        try {
            servicio.eliminarEjercicio(idEjercicio);
        } catch (EjercicioNoSePuedeEliminar e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
