package com.tfg.gestion_gimnasios.rest;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tfg.gestion_gimnasios.entidades.*;
import com.tfg.gestion_gimnasios.excepciones.*;
import com.tfg.gestion_gimnasios.repositorios.*;
import com.tfg.gestion_gimnasios.rest.dto.*;
import com.tfg.gestion_gimnasios.servicios.ServicioGimnasio;



@RestController
@RequestMapping("/gestiongimnasios/usuarios")
public class ControladorUsuarios {

    @Autowired
    private ServicioGimnasio servicio;
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
    public DUsuario buscarUsuario(@PathVariable String email) {
        return servicio.buscarUsuario(email)
                .map(u -> new DUsuario(u.getNombre(), u.getApellidos(), u.getTlf(), u.getEmail(), u.getClave(), u.getCuotaPagada(), u.getCuotaValidaHasta()))
                .orElseThrow(() -> new UsuarioNoEncontrado());
    }

    @PutMapping("/{email}")
    public ResponseEntity<DUsuario> actualizarUsuario(@PathVariable String email, @RequestBody DUsuario dUsuario) {
        if (servicio.buscarUsuario(email).isPresent()) {
            if (dUsuario.email() != null && !dUsuario.email().equals(email)) {
                throw new EmailNoPuedeSerActualizado();
            }
            try {
                DUsuario dUsu = new DUsuario(
                    dUsuario.nombre(),
                    dUsuario.apellidos(),
                    dUsuario.tlf(),
                    email,
                    dUsuario.clave(),
                    dUsuario.cuotaPagada(),
                    dUsuario.cuotaValidaHasta()
                );
                Usuario u = servicio.actualizarUsuario(mapeador.nuevaEntidad(dUsu));
                return ResponseEntity.ok(mapeador.toDTO(u));
            } catch (UsuarioNoEncontrado e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{email}/cuota")
    public DUsuario pagarCuota(@PathVariable String email, @RequestBody int meses) {
        if (meses < 0) {
            throw new MesesDebeSerMayorQueCero();
        }
        Usuario usuarioActualizado = servicio.pagarCuota(email);
        return mapeador.toDTO(usuarioActualizado);
    }

    @GetMapping("/{emailUsuario}/accesos")
    public List<DAcceso> obtenerAccesos(@PathVariable String emailUsuario) {
        Usuario usuario = servicio.buscarUsuario(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontrado());
        List<DAcceso> accesosDto = new ArrayList<>();
        for (Acceso acceso : usuario.historialAccesos()) {
            accesosDto.add(new DAcceso(acceso.getId(), acceso.getHoraEntrada(), acceso.getHoraSalida()));
        }
        return accesosDto;
    }


    /**
     * Endpoint para consultar la racha actual de días seguidos de asistencia de un usuario.
     * Solo el propio usuario o el admin pueden usarlo.
     */
    @GetMapping("/{emailUsuario}/racha-dias-seguidos")
    public int getRachaDiasSeguidos(@PathVariable String email, Principal principal) {
        // Puedes añadir comprobación de seguridad aquí si lo deseas
        return servicio.calcularDiasSeguidos(email);
    }

    @PostMapping("/{emailUsuario}/medalla/{idMedalla}")
    public void darMedalla(@PathVariable int idMedalla, @PathVariable String emailUsuario) {
        servicio.otorgarMedallaAUsuario(emailUsuario, idMedalla);
    }

    /**
     * Endpoint para consultar las medallas actuales del usuario.
     * Solo el propio usuario o el admin pueden usarlo.
     */
    @GetMapping("/{emailUsuario}/medallas")
    public List<DMedalla> getMedallasUsuario(@PathVariable String emailUsuario) {
        List<Medalla> medallas = servicio.buscarMedallasPorUsuario(emailUsuario);
        List<DMedalla> medallasDto = new ArrayList<>();
        for (Medalla medalla : medallas) {
            medallasDto.add(mapeador.toDTO(medalla));
        }
        return medallasDto;
    }
    @GetMapping("/{emailUsuario}/gimnasio")
    public DGimnasio getGimnasioUsuario(@PathVariable String emailUsuario) {
        if (servicio.buscarUsuario(emailUsuario).isEmpty()) {
            throw new UsuarioNoEncontrado();
        }
        Gimnasio gimnasio = servicio.obtenerGimnasioPorUsuario(emailUsuario).orElseThrow();
        return mapeador.toDTO(gimnasio);
    }
    @GetMapping("/todos")
    public List<DUsuario> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepo.findAll();
        usuarios.sort((u1, u2) -> u1.getApellidos().compareToIgnoreCase(u2.getApellidos()));
        List<DUsuario> usuariosDto = new ArrayList<>();
        for (Usuario u : usuarios) {
            usuariosDto.add(mapeador.toDTO(u));
        }
        return usuariosDto;
    }

    @GetMapping("/{emailUsuario}/rutinas")
    public List<DRutina> buscarRutinasPorUsuario(@PathVariable String emailUsuario) {
        List<Rutina> rutinas = usuarioRepo.buscarRutinasPorUsuario(emailUsuario);
    return rutinas.stream()
        .map(r -> new DRutina(
            r.getId(),
            r.getNombre(),
            r.getDescripcion(),
            r.getEntrenador() != null ? r.getEntrenador().getEmail() : null,
            r.getUsuario() != null ? r.getUsuario().getEmail() : null))
        .toList();
    }
    @GetMapping("/{emailUsuario}/solicitudes")
    public List<DSolicitud> buscarSolicitudesPorUsuario(@PathVariable String emailUsuario) {
        List<Solicitud> solicitudes = servicio.buscarSolicitudesPorUsuario(emailUsuario);
        List<DSolicitud> solicitudesDTO = new ArrayList<>();
        for (Solicitud solicitud : solicitudes) {
            ClaseColectiva clase = servicio.claseDeSolicitud(solicitud.getId());
            solicitudesDTO.add(mapeador.toDTO(solicitud, emailUsuario, clase.getTipoClase()));
        }
        return solicitudesDTO;
    }
    @PostMapping("/generar-test/{idGimnasio}")
    public ResponseEntity<Void> generarUsuariosTest(
            @PathVariable int idGimnasio,
            @RequestParam(defaultValue = "10") int cantidad) {
        servicio.generarUsuariosAleatorios(idGimnasio, cantidad);
        return ResponseEntity.ok().build();
    }

    // Devuelve el número de medallas de un usuario
    @GetMapping("/{emailUsuario}/total")
    public int medallasUsuario(@PathVariable String emailUsuario) {
        return servicio.totalMedallasUsuario(emailUsuario);
    }

    @GetMapping("/{emailUsuario}/lista-medallas")
    public List<DMedalla> listarMedallasUsuario(@PathVariable String emailUsuario) {
        List<Medalla> medallas = servicio.buscarMedallasPorUsuario(emailUsuario);
        List<DMedalla> medallasDto = new ArrayList<>();
        for (Medalla medalla : medallas) {
            medallasDto.add(mapeador.toDTO(medalla));
        }
        return medallasDto;
    }
    @GetMapping("/{emailUsuario}/accesos/ultimo")
    public ResponseEntity<DAcceso> getUltimoAcceso(@PathVariable String emailUsuario) {
        Acceso acceso = servicio.buscarUltimoAccesoUsuario(emailUsuario);
        if (acceso == null) {
            // Si no hay ningún acceso previo, devolver un DAcceso vacío
            return ResponseEntity.ok(new DAcceso(0, null, null));
        }
        return ResponseEntity.ok(mapeador.toDTO(acceso));
    }

    /**
     * Devuelve el número de veces que un usuario ha asistido al gimnasio (accesos registrados)
     */
    @GetMapping("/{emailUsuario}/num-accesos")
    public int getNumeroAccesos(@PathVariable String emailUsuario) {
        Usuario usuario = servicio.buscarUsuario(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontrado());
        return usuario.historialAccesos().size();
    }

    @GetMapping("/{email}/proximas-clases")
    public List<Map<String, Object>> getProximasClases(@PathVariable String email) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        // Buscar todas las clases colectivas
        List<ClaseColectiva> clases = claseRepo.buscarTodas();
        for (ClaseColectiva clase : clases) {
            for (Solicitud solicitud : clase.getSolicitudes()) {
                if (solicitud.getUsuario().getEmail().equals(email)
                    && Boolean.TRUE.equals(solicitud.getConfReserva())
                    && solicitud.getFechaSolicitud() != null
                    && solicitud.getFechaSolicitud().isAfter(hoy.minusDays(1))) {

                    DayOfWeek diaSemanaClase = clase.getDiaSemana();
                    LocalDate fechaProxima = hoy;
                    while (fechaProxima.getDayOfWeek() != diaSemanaClase) {
                        fechaProxima = fechaProxima.plusDays(1);
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("solicitudId", solicitud.getId());
                    map.put("nombreClase", clase.getTipoClase());
                    map.put("horaInicio", clase.getHoraIni().toString());
                    map.put("horaFin", clase.getHoraFin().toString());
                    map.put("fecha", fechaProxima.toString());
                    result.add(map);
                }
            }
        }
        return result;
    }

}