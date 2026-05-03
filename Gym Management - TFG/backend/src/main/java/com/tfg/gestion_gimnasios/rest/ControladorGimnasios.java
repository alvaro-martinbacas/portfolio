package com.tfg.gestion_gimnasios.rest;

import com.tfg.gestion_gimnasios.entidades.*;
import com.tfg.gestion_gimnasios.excepciones.*;
import com.tfg.gestion_gimnasios.rest.dto.*;
import com.tfg.gestion_gimnasios.servicios.ServicioGimnasio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gestiongimnasios/gimnasios")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ControladorGimnasios {

    @Autowired
    Mapeador mapeador;

    @Autowired
    ServicioGimnasio servicioGimnasio;


    /**
     * Endpoint para crear un nuevo gimnasio.
     * @param dGimnasio El DTO del gimnasio a crear, debe contener todos los campos necesarios.
     * @return El DTO del gimnasio creado.
     */
    @PostMapping("/")
    public ResponseEntity<?> crearGimnasio(@RequestBody DGimnasio dGimnasio) {
        try {
            Gimnasio gimnasio = servicioGimnasio.nuevoGimnasio(mapeador.toEntity(dGimnasio));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mapeador.toDTO(gimnasio));
        } catch (DataIntegrityViolationException e) {
            String mensaje = e.getMessage().toLowerCase();
            if (mensaje.contains("uk_gimnasio_nombre_direccion")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe un gimnasio con ese nombre en esa dirección");
            } else if (mensaje.contains("uk_gimnasio_telefono")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe un gimnasio con ese teléfono");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear el gimnasio: datos duplicados");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al crear el gimnasio");
        }
    }

    /**
     * Endpoint para buscar un gimnasio por su ID.
     * @param idGimnasio El ID del gimnasio a buscar.
     * @return El DTO del gimnasio encontrado.
     */
    @GetMapping("/{idGimnasio}")
    public ResponseEntity<?> buscarGimnasio(@PathVariable int idGimnasio){
        try {
            Gimnasio gimnasio = servicioGimnasio.buscarGimnasio(idGimnasio);
            if (gimnasio == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El gimnasio especificado no existe");
            }
            return ResponseEntity.ok(mapeador.toDTO(gimnasio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar el gimnasio");
        }
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodosLosGimnasios() {
        try {
            List<Gimnasio> gimnasios = servicioGimnasio.buscarGimnasios();
            List<DGimnasio> gimnasiosDTO = new LinkedList<>();
            for (Gimnasio gimnasio : gimnasios) {
                gimnasiosDTO.add(mapeador.toDTO(gimnasio));
            }
            return ResponseEntity.ok(gimnasiosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar los gimnasios");
        }
    }

    /**
     * Endpoint para obtener todos los usuarios de un gimnasio.
     * @param idGimnasio El ID del gimnasio.
     * @return Lista de DTOs de usuarios pertenecientes al gimnasio.
     */
    @GetMapping("/{idGimnasio}/usuarios")
    public ResponseEntity<?> buscarUsuariosPorGimnasio(@PathVariable int idGimnasio) {
        try {
            Gimnasio gimnasio = servicioGimnasio.buscarGimnasio(idGimnasio);
            if (gimnasio == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El gimnasio especificado no existe");
            }
            List<Usuario> usuarios = gimnasio.usuariosMatriculados();
            List<DUsuario> usuariosDTO = new LinkedList<>();
            for (Usuario usuario : usuarios) {
                usuariosDTO.add(mapeador.toDTO(usuario));
            }
            return ResponseEntity.ok(usuariosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar los usuarios del gimnasio");
        }
    }

    /**
     * Endpoint para actualizar un gimnasio existente.
     * @param idGimnasio El ID del gimnasio a actualizar.
     * @param dGimnasio El DTO del gimnasio con los nuevos datos. Debe contener todos los campos necesarios, excepto el ID, que se toma de la URL.
     * @return El DTO del gimnasio actualizado, o un error 404 si el gimnasio no existe.
     */
    @PutMapping("/{idGimnasio}")
    public ResponseEntity<?> actualizarGimnasio(@PathVariable int idGimnasio, @RequestBody DGimnasio dGimnasio) {
        try {
            Gimnasio gimnasio = servicioGimnasio.buscarGimnasio(idGimnasio);
            if (gimnasio == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El gimnasio especificado no existe");
            }
            DGimnasio dGimnasioConID = new DGimnasio(idGimnasio, dGimnasio.nombre(), dGimnasio.direccion(), dGimnasio.telefono(), dGimnasio.horaApertura(), dGimnasio.horaCierre());
            Gimnasio g = servicioGimnasio.actualizarGimnasio(mapeador.toEntity(dGimnasioConID));
            return ResponseEntity.ok(mapeador.toDTO(g));
        } catch (DataIntegrityViolationException e) {
            String mensaje = e.getMessage().toLowerCase();
            if (mensaje.contains("uk_gimnasio_nombre_direccion")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe otro gimnasio con ese nombre en esa dirección");
            } else if (mensaje.contains("uk_gimnasio_telefono")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe otro gimnasio con ese teléfono");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al actualizar el gimnasio: datos duplicados");
        } catch (GimnasioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El gimnasio especificado no existe");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al actualizar el gimnasio");
        }
    }

    /**
     * Endpoint para eliminar un gimnasio por su ID.
     * @param idGimnasio El ID del gimnasio a eliminar.
     */
    @DeleteMapping("/{idGimnasio}")
    public ResponseEntity<?> eliminarGimnasio(@PathVariable int idGimnasio) {
        try {
            servicioGimnasio.eliminarGimnasio(idGimnasio);
            return ResponseEntity.ok("Gimnasio eliminado correctamente");
        } catch (GimnasioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El gimnasio especificado no existe");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al eliminar el gimnasio");
        }
    }

    
    /**
     * Endpoint para crear un nuevo usuario en un gimnasio.
     * @param idGimnasio El ID del gimnasio al que pertenece el usuario.
     * @param dUsuario El DTO del usuario a crear.
     * @return El DTO del usuario creado.
     */
    @PostMapping("/{idGimnasio}/nuevousuario")
    public ResponseEntity<?> crearUsuario(@PathVariable int idGimnasio, @RequestBody DUsuario dUsuario) {
        try {
            // Validar contraseña antes de procesar
            if (dUsuario.clave() == null || dUsuario.clave().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La contraseña es obligatoria");
            }
            if (dUsuario.clave().length() < 8) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La contraseña debe tener al menos 8 caracteres");
            }
            
            // Validar formato de teléfono
            if (dUsuario.tlf() != null && !dUsuario.tlf().matches("^(\\+34|0034|34)?[6789]\\d{8}$")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El número de teléfono tiene un formato incorrecto");
            }
            
            Gimnasio gimnasio = servicioGimnasio.buscarGimnasio(idGimnasio);
            Usuario usuario = mapeador.nuevaEntidad(dUsuario);
            Usuario nuevoUsuario = servicioGimnasio.registroUsuario(gimnasio, usuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mapeador.toDTO(nuevoUsuario));
        } catch (GimnasioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El gimnasio especificado no existe");
        } catch (Exception e) {
            String mensaje = e.getMessage().toLowerCase();
            if (mensaje.contains("uk_email")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe un usuario con ese email");
            } else if (mensaje.contains("uk_tlf")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe un usuario con ese teléfono");
            } else if (mensaje.contains("validation failed") || mensaje.contains("pattern")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El número de teléfono tiene un formato incorrecto");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear el usuario: " + e.getMessage());
        }
    }


    /**
     * Endpoint para eliminar un usuario por su email.
     * @param email El email del usuario a eliminar.
     */
    @DeleteMapping("/{idGimnasio}/usuarios/{email}")
    @ResponseStatus(HttpStatus.OK)
    public void eliminarUsuario(@PathVariable int idGimnasio, @PathVariable String email) {
        servicioGimnasio.eliminarUsuario(idGimnasio, email);
    }


    /**
     * Endpoint para crear una nueva clase colectiva en un gimnasio.
     * @param idGimnasio El ID del gimnasio al que pertenece la clase colectiva.
     * @param dClaseColectiva El DTO de la clase colectiva a crear.
     * @return El DTO de la clase colectiva creada.
     */
    @PostMapping("/{idGimnasio}/clasescolectivas")
    public ResponseEntity<?> crearClaseColectiva(@PathVariable int idGimnasio, @RequestBody DClaseColectiva dClaseColectiva) {
        try {
            ClaseColectiva clase = servicioGimnasio.nuevaClaseColectiva(idGimnasio, 
                mapeador.toEntity(dClaseColectiva), 
                dClaseColectiva.nombreTipoClase());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mapeador.toDTO(clase));
        } catch (GimnasioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El gimnasio especificado no existe");
        } catch (TipoClaseNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El tipo de clase especificado no existe");
        } catch (HorarioFueraRangoGimnasio e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El horario es incorrecto. La hora de fin debe ser posterior a la hora de inicio y estar dentro del horario del gimnasio");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al crear la clase colectiva");
        }
    }


    @GetMapping("/{idGimnasio}/clasescolectivas")
    public ResponseEntity<?> buscarClasesPorGimnasio(@PathVariable int idGimnasio) {
        try {
            Gimnasio gimnasio = servicioGimnasio.buscarGimnasio(idGimnasio);
            if (gimnasio == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El gimnasio especificado no existe");
            }
            List<ClaseColectiva> clases = gimnasio.clasesDisponibles();
            List<DClaseColectiva> clasesDTO = new LinkedList<>();
            for (ClaseColectiva clase : clases) {
                clasesDTO.add(mapeador.toDTO(clase));
            }
            return ResponseEntity.ok(clasesDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar las clases colectivas del gimnasio");
        }
    }

    @DeleteMapping("/{idGimnasio}/clasescolectivas/{idClase}")
    @ResponseStatus(HttpStatus.OK)
    public void eliminarClase(@PathVariable int idGimnasio, @PathVariable int idClase) {
        servicioGimnasio.eliminarClaseColectiva(idGimnasio, idClase);
    }



    @PostMapping("/{idGimnasio}/nuevoentrenador")
    public ResponseEntity<?> crearEntrenador(@PathVariable int idGimnasio, @RequestBody DEntrenador dEntrenador) {
        try {
            Gimnasio gimnasio = servicioGimnasio.buscarGimnasio(idGimnasio);
            Entrenador entrenador = mapeador.nuevaEntidad(dEntrenador);
            Entrenador nuevoEntrenador = servicioGimnasio.nuevoEntrenador(gimnasio, entrenador);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mapeador.toDTO(nuevoEntrenador));
        } catch (GimnasioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El gimnasio especificado no existe");
        } catch (DataIntegrityViolationException e) {
            String mensaje = e.getMessage().toLowerCase();
            if (mensaje.contains("uk_email")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe un entrenador con ese email");
            } else if (mensaje.contains("uk_tlf")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe un entrenador con ese teléfono");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear el entrenador: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al crear el entrenador");
        }
    }


    @GetMapping("/{idGimnasio}/entrenadores")
    @ResponseStatus(HttpStatus.OK)
    public List<DEntrenador> buscarEntrenadoresPorGimnasio(@PathVariable int idGimnasio) {
        List<Entrenador> entrenadores = servicioGimnasio.buscarEntrenadoresPorGimnasio(idGimnasio);
        // Ordenar: primero activos por nombre, luego inactivos por nombre
        entrenadores.sort((a, b) -> {
            boolean activoA = String.valueOf(a.getActivo()).equalsIgnoreCase("true");
            boolean activoB = String.valueOf(b.getActivo()).equalsIgnoreCase("true");
            if (activoA && !activoB) return -1;
            if (!activoA && activoB) return 1;
            return a.getNombre().compareToIgnoreCase(b.getNombre());
        });
        List<DEntrenador> entrenadoresDTO = new LinkedList<>();
        for (Entrenador entrenador : entrenadores) {
            entrenadoresDTO.add(mapeador.toDTO(entrenador));
        }
        return entrenadoresDTO;
    }



    @PutMapping("/{idGimnasio}/entrenadores/{email}/desactivar")
    public ResponseEntity<?> desactivarEntrenador(@PathVariable int idGimnasio, @PathVariable String email) {
        try {
            servicioGimnasio.desactivarEntrenador(idGimnasio, email);
            return ResponseEntity.ok().build();
        } catch (EntrenadorNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el entrenador especificado");
        } catch (GimnasioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el gimnasio especificado");
        } catch (EntrenadorInactivo e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El entrenador ya está inactivo");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al desactivar el entrenador");
        }
    }

    @PutMapping("/{idGimnasio}/entrenadores/{email}/activar")
    public ResponseEntity<?> activarEntrenador(@PathVariable int idGimnasio, @PathVariable String email) {
        try {
            servicioGimnasio.activarEntrenador(idGimnasio, email);
            return ResponseEntity.ok().build();
        } catch (EntrenadorNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el entrenador especificado");
        } catch (GimnasioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el gimnasio especificado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al activar el entrenador");
        }
    }

    @DeleteMapping("/{idGimnasio}/entrenadores/{email}")
    @ResponseStatus(HttpStatus.OK)
    public void eliminarEntrenador(@PathVariable int idGimnasio, @PathVariable String email) {
        servicioGimnasio.eliminarEntrenador(idGimnasio, email);
    }

    @GetMapping("/{idGimnasio}/solicitudes")
    public ResponseEntity<?> buscarSolicitudesPorGimnasio(@PathVariable int idGimnasio) {
        try {
            Gimnasio gimnasio = servicioGimnasio.buscarGimnasio(idGimnasio);
            if (gimnasio == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El gimnasio especificado no existe");
            }
            List<ClaseColectiva> clases = gimnasio.clasesDisponibles();
            List<DSolicitud> solicitudesDTO = new LinkedList<>();
            for (ClaseColectiva clase : clases) {
                for (Solicitud solicitud : clase.getSolicitudes()) {
                    solicitudesDTO.add(mapeador.toDTO(solicitud, solicitud.getUsuario().getEmail(), clase.getTipoClase()));
                }
            }

            return ResponseEntity.ok(solicitudesDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar las solicitudes del gimnasio");
        }
    }
    
    @PostMapping("/{idGimnasio}/clasesColectivas/{idClase}/nuevasolicitud")
    public ResponseEntity<?> crearSolicitud(@PathVariable int idClase, @RequestBody String emailUsuario, @PathVariable int idGimnasio) {
        try {
            Solicitud solicitud = servicioGimnasio.nuevaSolicitud(idClase, emailUsuario, idGimnasio);
            ClaseColectiva claseColectiva = servicioGimnasio.buscarClase(idClase).orElseThrow();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mapeador.toDTO(solicitud, emailUsuario, claseColectiva.getTipoClase()));
        } catch (ClaseColectivaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("La clase colectiva especificada no existe");
        } catch (UsuarioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario especificado no existe");
        } catch (GimnasioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El gimnasio especificado no existe");
        } catch (SolicitudYaExistente e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe una solicitud para esta clase y usuario");
        } catch (SolicitudDemasiadoPronto e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La solicitud se realizó demasiado pronto. Sólo se permite realizarla el día anterior o el mismo día antes de la clase");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al crear la solicitud");
        }
    }


    /**
     * Endpoint para crear una nueva rutina para un usuario asignada por un entrenador pero sin ejercicios.
     * @param idGimnasio El ID del gimnasio.
     * @param emailEntrenador El email del entrenador.
     * @param emailUsuario El email del usuario.
     * @param dRutina El DTO de la rutina a crear.
     * @return El DTO de la rutina creada.
     */
    @PostMapping("/{idGimnasio}/usuarios/{emailUsuario}/rutinas")
    public ResponseEntity<?> crearRutina(
        @PathVariable int idGimnasio,
        @PathVariable String emailUsuario,
        @RequestBody DRutina dRutina) {
        try {
            String emailEntrenador = dRutina.emailEntrenador();
            
            // Crear la rutina manualmente sin usar el mapeador
            Rutina rutina = new Rutina(
                0, // El ID se asignará automáticamente
                dRutina.nombre(),
                dRutina.descripcion()
            );
            
            Rutina rutinaCreada = servicioGimnasio.nuevaRutina(emailEntrenador, emailUsuario, idGimnasio, rutina);
            
            // Devolver el DTO con los datos correctos
            DRutina dRutinaCreada = new DRutina(
                rutinaCreada.getId(),
                rutinaCreada.getNombre(),
                rutinaCreada.getDescripcion(),
                rutinaCreada.getEntrenador() != null ? rutinaCreada.getEntrenador().getEmail() : null,
                rutinaCreada.getUsuario() != null ? rutinaCreada.getUsuario().getEmail() : emailUsuario
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dRutinaCreada);
        } catch (GimnasioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El gimnasio especificado no existe");
        } catch (UsuarioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario no existe");
        } catch (EntrenadorNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El entrenador no existe");
        } catch (EntrenadorInactivo e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El entrenador está inactivo y no puede crear rutinas");
        } catch (RutinaYaExistente e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe una rutina con ese nombre para este usuario");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la rutina");
        }
    }


    @PostMapping("/{idGimnasio}/accesos/entrada")
    public ResponseEntity<?> registrarEntrada(@PathVariable int idGimnasio, Principal principal) {
        try {
            String emailUsuario = principal.getName();
            Acceso acceso = servicioGimnasio.registrarEntrada(idGimnasio, emailUsuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mapeador.toDTO(acceso));
        } catch (GimnasioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El gimnasio especificado no existe");
        } catch (UsuarioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario no existe");
        } catch (UsuarioNoPerteneceAGimnasio e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("El usuario no está registrado en este gimnasio");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar la entrada");
        }
    }

    @PostMapping("/{idGimnasio}/accesos/salida")
    public ResponseEntity<?> registrarSalida(@PathVariable int idGimnasio, Principal principal) {
        try {
            String emailUsuario = principal.getName();
            Acceso acceso = servicioGimnasio.registrarSalida(idGimnasio, emailUsuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mapeador.toDTO(acceso));
        } catch (GimnasioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El gimnasio especificado no existe");
        } catch (UsuarioNoEncontrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario no existe");
        } catch (UsuarioNoHaAccedidoGimnasio e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No hay un acceso abierto para registrar la salida");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar la salida");
        }
    }

    @GetMapping("/{idGimnasio}/accesos")
    public ResponseEntity<?> obtenerAccesosPorGimnasio(@PathVariable int idGimnasio) {
        try {
            Gimnasio gimnasio = servicioGimnasio.buscarGimnasio(idGimnasio);
            if (gimnasio == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El gimnasio especificado no existe");
            }
            List<Acceso> accesos = gimnasio.historialAccesos();
            List<DAcceso> accesosDTO = new LinkedList<>();
            for (Acceso acceso : accesos) {
                accesosDTO.add(mapeador.toDTO(acceso));
            }
            return ResponseEntity.ok(accesosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los accesos del gimnasio");
        }
    }
    @GetMapping("/{idGimnasio}/accesos-sin-usuario")
    public ResponseEntity<?> obtenerAccesosSinUsuario(@PathVariable int idGimnasio) {
        try {
            Gimnasio gimnasio = servicioGimnasio.buscarGimnasio(idGimnasio);
            if (gimnasio == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El gimnasio especificado no existe");
            }
            List<Acceso> accesos = gimnasio.historialAccesos();
            List<DAccesoNoConfidencial> accesosDTO = new LinkedList<>();
            for (Acceso acceso : accesos) {
                accesosDTO.add(mapeador.toDTONoConfidencial(acceso));
            }
            return ResponseEntity.ok(accesosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los accesos sin usuario del gimnasio");
        }
    }

    @GetMapping("/{idGimnasio}/aforo")
    public ResponseEntity<?> consultarAforo(
            @PathVariable Long idGimnasio,
            @RequestParam String fechaHora) {
        try {
            if (servicioGimnasio.buscarGimnasio(idGimnasio.intValue()) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El gimnasio especificado no existe");
            }
            
            LocalDateTime momento;
            try {
                momento = LocalDateTime.parse(fechaHora);
            } catch (DateTimeParseException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El formato de la fecha y hora no es válido. Debe ser yyyy-MM-ddTHH:mm:ss");
            }
            
            int aforo = servicioGimnasio.consultarAforo(idGimnasio, momento);
            return ResponseEntity.ok(Map.of("aforo", aforo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Parámetros inválidos para la consulta de aforo");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar el aforo del gimnasio");
        }
    }

    @GetMapping("/{idGimnasio}/total-medallas")
    public ResponseEntity<?> totalMedallasOtorgadas(@PathVariable int idGimnasio) {
        try {
            if (servicioGimnasio.buscarGimnasio(idGimnasio) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El gimnasio especificado no existe");
            }
            int totalMedallas = servicioGimnasio.totalMedallasGimnasio(idGimnasio);
            return ResponseEntity.ok(Map.of("totalMedallas", totalMedallas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el total de medallas del gimnasio");
        }
    }

    @PostMapping("/{idGimnasio}/verificacion-medallas-todos")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> verificarMedallasTodosUsuarios(@PathVariable int idGimnasio) {
        try {
            List<Usuario> usuarios = servicioGimnasio.buscarUsuariosPorGimnasio(idGimnasio);
            int totalUsuarios = usuarios.size();
            int usuariosActualizados = 0;

            for (Usuario usuario : usuarios) {
                try {
                    servicioGimnasio.verificarYAsignarTodasLasMedallas(usuario.getEmail());
                    usuariosActualizados++;
                } catch (Exception e) {
                    // Registrar error pero continuar con el siguiente usuario
                    System.err.println("Error al verificar medallas para usuario " + usuario.getEmail() + ": " + e.getMessage());
                }
            }

            return ResponseEntity.ok(Map.of(
                "mensaje", "Verificación de medallas completada",
                "total_usuarios", totalUsuarios,
                "usuarios_actualizados", usuariosActualizados
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al verificar medallas", "detalle", e.getMessage()));
        }
    }

    @GetMapping("/{idGimnasio}/medallas/{idMedalla}/usuarios")
    public ResponseEntity<?> obtenerUsuariosConMedalla(@PathVariable int idGimnasio, @PathVariable int idMedalla) {
        try {
            Gimnasio gimnasio = servicioGimnasio.buscarGimnasio(idGimnasio);
            if (gimnasio == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El gimnasio especificado no existe");
            }
            List<Usuario> usuarios = servicioGimnasio.usuariosGimnasioConMedalla(idGimnasio, idMedalla);
            List<DUsuario> usuariosDTO = new LinkedList<>();
            for (Usuario usuario : usuarios) {
                usuariosDTO.add(mapeador.toDTO(usuario));
            }
            return ResponseEntity.ok(usuariosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los usuarios con la medalla especificada");
        }
    }

    

}