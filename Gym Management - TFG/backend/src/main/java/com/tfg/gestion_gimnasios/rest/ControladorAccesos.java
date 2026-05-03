package com.tfg.gestion_gimnasios.rest;

import com.tfg.gestion_gimnasios.entidades.Acceso;
import com.tfg.gestion_gimnasios.rest.dto.DAcceso;
import com.tfg.gestion_gimnasios.servicios.ServicioGimnasio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gestiongimnasios/accesos")
@CrossOrigin("/localhost:8080")
public class ControladorAccesos {

    @Autowired
    private ServicioGimnasio servicioGimnasio;

    @GetMapping
    public ResponseEntity<List<DAcceso>> buscarAccesosFiltrados(
            @RequestParam(required = false, name = "gimnasioId") Integer idGimnasio,
            @RequestParam(required = false, name = "usuarioEmail") String emailUsuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        List<Acceso> accesos = servicioGimnasio.buscarAccesosFiltrados(
            idGimnasio, emailUsuario, fechaInicio, fechaFin);
        
        List<DAcceso> dtos = accesos.stream()
            .map(DAcceso::of)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas(
            @RequestParam(required = false, name = "gimnasioId") Integer idGimnasio,
            @RequestParam(required = false, name = "usuarioEmail") String emailUsuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        List<Acceso> accesos = servicioGimnasio.buscarAccesosFiltrados(
            idGimnasio, emailUsuario, fechaInicio, fechaFin);
            
        Map<String, Object> stats = servicioGimnasio.calcularEstadisticasAccesos(accesos);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/test/{idGimnasio}")
    public ResponseEntity<Void> generarAccesosTest(
            @PathVariable int idGimnasio,
            @RequestParam(defaultValue = "10") int cantidadPorUsuario) {
        servicioGimnasio.generarAccesosAleatorios(idGimnasio, cantidadPorUsuario);
        return ResponseEntity.ok().build();
    }

}
