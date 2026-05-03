package com.tfg.gestion_gimnasios.rest;



import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tfg.gestion_gimnasios.entidades.Ejercicio;
import com.tfg.gestion_gimnasios.entidades.EjercicioRutina;
import com.tfg.gestion_gimnasios.excepciones.EjercicioNoEncontrado;
import com.tfg.gestion_gimnasios.excepciones.EjercicioRutinaNoEncontrado;
import com.tfg.gestion_gimnasios.repositorios.*;
import com.tfg.gestion_gimnasios.rest.dto.*;
import com.tfg.gestion_gimnasios.servicios.ServicioGimnasio;



@RestController
@RequestMapping("/gestiongimnasios/ejerciciosrutina")
@CrossOrigin("/localhost:8080")
public class ControladorEjerciciosRutina {
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
     * Endpoint para asignar un ejercicio a un ejercicioRutina existente.
     * @param idEjercicio El ID del ejercicio.
     * @param idEjercicioRutina El ID del ejercicioRutina.
     */
    @PutMapping("/{idEjercicioRutina}/ejercicio/{idEjercicio}")
    @ResponseStatus(HttpStatus.OK)
    public void aniadirEjercicioAEjercicioRutina(
        @PathVariable int idEjercicio,
        @PathVariable int idEjercicioRutina) {
        servicio.aniadirEjercicioAEjercicioRutina(idEjercicio, idEjercicioRutina);
    }

    @GetMapping("/{idEjercicioRutina}/video")
    public String obtenerVideoEjercicio(@PathVariable int idEjercicioRutina) {
        EjercicioRutina er = ejercicioRutinaRepo.buscar(idEjercicioRutina)
            .orElseThrow(EjercicioRutinaNoEncontrado::new);

        Ejercicio ejercicioId = ejercicioRepo.buscar(er.getEjercicio().getId())
            .orElseThrow(EjercicioNoEncontrado::new);
        String nombreEjercicio = ejercicioId.getNombre();

        return servicio.obtenerVideoEjercicio(nombreEjercicio);
    }
}
