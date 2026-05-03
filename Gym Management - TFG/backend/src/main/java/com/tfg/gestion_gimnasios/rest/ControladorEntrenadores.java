package com.tfg.gestion_gimnasios.rest;


import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tfg.gestion_gimnasios.entidades.*;
import com.tfg.gestion_gimnasios.excepciones.*;
import com.tfg.gestion_gimnasios.repositorios.*;
import com.tfg.gestion_gimnasios.rest.dto.*;
import com.tfg.gestion_gimnasios.servicios.ServicioGimnasio;


@RestController
@RequestMapping("/gestiongimnasios/entrenadores")
public class ControladorEntrenadores {

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
    
    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public DEntrenador buscarEntrenador(@PathVariable String email) {
        Entrenador entrenador = servicio.buscarEntrenador(email).orElseThrow(EntrenadorNoEncontrado::new);
        return mapeador.toDTO(entrenador);
    }

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DEntrenador> actualizarEntrenador(@PathVariable String email, @RequestBody DEntrenador dEntrenador) {
        if (servicio.buscarEntrenador(email).isPresent()) {
            try {
                DEntrenador dEnt = new DEntrenador(
                    dEntrenador.nombre(),
                    email,
                    dEntrenador.clave(), 
                    dEntrenador.tlf(),
                    dEntrenador.activo()
                );
                Entrenador e = servicio.actualizarEntrenador(mapeador.nuevaEntidad(dEnt));
                return ResponseEntity.status(HttpStatus.OK).body(mapeador.toDTO(e));
            } catch (EntrenadorNoEncontrado e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }




    @GetMapping("/{email}/rutinas")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<DRutina>> buscarRutinasPorEntrenador(@PathVariable String email) {
        if (servicio.buscarEntrenador(email).isPresent()) {
            List<Rutina> rutinas = servicio.buscarRutinasPorEntrenador(email);
            List<DRutina> dRutinas = rutinas.stream()
                .map(r -> new DRutina(
                    r.getId(),
                    r.getNombre(),
                    r.getDescripcion(),
                    r.getEntrenador() != null ? r.getEntrenador().getEmail() : null,
                    r.getUsuario() != null ? r.getUsuario().getEmail() : null))
                .toList();
            return ResponseEntity.status(HttpStatus.OK).body(dRutinas);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{email}/gimnasio")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DGimnasio> buscarGimnasioPorEntrenador(@PathVariable String email) {
        Entrenador entrenador = servicio.buscarEntrenador(email).orElseThrow(EntrenadorNoEncontrado::new);
        for (Gimnasio gimnasio : gimnasioRepo.buscarTodos()) {
            if (gimnasio.entrenadores().contains(entrenador)) {
                return ResponseEntity.status(HttpStatus.OK).body(mapeador.toDTO(gimnasio));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

        /**
     * Endpoint para añadir un ejercicioRutina a una rutina existente.
     * @param emailEntrenador El email del entrenador.
     * @param idRutina El ID de la rutina.
     * @param idEjercicio El ID del ejercicio.
     * @param dEjercicioRutina El DTO del ejercicioRutina a añadir.
     * @return El DTO del ejercicioRutina añadido.
     */
    @PostMapping("/{emailEntrenador}/rutinas/{idRutina}/ejerciciosRutina")
    @ResponseStatus(HttpStatus.CREATED)
    public DEjercicioRutina aniadirEjercicioRutina(@PathVariable String emailEntrenador, @PathVariable int idRutina, @RequestBody DAnadirEjercicioARutina dEjercicioRutina) {
        EjercicioRutina ejercicioRutina = mapeador.toEntity(dEjercicioRutina.ejercicioRutina());
        int idEjercicio = dEjercicioRutina.idEjercicio();
        EjercicioRutina nuevoEjercicioRutina = servicio.aniadirEjercicioRutina(emailEntrenador, idRutina, ejercicioRutina, idEjercicio);
        return mapeador.toDTO(nuevoEjercicioRutina);
    }

    
    @PutMapping("/{emailEntrenador}/rutinas/{idRutina}/ejerciciosRutina/{idEjercicioRutina}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DEjercicioRutina> actualizarEjercicioRutina(@PathVariable String emailEntrenador, @PathVariable int idRutina, @PathVariable int idEjercicioRutina, @RequestBody DEjercicioRutina dEjercicioRutina) {
        EjercicioRutina er = servicio.buscarEjercicioRutina(idEjercicioRutina).orElseThrow(EjercicioRutinaNoEncontrado::new);

        er.setSeries(dEjercicioRutina.series());
        er.setRepeticiones(dEjercicioRutina.repeticiones());
        er.setDescanso(dEjercicioRutina.descanso());
        er.setIndicaciones(dEjercicioRutina.indicaciones());
        
        EjercicioRutina erActualizado = servicio.actualizarEjercicioRutina(emailEntrenador, idRutina, er);

        return ResponseEntity.status(HttpStatus.OK).body(mapeador.toDTO(erActualizado));
    }
    @DeleteMapping("/{emailEntrenador}/rutinas/{idRutina}")
    @ResponseStatus(HttpStatus.OK)
    public void eliminarRutina(@PathVariable int idRutina, @PathVariable String emailEntrenador) {
        servicio.eliminarRutina(idRutina, emailEntrenador);
    }
    /**
     * Endpoint para eliminar un ejercicioRutina de una rutina.
     * @param emailEntrenador El email del entrenador.
     * @param idRutina El ID de la rutina.
     * @param idEjercicioRutina El ID del ejercicioRutina a eliminar.
     */
    @DeleteMapping("/{emailEntrenador}/rutinas/{idRutina}/ejerciciosRutina/{idEjercicioRutina}")
    @ResponseStatus(HttpStatus.OK)
    public void eliminarEjercicioRutina(
        @PathVariable String emailEntrenador,
        @PathVariable int idRutina,
        @PathVariable int idEjercicioRutina) {
        servicio.eliminarEjercicioRutina(emailEntrenador, idRutina, idEjercicioRutina);
    }

    @GetMapping("/{emailEntrenador}/clases")
    @ResponseStatus(HttpStatus.OK)
    public List<DClaseColectiva> clasesPorEntrenador(@PathVariable String emailEntrenador) {
        List<ClaseColectiva> clases = servicio.clasesPorEntrenador(emailEntrenador);
        List<DClaseColectiva> clasesDTO = new LinkedList<>();
        for (ClaseColectiva c : clases) {
            clasesDTO.add(mapeador.toDTO(c));
        }
        return clasesDTO;
    }

}
