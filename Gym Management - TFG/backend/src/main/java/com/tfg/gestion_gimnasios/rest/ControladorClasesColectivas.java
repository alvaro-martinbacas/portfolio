package com.tfg.gestion_gimnasios.rest;


import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tfg.gestion_gimnasios.entidades.*;
import com.tfg.gestion_gimnasios.excepciones.*;
import com.tfg.gestion_gimnasios.rest.dto.*;
import com.tfg.gestion_gimnasios.servicios.ServicioGimnasio;
import com.tfg.gestion_gimnasios.repositorios.*;


@RestController
@RequestMapping("/gestiongimnasios/clasescolectivas")
@CrossOrigin("/localhost:8080")
public class ControladorClasesColectivas {
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

    @PutMapping("/{idClase}/asignar")
    public ResponseEntity<?> asignarEntrenador(@PathVariable int idClase, @RequestBody String emailEntrenador) {
        try {
            servicio.anadirEntrenadorAClaseColectiva(idClase, emailEntrenador);
            return ResponseEntity.ok().build();
        } catch (ClaseColectivaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Clase no encontrada");
        } catch (EntrenadorNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entrenador no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al asignar el entrenador");
        }
    }

    @PutMapping("/{idClase}/quitar")
    public ResponseEntity<?> quitarEntrenador(@PathVariable int idClase) {
        try {
            servicio.quitarEntrenadorClaseColectiva(idClase);
            return ResponseEntity.ok().build();
        } catch (ClaseColectivaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Clase no encontrada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al quitar el entrenador");
        }
    }
    
    @GetMapping("/{idClase}")
    public ResponseEntity<?> buscarClase(@PathVariable int idClase) {
        try {
            ClaseColectiva clase = servicio.buscarClase(idClase)
                    .orElseThrow(ClaseColectivaNoEncontrada::new);
            return ResponseEntity.ok(mapeador.toDTO(clase));
        } catch (ClaseColectivaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Clase no encontrada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar la clase");
        }
    }

    @DeleteMapping("/{idClase}/solicitudes/{idSolicitud}")
    public ResponseEntity<?> eliminarSolicitud(@PathVariable int idClase, @PathVariable int idSolicitud) {
        try {
            servicio.eliminarSolicitud(idClase, idSolicitud);
            return ResponseEntity.ok().build();
        } catch (ClaseColectivaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Clase no encontrada");
        } catch (SolicitudNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud no encontrada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la solicitud");
        }
    }

    @PutMapping("/{idClase}")
    public ResponseEntity<?> actualizarClase(@PathVariable int idClase, @RequestBody DClaseColectiva dClaseColectiva) {
        try {
            Optional<ClaseColectiva> claseOptional = servicio.buscarClase(idClase);
            if (claseOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Clase no encontrada");
            }

            ClaseColectiva claseExistente = claseOptional.get();
            claseExistente.setDiaSemana(dClaseColectiva.diaSemana());
            claseExistente.setHoraIni(dClaseColectiva.horaIni());
            claseExistente.setHoraFin(dClaseColectiva.horaFin());

            ClaseColectiva claseActualizada = servicio.actualizarClaseColectiva(claseExistente);
            return ResponseEntity.ok(mapeador.toDTO(claseActualizada));
        } catch (ClaseColectivaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Clase no encontrada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la clase");
        }
    }

    @GetMapping("/{idClase}/solicitudes")
    public ResponseEntity<?> buscarSolicitudes(@PathVariable int idClase) {
        try {
            ClaseColectiva clase = servicio.buscarClase(idClase).orElseThrow(ClaseColectivaNoEncontrada::new);
            List<Solicitud> solicitudes = servicio.buscarSolicitudesPorClase(clase);
            List<DSolicitud> solicitudesDTO = new LinkedList<>();
            for (Solicitud solicitud : solicitudes) {
                solicitudesDTO.add(mapeador.toDTO(solicitud, solicitud.getUsuario().getEmail(), clase.getTipoClase()));
            }
            return ResponseEntity.ok(solicitudesDTO);
        } catch (ClaseColectivaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Clase no encontrada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar las solicitudes");
        }
    }
}
