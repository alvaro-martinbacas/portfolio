package com.tfg.gestion_gimnasios.rest;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import com.tfg.gestion_gimnasios.entidades.*;
import com.tfg.gestion_gimnasios.excepciones.EntrenadorNoEncontrado;
import com.tfg.gestion_gimnasios.repositorios.*;
import com.tfg.gestion_gimnasios.rest.dto.*;
import com.tfg.gestion_gimnasios.servicios.ServicioGimnasio;

@RestController
@RequestMapping("/gestiongimnasios/rutinas")
@CrossOrigin("/localhost:8080")
public class ControladorRutinas {
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
    ServicioGimnasio servicio;
    @Autowired
    Mapeador mapeador;


    @GetMapping("/todas")
    public List<DRutina> buscarTodas(){
        List<Rutina> rutinas = servicio.buscarRutinas();
        List<DRutina> rutinasDTO = new LinkedList<>();

        for (Rutina rutina : rutinas) {
            rutinasDTO.add(mapeador.toDTO(rutina));
        }
        return rutinasDTO;
    }

    @PutMapping("/{idRutina}")
    public void actualizarRutina(@PathVariable int idRutina, @RequestBody DRutina dRutina) {
        Entrenador ent = entrenadorRepo.buscar(dRutina.emailEntrenador()).orElseThrow(EntrenadorNoEncontrado::new);
        Rutina rutina = mapeador.toEntity(dRutina, ent);
        rutina.setId(idRutina);
        servicio.actualizarRutina(rutina);
    }

    @GetMapping("/{id}")
    public DRutina obtenerRutinaPorId(@PathVariable int id) {
        Rutina rutina = servicio.buscarRutinaPorId(id);
        return mapeador.toDTO(rutina);
    }

    @GetMapping("/{id}/ejerciciosRutina")
    public List<DEjercicioRutina> obtenerEjerciciosRutina(@PathVariable int id) {
        List<EjercicioRutina> ejerciciosRutina = servicio.buscarEjerciciosRutinaPorRutina(id);
        List<DEjercicioRutina> ejerciciosRutinaDTO = new LinkedList<>();

        for (EjercicioRutina er : ejerciciosRutina) {
            ejerciciosRutinaDTO.add(mapeador.toDTO(er));
        }
        return ejerciciosRutinaDTO;
    }
}