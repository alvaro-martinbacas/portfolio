package com.tfg.gestion_gimnasios.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tfg.gestion_gimnasios.entidades.Medalla;
import com.tfg.gestion_gimnasios.rest.dto.DMedalla;
import com.tfg.gestion_gimnasios.rest.dto.Mapeador;
import com.tfg.gestion_gimnasios.servicios.ServicioGimnasio;

@RestController
@RequestMapping("/gestiongimnasios/medallas")
@CrossOrigin(origins = "http://localhost:5173")
public class ControladorMedallas {

    @Autowired
    private ServicioGimnasio servicio;
    @Autowired
    private Mapeador mapeador;

    @GetMapping("/todos")
    public List<DMedalla> listarMedallas() {
        List<Medalla> medallas = servicio.buscarMedallas();
        List<DMedalla> medallasDTO = new ArrayList<>();
        for (Medalla medalla : medallas) {
            medallasDTO.add(mapeador.toDTO(medalla));
        }
        return medallasDTO;
    }

    @GetMapping("/tipo/{tipo}")
    public List<DMedalla> listarMedallasPorTipo(@PathVariable String tipo) {
        List<Medalla> medallas = servicio.buscarMedallasPorTipo(tipo);
        List<DMedalla> medallasDTO = new ArrayList<>();
        for (Medalla medalla : medallas) {
            medallasDTO.add(mapeador.toDTO(medalla));
        }
        return medallasDTO;
    }

    @PostMapping
    public ResponseEntity<DMedalla> crearMedalla(@RequestBody DMedalla medallaDTO) {
        Medalla medalla = new Medalla();
        medalla.setNombre(medallaDTO.nombre());
        medalla.setDescripcion(medallaDTO.descripcion());
        medalla.setIcono(medallaDTO.icono());
        medalla.setTipo(medallaDTO.tipo());
        medalla.setObjetivo(medallaDTO.objetivo());
        
        servicio.crearMedalla(medalla);
        return ResponseEntity.ok(mapeador.toDTO(medalla));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMedalla(@PathVariable int id) {
        try {
            Medalla medalla = servicio.buscarMedallaPorId(id);
            if (medalla == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("La medalla especificada no existe");
            }            
            servicio.eliminarMedalla(id);
            return ResponseEntity.ok("Medalla eliminada correctamente");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error al eliminar la medalla: restricción de integridad");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error inesperado al eliminar la medalla");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DMedalla> actualizarMedalla(@PathVariable int id, @RequestBody DMedalla medallaDTO) {
        Medalla medalla = servicio.buscarMedallaPorId(id);

        medalla.setNombre(medallaDTO.nombre());
        medalla.setDescripcion(medallaDTO.descripcion());
        medalla.setIcono(medallaDTO.icono());
        medalla.setTipo(medallaDTO.tipo());
        medalla.setObjetivo(medallaDTO.objetivo());

        servicio.actualizarMedalla(medalla);
        return ResponseEntity.ok(mapeador.toDTO(medalla));
    }
}
