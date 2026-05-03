package com.tfg.gestion_gimnasios.servicios;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import com.tfg.gestion_gimnasios.entidades.*;
import com.tfg.gestion_gimnasios.excepciones.*;
import com.tfg.gestion_gimnasios.repositorios.*;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.PersistenceException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;




@Service
@Validated
public class ServicioGimnasio {
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
    RepositorioMedalla medallaRepo;
    @Autowired
    RepositorioTipoClase tipoClaseRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ServicioGimnasio() {
    }

    @PostConstruct
    public void crearAdminGlobal() {
        
        String adminEmail = "admin@gimnasio.com";
        String adminClave = "admin1234";
        if (usuarioRepo.buscar(adminEmail).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setEmail(adminEmail);
            admin.setApellidos("-");
            admin.setClave(passwordEncoder.encode(adminClave));
            admin.setNombre("Administrador Global");
            admin.setTlf("700000000");
            admin.setCuotaPagada(true);
            usuarioRepo.guardar(admin);
        }
    }

    /**
     * Crea un nuevo gimnasio en el sistema.
     * @param gimnasio El gimnasio a crear, debe ser válido.
     */
    public Gimnasio nuevoGimnasio(@Valid Gimnasio gimnasio){

        if (gimnasioRepo.buscar(gimnasio.getId()).isPresent()) {
            throw new GimnasioYaExistente();
        }
        if (gimnasioRepo.buscarPorNombre(gimnasio.getNombre()).isPresent()) {
            throw new GimnasioYaExistente();
        }
        if (gimnasioRepo.buscarPorDireccion(gimnasio.getDireccion()).isPresent()) {
            throw new GimnasioYaExistente();
        }
        gimnasioRepo.guardar(gimnasio);
        return gimnasio;
    }

    /**
     * Busca un gimnasio por su ID.
     * @param id del gimnasio a buscar
     * @return Un Optional que contiene el gimnasio si se encuentra, o vacío si no.
     */

    public Gimnasio buscarGimnasio(int id) {
        return gimnasioRepo.buscar(id).orElseThrow(GimnasioNoEncontrado::new);
    }
    
    public List<Gimnasio> buscarGimnasios() {
        return gimnasioRepo.buscarTodos();
    }

    public Gimnasio actualizarGimnasio(@Valid Gimnasio datos){
        Gimnasio existente = gimnasioRepo.buscar(datos.getId())
            .orElseThrow(GimnasioNoEncontrado::new);

        existente.setNombre(datos.getNombre());
        existente.setDireccion(datos.getDireccion());
        existente.setTelefono(datos.getTelefono());
        existente.setHoraApertura(datos.getHoraApertura());
        existente.setHoraCierre(datos.getHoraCierre());

        gimnasioRepo.actualizar(existente);
        return existente;
    }


    /**
     * Elimina un gimnasio del sistema.
     * @param gimnasio El gimnasio a eliminar, debe ser válido y existir en el sistema.
     */
    public void eliminarGimnasio(@Valid int idGimnasio) {
        if (gimnasioRepo.buscar(idGimnasio).isEmpty()) {
            throw new GimnasioNoEncontrado();
        }
        gimnasioRepo.eliminar(idGimnasio);
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * @param usuario El usuario a crear, debe ser válido.
     * @return El usuario creado.
     */
    public Usuario registroUsuario(@Valid Gimnasio gimnasio, @Valid Usuario usuario) {
        if ("admin@gimnasio.es".equals(usuario.getEmail()) || "Admin".equals(usuario.getNombre())) {
            throw new ValoresReservadosParaDireccion();
        }
        if (usuarioRepo.buscar(usuario.getEmail()).isPresent()) {
            throw new UsuarioYaExiste();
        }
        if (usuarioRepo.buscarPorTLF(usuario.getTlf()).isPresent()) {
            throw new UsuarioYaExiste();
        }

        var g = gimnasioRepo.buscar(gimnasio.getId()).orElseThrow(GimnasioNoEncontrado::new);
        g.aniadirUsuario(usuario);
        usuarioRepo.guardar(usuario);
        return usuario;
    }

    /**
     * Busca un usuario por su email.
     * @param email del usuario a buscar
     * @return Un Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    public Optional<Usuario> buscarUsuario(String email) {
        return usuarioRepo.buscar(email);
    }

    /**
     * Busca un usuario por su número de teléfono.
     * @param tlf del usuario a buscar
     * @return Un Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    public Optional<Usuario> buscarUsuarioPorTlf(String tlf) {
        return usuarioRepo.buscarPorTLF(tlf);
    }   

    public List<Usuario> buscarUsuariosPorGimnasio(int idGimnasio) {
        Gimnasio gimnasio = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        return gimnasio.usuariosMatriculados();
    }
    /**
     * Actualiza un usuario existente en el sistema.
     * @param usuario El usuario a actualizar, debe ser válido y existir en el sistema.
     * @return El usuario actualizado.
     */
    public Usuario actualizarUsuario(@Valid Usuario usuario) {
        if (usuarioRepo.buscar(usuario.getEmail()).isEmpty()) {
            throw new UsuarioNoEncontrado();
        }
        usuarioRepo.actualizar(usuario);
        return usuario;
    }

    /**
     * Elimina un usuario del sistema.
     * @param email El email del usuario a eliminar, debe existir en el sistema.
     */
    public void eliminarUsuario(int idGimnasio, String email) {
        var g = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);

        g.eliminarUsuario(email);
        usuarioRepo.eliminar(email);
    }

    /**
     * Método que se ejecuta mensualmente a las 00 am para actualizar las cuotas de los usuarios.
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void tareaActualizacionCuotas() {
        for (Usuario usuario : usuarioRepo.usuariosCuotaCaducaHoy()) {
            usuario.resetCuota();
            usuarioRepo.actualizar(usuario);
        }
    }

    /**
     * Método para aumentar la cuota de un usuario por un número determinado de meses.
     * @param email del usuario al que se le va a aumentar la cuota.
     * @param meses Número de meses por los que se va a aumentar la cuota.
     * @return El usuario actualizado con la nueva cuota.
     */
    public Usuario pagarCuota(String email) {
        if (usuarioRepo.buscar(email).isEmpty()) {
            throw new UsuarioNoEncontrado();
        }
        Usuario usuario = usuarioRepo.buscar(email).get();
        usuario.pagarCuota();
        usuarioRepo.actualizar(usuario);
        return usuario;
    }

    /**
     * Método para crear una nueva clase colectiva en el sistema.
     * @param gimnasio El gimnasio al que pertenece la clase colectiva.
     * @param claseColectiva La clase colectiva a crear, debe ser válida.
     * @return La clase colectiva creada.
     */
    public ClaseColectiva nuevaClaseColectiva(@Valid int idGimnasio, @Valid ClaseColectiva claseColectiva, String nombreTipoClase) {
        var g = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        TipoClase tc = tipoClaseRepo.buscar(nombreTipoClase).orElseThrow(TipoClaseNoEncontrado::new);
        
        if (!g.horarioPermitido(claseColectiva.getHoraIni(), claseColectiva.getHoraFin())) {
            throw new HorarioFueraRangoGimnasio();
        }

        claseColectiva.aniadirTipoClase(tc);
        claseRepo.guardar(claseColectiva);
        g.aniadirClaseColectiva(claseColectiva);
        gimnasioRepo.actualizar(g);
        return claseColectiva;
    }

    public Optional<ClaseColectiva> buscarClase(int id){
        return claseRepo.buscar(id);
    }

    public List<ClaseColectiva> buscarClasesPorGimnasio(int idGimnasio) {
        Gimnasio gimnasio = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        return gimnasio.clasesDisponibles();
    }
    /**
     * Actualiza una clase colectiva existente en el sistema.
     * @param claseColectiva La clase colectiva a actualizar, debe ser válida y existir en el sistema.
     * @return La clase colectiva actualizada.
     */
    public ClaseColectiva actualizarClaseColectiva(@Valid ClaseColectiva claseColectiva) {
        if (claseRepo.buscar(claseColectiva.getId()).isEmpty()) {
            throw new ClaseColectivaNoEncontrada();
        }
        claseRepo.actualizar(claseColectiva);
        return claseColectiva;
    }

    /**
     * Elimina una clase colectiva del sistema.
     * @param idClaseColectiva El id de la clase colectiva a eliminar, debe existir en el sistema.
     */
    public void eliminarClaseColectiva(int idGimnasio, int idClaseColectiva) {
        if (claseRepo.buscar(idClaseColectiva).isEmpty()) {
            throw new ClaseColectivaNoEncontrada();
        }
        var g = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        g.eliminarClaseColectiva(idClaseColectiva);
        claseRepo.eliminar(idClaseColectiva);
        gimnasioRepo.actualizar(g);
    }


    public Entrenador nuevoEntrenador(@Valid Gimnasio gimnasio, @Valid Entrenador entrenador) {
        if (entrenadorRepo.buscar(entrenador.getEmail()).isPresent()) {
            if (entrenadorRepo.buscar(entrenador.getEmail()).get().getActivo()) {
                throw new EntrenadorYaExiste();
            } else {
                // Si el entrenador ya existe pero está inactivo, lo reactivamos
                entrenador.setActivo(true);
                entrenadorRepo.actualizar(entrenador);
                return entrenador;
            }
        }
        if(gimnasioRepo.buscar(gimnasio.getId()).isEmpty()) {
            throw new GimnasioNoEncontrado();
        }
        if (entrenadorRepo.buscarPorTLF(entrenador.getTlf()).isPresent()) {
            throw new EntrenadorYaExiste();
        }
        gimnasio.aniadirEntrenador(entrenador);
        entrenadorRepo.guardar(entrenador);
        gimnasioRepo.actualizar(gimnasio);
        return entrenador;
    }

    public Optional<Gimnasio> obtenerGimnasioPorUsuario(String emailUsuario) {
        usuarioRepo.buscar(emailUsuario).orElseThrow(UsuarioNoEncontrado::new);
        return gimnasioRepo.buscarPorUsuario(emailUsuario);
    }

    public Optional<Entrenador> buscarEntrenador(String email) {
        Entrenador entrenador = entrenadorRepo.buscar(email).orElseThrow(EntrenadorNoEncontrado::new);
        if (!entrenador.getActivo()) {
            throw new EntrenadorInactivo();
        }
        return entrenadorRepo.buscar(email);
    }

    public List<Entrenador> buscarEntrenadoresPorGimnasio(int idGimnasio){
        Gimnasio gimnasio = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        List<Entrenador> entrenadores = gimnasio.entrenadores();
        return entrenadores;
    }

    public Entrenador actualizarEntrenador(@Valid Entrenador entrenador) {
        if (entrenadorRepo.buscar(entrenador.getEmail()).isEmpty()) {
            throw new EntrenadorNoEncontrado();
        }

        if (!entrenador.getActivo())
            throw new EntrenadorInactivo();
        
        entrenadorRepo.actualizar(entrenador);
        return entrenador;
    }



    public List<Entrenador> buscarEntrenadoresTrabajandoPorGimnasio(int idGimnasio) {
        Gimnasio gimnasio = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        return gimnasio.entrenadoresTrabajando();
    }

    /**
     * Desactiva un entrenador en el sistema.
     * Este método cambia un entrenador a inactivo y actualiza las clases colectivas
     * @param idGimnasio ID del gimnasio al que pertenece el entrenador.
     * @param email Email del entrenador a eliminar.
     */
    public void desactivarEntrenador(int idGimnasio, String email) {
        if (entrenadorRepo.buscar(email).isEmpty()) {
            throw new EntrenadorNoEncontrado();
        }

        if (!entrenadorRepo.buscar(email).get().getActivo()) {
            throw new EntrenadorInactivo();
        }

        var g = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        
        List<ClaseColectiva> clasesImpartidas = gimnasioRepo.buscarClasesPorGimnasio(idGimnasio);
        for (ClaseColectiva clase : clasesImpartidas) {
            if (clase.eliminarEntrenadorSiCoincide(email)) {
                claseRepo.actualizar(clase);
            }
        }
        
        Entrenador e = entrenadorRepo.buscar(email).orElseThrow(EntrenadorNoEncontrado::new);
        g.desactivarEntrenador(email);
        entrenadorRepo.actualizar(e);
    }

    public void activarEntrenador(int idGimnasio, String email){
        if (entrenadorRepo.buscar(email).isEmpty()) {
            throw new EntrenadorNoEncontrado();
        }

        Entrenador e = entrenadorRepo.buscar(email).orElseThrow(EntrenadorNoEncontrado::new);
        if (e.getActivo()) {
            throw new EntrenadorYaActivo();
        }

        var g = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        g.activarEntrenador(email);
        entrenadorRepo.actualizar(e);

    }

    public void eliminarEntrenador(int idGimnasio, String email){
        if (entrenadorRepo.buscar(email).isEmpty()) {
            throw new EntrenadorNoEncontrado();
        }
        var g = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        
        List<ClaseColectiva> clasesImpartidas = gimnasioRepo.buscarClasesPorGimnasio(idGimnasio);
        for (ClaseColectiva clase : clasesImpartidas) {
            if (clase.eliminarEntrenadorSiCoincide(email)) {
                claseRepo.actualizar(clase);
            }
        }
        
        Entrenador e = entrenadorRepo.buscar(email).orElseThrow(EntrenadorNoEncontrado::new);
        g.eliminarEntrenador(email);
        entrenadorRepo.actualizar(e);
    }

    public void anadirEntrenadorAClaseColectiva(int idClaseColectiva, String emailEntrenador) {
        ClaseColectiva clase = claseRepo.buscar(idClaseColectiva).orElseThrow(ClaseColectivaNoEncontrada::new);
        Entrenador entrenador = entrenadorRepo.buscar(emailEntrenador).orElseThrow(EntrenadorNoEncontrado::new);
        
        if (clase.getEntrenador() != null) {
            throw new EntrenadorYaAsignado();
        }
        
        clase.aniadirEntrenador(entrenador);
        entrenador.aniadirClase(clase);
        claseRepo.actualizar(clase);
        entrenadorRepo.actualizar(entrenador);
    }

    public List<ClaseColectiva> clasesPorEntrenador(String emailEntrenador) {
        Entrenador entrenador = entrenadorRepo.buscar(emailEntrenador).orElseThrow(EntrenadorNoEncontrado::new);
        if (!entrenador.getActivo()) {
            throw new EntrenadorInactivo();
        }
        return entrenador.obtenerClasesImpartidas();
    }

    public Usuario loginUsuario(String email, String clave) {
        Usuario u = usuarioRepo.buscar(email).orElseThrow(UsuarioNoEncontrado::new);

        if (!u.getClave().equals(clave)) {
            throw new ClaveIncorrecta();
        }
        return u;
    }

    public void quitarEntrenadorClaseColectiva(int idClaseColectiva){
        ClaseColectiva clase = claseRepo.buscar(idClaseColectiva).orElseThrow(ClaseColectivaNoEncontrada::new);
        
        if (clase.getEntrenador() == null) {
            throw new EntrenadorNoAsignado();
        }
        
        Entrenador entrenador = clase.getEntrenador();
        clase.quitarEntrenador();
        entrenador.eliminarClase(clase);
        
        claseRepo.actualizar(clase);
        entrenadorRepo.actualizar(entrenador);
    }
    
    public Solicitud nuevaSolicitud(@Valid int idClase, @Valid String emailUsuario, @Valid int idGimnasio) {
        ClaseColectiva clase = claseRepo.buscar(idClase).orElseThrow(ClaseColectivaNoEncontrada::new);
        Usuario usuario = usuarioRepo.buscar(emailUsuario).orElseThrow(UsuarioNoEncontrado::new);

        if (clase.getSolicitudes().stream().anyMatch(s -> s.getUsuario().getEmail().equals(emailUsuario))) {
            throw new UsuarioYaHaSolicitadoPlaza();
        }
        if (gimnasioRepo.buscar(idGimnasio).isEmpty()) {
            throw new GimnasioNoEncontrado();
        }
        if (gimnasioRepo.buscarUsuarioPorEmail(idGimnasio, emailUsuario).isEmpty()) {
            throw new UsuarioNoPerteneceAGimnasio();
        }
        if (gimnasioRepo.buscarClasePorId(idGimnasio, idClase).isEmpty()) {
            throw new ClaseColectivaNoPerteneceAGimnasio();
        }

        if (!usuario.getCuotaPagada()) {
            throw new CuotaNoPagada();
        }


        // Sólo se puede solicitar el día anterior o el mismo día antes de la clase
        DayOfWeek hoy = LocalDate.now().getDayOfWeek();
        DayOfWeek diaClase = DayOfWeek.valueOf(clase.getDiaSemana().name());
        int diasHastaClase = diaClase.getValue() - hoy.getValue();
        if (diasHastaClase < 0) {
            diasHastaClase += 7;
        }

        // Calcular la fecha y hora de la clase
        LocalDate fechaClase = LocalDate.now().plusDays(diasHastaClase);
        LocalDateTime fechaHoraClase = LocalDateTime.of(fechaClase, clase.getHoraIni());

        // Si la solicitud es para una clase con más de 1 día de antelación, no se permite
        if (diasHastaClase > 1) {
            throw new SolicitudDemasiadoPronto();
        }

        // Si es el mismo día, solo se permite si la hora actual es anterior a la hora de inicio de la clase
        if (diasHastaClase == 0 && LocalTime.now().isAfter(clase.getHoraIni())) {
            throw new SolicitudTardia();
        }


        if (fechaHoraClase.isBefore(LocalDateTime.now())) {
            throw new FechaPasada();
        }

        Solicitud solicitud = new Solicitud(LocalDate.now(), usuario);
        solicitud.setConfReserva(true);

        if (clase.getPlazasOcupadas() >= clase.getMaxPlazas()) {
            solicitud.setConfReserva(false); 
        }

        solicitudRepo.guardar(solicitud);
        clase.aniadirSolicitud(solicitud);
        claseRepo.actualizar(clase);

        return solicitud;
    }

    public List<Solicitud> buscarSolicitudesPorClase(ClaseColectiva clase) {
        if (claseRepo.buscar(clase.getId()).isEmpty()) {
            throw new ClaseColectivaNoEncontrada();
        }
        return clase.getSolicitudes();
    }

    public List<Solicitud> buscarSolicitudesPorUsuario(String emailUsuario) {
        if (usuarioRepo.buscar(emailUsuario).isEmpty()) {
            throw new UsuarioNoEncontrado();
        }
        return solicitudRepo.buscarSolicitudesPorUsuario(emailUsuario);
    }

    public void eliminarSolicitud(int idClase, int idSolicitud) {
        ClaseColectiva clase = claseRepo.buscar(idClase).orElseThrow(ClaseColectivaNoEncontrada::new);
        Solicitud solicitud = solicitudRepo.buscar(idSolicitud).orElseThrow(SolicitudNoEncontrada::new);

        if (!clase.getSolicitudes().contains(solicitud)) {
            throw new SolicitudNoPerteneceAClase();
        }

        clase.eliminarSolicitud(solicitud);
        if (solicitud.getConfReserva()) {
            clase.aceptarPrimeraSolicitudAutomaticamente();
        }
        solicitudRepo.eliminar(idSolicitud);

        claseRepo.actualizar(clase);
    }



    /**
     * Crea una nueva rutina para un usuario asignada por un entrenador pero sin ejercicios. También se asigna al usuario la rutina.
     * @param emailEntrenador
     * @param emailUsuario
     * @param idGimnasio
     * @param rutina
     * @return
     */
    public Rutina nuevaRutina(String emailEntrenador, String emailUsuario, @Valid int idGimnasio, @Valid Rutina rutina) {

        if (gimnasioRepo.buscar(idGimnasio).isEmpty()) {
            throw new GimnasioNoEncontrado();
        }
        if (usuarioRepo.buscar(emailUsuario).isEmpty()) {
            throw new UsuarioNoEncontrado();
        }
        if (entrenadorRepo.buscar(emailEntrenador).isEmpty()) {
            throw new EntrenadorNoEncontrado();
        }
        if (!gimnasioRepo.buscarUsuarioPorEmail(idGimnasio, emailUsuario).isPresent()) {
            throw new UsuarioNoPerteneceAGimnasio();
        }
        if (!gimnasioRepo.buscarEntrenadorPorEmail(idGimnasio, emailEntrenador).isPresent()) {
            throw new EntrenadorNoPerteneceAGimnasio();
        }
        if (rutinaRepo.buscar(rutina.getId()).isPresent()) {
            throw new RutinaYaExistente();
        }

        Entrenador e = entrenadorRepo.buscar(emailEntrenador).orElseThrow(EntrenadorNoEncontrado::new);
        Usuario u = usuarioRepo.buscar(emailUsuario).orElseThrow(UsuarioNoEncontrado::new);
        e.aniadirRutina(rutina);
        u.asignarRutina(rutina);
        rutina.setEntrenador(e);
        rutina.setUsuario(u);

        rutinaRepo.guardar(rutina);
        entrenadorRepo.actualizar(e);
        usuarioRepo.actualizar(u);
        return rutina;
    }

    public List<Rutina> buscarRutinas(){
        return rutinaRepo.buscarTodas();
    }

    public void eliminarRutina(int idRutina, String emailEntrenador) {
        Rutina rutina = rutinaRepo.buscar(idRutina).orElseThrow(RutinaNoEncontrada::new);
        
        if (!"admin@gimnasio.com".equalsIgnoreCase(emailEntrenador) && 
            !emailEntrenador.equals(rutina.getEntrenador().getEmail())) {
            throw new EntrenadorNoPerteneceARutina();
        }

        Entrenador entrenador = rutina.getEntrenador();
        if (entrenador != null) {
            entrenador.eliminarRutina(rutina);
        }

        List<Gimnasio> gimnasios = gimnasioRepo.buscarTodos();
        for (Gimnasio gimnasio : gimnasios) {
            for (Usuario usuario : gimnasio.usuariosMatriculados()) {
                if (usuario.getRutinas().contains(rutina)) {
                    usuario.eliminarRutina(rutina);
                    break;
                }
            }
        }
        rutinaRepo.eliminar(rutina.getId());
    }
    /**
     * Añade un ejercicioRutina a una rutina existente y también lo añade al repositorio (no tiene sentido crear un ejercicioRutina fuera de una rutina).
     * @param emailEntrenador
     * @param idRutina
     * @param ejercicioRutina
     * @return
     */

    public EjercicioRutina aniadirEjercicioRutina(String emailEntrenador, int idRutina, @Valid EjercicioRutina ejercicioRutina, int idEjercicio) {
        if (rutinaRepo.buscar(idRutina).isEmpty()) {
            throw new RutinaNoEncontrada();
        }
        Rutina rutina = rutinaRepo.buscar(idRutina).orElseThrow(RutinaNoEncontrada::new);

        // Permitir al admin añadir ejercicios a cualquier rutina sin restricciones
        if (!"admin@gimnasio.com".equals(emailEntrenador)) {
            if (rutina.getEntrenador() == null || !rutina.getEntrenador().getEmail().equals(emailEntrenador)) {
                throw new EntrenadorNoPerteneceARutina();
            }
        }

        Ejercicio ejercicio = ejercicioRepo.buscar(idEjercicio).orElseThrow(EjercicioNoEncontrado::new);
        rutina.aniadirEjercicioRutina(ejercicioRutina);
        ejercicioRutina.setEjercicio(ejercicio);
        ejercicioRutinaRepo.guardar(ejercicioRutina);
        rutinaRepo.actualizar(rutina);
        return ejercicioRutina;
    }

    public Optional<EjercicioRutina> buscarEjercicioRutina(int idEjercicioRutina) {
        return ejercicioRutinaRepo.buscar(idEjercicioRutina);
    }

    public EjercicioRutina actualizarEjercicioRutina(String emailEntrenador, int idRutina, @Valid EjercicioRutina ejercicioRutina) {

        Rutina rutina = rutinaRepo.buscar(idRutina).orElseThrow(RutinaNoEncontrada::new);
        
        if (ejercicioRutinaRepo.buscar(ejercicioRutina.getId()).isEmpty()) {
            throw new EjercicioRutinaNoEncontrado();
        }
        if (rutina.getEntrenador() == null || !rutina.getEntrenador().getEmail().equals(emailEntrenador)) {
            throw new EntrenadorNoPerteneceARutina();
        }

        ejercicioRutinaRepo.actualizar(ejercicioRutina);
        return ejercicioRutina;
    }

    public EjercicioRutina eliminarEjercicioRutina(String emailEntrenador, int idRutina, int idEjercicioRutina) {
        // Permitir al admin eliminar ejercicios de cualquier rutina sin restricciones
        boolean esAdmin = "admin@gimnasio.com".equalsIgnoreCase(emailEntrenador);
        if (!esAdmin && entrenadorRepo.buscar(emailEntrenador).isEmpty()) {
            throw new EntrenadorNoEncontrado();
        }
        if (ejercicioRutinaRepo.buscar(idEjercicioRutina).isEmpty()) {
            throw new EjercicioRutinaNoEncontrado();
        }
        Rutina rutina = rutinaRepo.buscar(idRutina).orElseThrow(RutinaNoEncontrada::new);
        EjercicioRutina ejercicioRutina = ejercicioRutinaRepo.buscar(idEjercicioRutina).orElseThrow(EjercicioRutinaNoEncontrado::new);
        if (!rutina.getEjerciciosRutina().contains(ejercicioRutina)) {
            throw new EjercicioRutinaNoPerteneceARutina();
        }
        if (!esAdmin && (rutina.getEntrenador() == null || !rutina.getEntrenador().getEmail().equals(emailEntrenador))) {
            throw new EntrenadorNoPerteneceARutina();
        }
        rutina.eliminarEjercicioRutina(ejercicioRutina);
        ejercicioRutinaRepo.eliminar(idEjercicioRutina);
        rutinaRepo.actualizar(rutina);
        return ejercicioRutina;
    }

    public Ejercicio nuevoEjercicio(@Valid Ejercicio ejercicio) {
        if (ejercicioRepo.buscar(ejercicio.getId()).isPresent()) {
            throw new EjercicioYaExiste();
        }
        if (ejercicioRepo.buscarPorNombre(ejercicio.getNombre()).isPresent()) {
            throw new EjercicioYaExiste();
        }

        ejercicioRepo.guardar(ejercicio);
        return ejercicio;
    }

    public void aniadirEjercicioAEjercicioRutina(int idEjercicio, int idEjercicioRutina) {
        if (ejercicioRepo.buscar(idEjercicio).isEmpty()) {
            throw new EjercicioNoEncontrado();
        }
        if (ejercicioRutinaRepo.buscar(idEjercicioRutina).isEmpty()) {
            throw new EjercicioRutinaNoEncontrado();
        }

        Ejercicio ejercicio = ejercicioRepo.buscar(idEjercicio).orElseThrow(EjercicioNoEncontrado::new);
        EjercicioRutina ejercicioRutina = ejercicioRutinaRepo.buscar(idEjercicioRutina).orElseThrow(EjercicioRutinaNoEncontrado::new);
        ejercicioRutina.setEjercicio(ejercicio);
        ejercicioRutinaRepo.actualizar(ejercicioRutina);
    }
    

    public List<Rutina> buscarRutinasPorEntrenador(String emailEntrenador) {
        Entrenador entrenador = entrenadorRepo.buscar(emailEntrenador).orElseThrow(EntrenadorNoEncontrado::new);
        if (!entrenador.getActivo()) {
            throw new EntrenadorInactivo();
        }
        return rutinaRepo.buscarPorEntrenador(emailEntrenador);
    }
    /**
     * Registra un nuevo acceso de un usuario a un gimnasio.
     * Se crea el acceso con la hora de entrada y la hora de salida como null.
     * Cuando el usuario salga, se actualizará la hora de salida.
     * @param idGimnasio ID del gimnasio.
     * @param emailUsuario Email del usuario.
     * @return El acceso creado.
     */
    public Acceso registrarEntrada(int idGimnasio, String emailUsuario) {
        Gimnasio gimnasio = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        Usuario usuario = usuarioRepo.buscar(emailUsuario).orElseThrow(UsuarioNoEncontrado::new);
        if (!gimnasio.usuariosMatriculados().contains(usuario)) {
            throw new UsuarioNoPerteneceAGimnasio();
        }

        Acceso acceso = new Acceso(LocalDateTime.now(), null, usuario, gimnasio);
        usuario.aniadirAcceso(acceso);
        accesoRepo.guardar(acceso);
        return acceso;
    }
    /**
     * Registra la salida de un usuario de un gimnasio actualizando la hora de salida en el último acceso abierto.
     * @param idGimnasio ID del gimnasio.
     * @param emailUsuario Email del usuario.
     * @return El acceso actualizado con la hora de salida.
     */
    public Acceso registrarSalida(int idGimnasio, String emailUsuario) {
        gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        Usuario usuario = usuarioRepo.buscar(emailUsuario).orElseThrow(UsuarioNoEncontrado::new);

        // Busca el último acceso sin hora de salida (acceso abierto)
        Acceso accesoAbierto = usuarioRepo.buscarUltimoAcceso(usuario.getEmail()).orElseThrow(UsuarioNoHaAccedidoGimnasio::new);

        accesoAbierto.setHoraSalida(LocalDateTime.now());
        accesoRepo.actualizar(accesoAbierto);
        return accesoAbierto;
    }


    @Scheduled(cron = "0 0 0 * * *")
    public void limpiarSolicitudesAntiguas() {
        LocalDate fechaLimite = LocalDate.now().minusDays(2);
        List<Solicitud> solicitudesAntiguas = solicitudRepo.buscarTodas().stream()
            .filter(s -> s.getFechaSolicitud().isBefore(fechaLimite))
            .toList();

        for (Solicitud solicitud : solicitudesAntiguas) {
            solicitudRepo.eliminar(solicitud.getId());
        }
    }

    /**
     * MÉTODOS PARA LA GAMIFICACIÓN POR RACHA DE DÍAS Y TOTAL DE DÍAS DE ASISTENCIA
     * Estos métodos se encargan de asignar medallas a los usuarios por sus rachas
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void comprobarYAsignarMedallasPorAcceso() {
        // Obtener todos los gimnasios y sus usuarios
        List<Usuario> usuarios = gimnasioRepo.buscarTodos().stream()
            .flatMap(g -> g.usuariosMatriculados().stream())
            .distinct()
            .collect(Collectors.toList());
        
        for (Usuario usuario : usuarios) {
            try {
                int diasSeguidos = calcularDiasSeguidos(usuario.getEmail());
                List<Medalla> medallasDiasSeguidos = medallaRepo.findByTipo("DIAS_SEGUIDOS");
                for (Medalla medalla : medallasDiasSeguidos) {
                    if (diasSeguidos >= medalla.getObjetivo() && !usuario.getMedallas().contains(medalla)) {
                        usuario.getMedallas().add(medalla);
                    }
                }

                int totalDias = (int) usuario.historialAccesos().stream()
                    .map(a -> a.getHoraEntrada().toLocalDate())
                    .distinct()
                    .count();
                List<Medalla> medallasTotalDias = medallaRepo.findByTipo("TOTAL_DIAS");
                for (Medalla medalla : medallasTotalDias) {
                    if (totalDias >= medalla.getObjetivo() && !usuario.getMedallas().contains(medalla)) {
                        usuario.getMedallas().add(medalla);
                    }
                }

                usuarioRepo.actualizar(usuario);
            } catch (Exception e) {
                // Log error pero continuar con el siguiente usuario
                System.err.println("Error al procesar medallas para usuario " + usuario.getEmail() + ": " + e.getMessage());
            }
        }
    }

    public int calcularDiasSeguidos(String email) {
        Usuario usuario = usuarioRepo.buscar(email).orElseThrow(UsuarioNoEncontrado::new);
        
        // Obtener días únicos de acceso ordenados
        List<LocalDate> diasUnicos = usuario.historialAccesos().stream()
            .map(acceso -> acceso.getHoraEntrada().toLocalDate())
            .distinct()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

        if (diasUnicos.isEmpty()) {
            return 0;
        }

        int maxRacha = 1;
        int rachaActual = 1;
        LocalDate diaAnterior = diasUnicos.get(0);

        for (int i = 1; i < diasUnicos.size(); i++) {
            LocalDate diaActual = diasUnicos.get(i);
            if (diaActual.plusDays(1).equals(diaAnterior)) {
                rachaActual++;
                if (rachaActual > maxRacha) {
                    maxRacha = rachaActual;
                }
            } else {
                rachaActual = 1;
            }
            diaAnterior = diaActual;
        }
        
        return maxRacha;
    }

    public int calcularSemanasSeguidas(String email) {
        Usuario usuario = usuarioRepo.buscar(email).orElseThrow(UsuarioNoEncontrado::new);
        
        // Obtener todas las fechas de acceso y ordenarlas
        Set<LocalDate> fechasAcceso = usuario.historialAccesos().stream()
            .map(acceso -> acceso.getHoraEntrada().toLocalDate())
            .collect(Collectors.toSet());
        
        if (fechasAcceso.isEmpty()) {
            return 0;
        }

        List<LocalDate> fechasOrdenadas = new ArrayList<>(fechasAcceso);
        fechasOrdenadas.sort(Comparator.naturalOrder());

        int maxSemanasSeguidas = 0;
        int semanasSeguidas = 1;
        LocalDate inicioSemanaActual = fechasOrdenadas.get(0).with(WeekFields.ISO.dayOfWeek(), 1);
        LocalDate semanaSiguienteEsperada = inicioSemanaActual.plusWeeks(1);

        for (int i = 1; i < fechasOrdenadas.size(); i++) {
            LocalDate fechaActual = fechasOrdenadas.get(i);
            LocalDate inicioSemanaFechaActual = fechaActual.with(WeekFields.ISO.dayOfWeek(), 1);

            if (inicioSemanaFechaActual.equals(semanaSiguienteEsperada)) {
                semanasSeguidas++;
                semanaSiguienteEsperada = inicioSemanaFechaActual.plusWeeks(1);
            } else if (inicioSemanaFechaActual.isAfter(semanaSiguienteEsperada)) {
                maxSemanasSeguidas = Math.max(maxSemanasSeguidas, semanasSeguidas);
                semanasSeguidas = 1;
                semanaSiguienteEsperada = inicioSemanaFechaActual.plusWeeks(1);
            }
        }

        return Math.max(maxSemanasSeguidas, semanasSeguidas);
    }

    @Scheduled(cron = "0 0 0 * * MON") // Ejecutar cada lunes a medianoche
    public void comprobarYAsignarMedallasPorSemanas(String email) {
        Usuario usuario = usuarioRepo.buscar(email).orElseThrow(UsuarioNoEncontrado::new);
        int semanasSeguidas = calcularSemanasSeguidas(email);
        
        // Buscar medallas de tipo SEMANAS_SEGUIDAS y asignarlas si corresponde
        List<Medalla> medallasSemanasSeguidas = medallaRepo.buscarPorTipo("SEMANAS_SEGUIDAS");
        for (Medalla medalla : medallasSemanasSeguidas) {
            if (semanasSeguidas >= medalla.getObjetivo() && !usuario.getMedallas().contains(medalla)) {
                usuario.getMedallas().add(medalla);
            }
        }

        usuarioRepo.actualizar(usuario);
    }


    // Método que verifica y asigna todas las medallas al usuario manualmente
    public void verificarYAsignarTodasLasMedallas(String email) {
        Usuario usuario = usuarioRepo.buscar(email).orElseThrow(UsuarioNoEncontrado::new);
        
        // Verificar medallas por días seguidos
        int diasSeguidos = calcularDiasSeguidos(email);
        List<Medalla> medallasDiasSeguidos = medallaRepo.findByTipo("DIAS_SEGUIDOS");
        for (Medalla medalla : medallasDiasSeguidos) {
            if (diasSeguidos >= medalla.getObjetivo() && !usuario.getMedallas().contains(medalla)) {
                usuario.getMedallas().add(medalla);
            }
        }

        // Verificar medallas por total de días
        int totalDias = (int) usuario.historialAccesos().stream()
            .map(a -> a.getHoraEntrada().toLocalDate())
            .distinct()
            .count();
        List<Medalla> medallasTotalDias = medallaRepo.findByTipo("TOTAL_DIAS");
        for (Medalla medalla : medallasTotalDias) {
            if (totalDias >= medalla.getObjetivo() && !usuario.getMedallas().contains(medalla)) {
                usuario.getMedallas().add(medalla);
            }
        }

        // Verificar medallas por semanas
        int semanasSeguidas = calcularSemanasSeguidas(email);
        List<Medalla> medallasSemanasSeguidas = medallaRepo.buscarPorTipo("SEMANAS_SEGUIDAS");
        for (Medalla medalla : medallasSemanasSeguidas) {
            if (semanasSeguidas >= medalla.getObjetivo() && !usuario.getMedallas().contains(medalla)) {
                usuario.getMedallas().add(medalla);
            }
        }

        usuarioRepo.actualizar(usuario);
    }

    public List<Medalla> buscarMedallasPorUsuario(String email) {
        Usuario usuario = usuarioRepo.buscar(email).orElseThrow(UsuarioNoEncontrado::new);
        return usuario.getMedallas();
    }

    /**
     * Crea un nuevo TipoClase en el sistema.
     * @param tipoClase El TipoClase a crear, debe ser válido.
     * @return El TipoClase creado.
     */
    public TipoClase nuevoTipoClase(@Valid TipoClase tipoClase) {
        if (tipoClaseRepo.buscar(tipoClase.getNombre()).isPresent())
            throw new TipoClaseYaExiste();
        tipoClaseRepo.guardar(tipoClase);
        return tipoClase;
    }

    /**
     * Busca un TipoClase por su nombre.
     * @param id del TipoClase a buscar
     * @return Un Optional que contiene el TipoClase si se encuentra, o vacío si no.
     */
    public Optional<TipoClase> buscarTipoClase(String nombre) {
        return tipoClaseRepo.buscar(nombre);
    }

    /**
     * Actualiza un TipoClase existente en el sistema.
     * @param tipoClase El TipoClase a actualizar, debe ser válido y existir en el sistema.
     * @return El TipoClase actualizado.
     */
    public TipoClase actualizarTipoClase(@Valid TipoClase tipoClase) {
        tipoClaseRepo.buscar(tipoClase.getNombre()).orElseThrow(TipoClaseNoEncontrado::new);
        tipoClaseRepo.actualizar(tipoClase);
        return tipoClase;
    }

    /**
     * Elimina un TipoClase del sistema.
     * @param idTipoClase El id del TipoClase a eliminar, debe existir en el sistema.
     */
    public void eliminarTipoClase(String nombre) {
        tipoClaseRepo.buscar(nombre).orElseThrow(TipoClaseNoEncontrado::new);
        tipoClaseRepo.eliminar(nombre);
    }

    public List<Rutina> buscarRutinasPorUsuario(String emailUsuario) {
        return usuarioRepo.buscarRutinasPorUsuario(emailUsuario);
    }

    public Rutina actualizarRutina(@Valid Rutina rutina) {
        if (rutinaRepo.buscar(rutina.getId()).isEmpty()) {
            throw new RutinaNoEncontrada();
        }
        rutinaRepo.actualizar(rutina);
        return rutina;
    }

    public List<Ejercicio> buscarEjercicios() {
        return ejercicioRepo.buscarTodos();
    }

    public Optional<Ejercicio> actualizarEjercicio(@Valid Ejercicio ejercicio) {
        if (ejercicioRepo.buscar(ejercicio.getId()).isEmpty()) {
            throw new EjercicioNoEncontrado();
        }
        ejercicioRepo.actualizar(ejercicio);
        return ejercicioRepo.buscar(ejercicio.getId());
    }

    public Rutina buscarRutinaPorId(int idRutina) {
        return rutinaRepo.buscar(idRutina).orElseThrow(RutinaNoEncontrada::new);
    }

    public List<EjercicioRutina> buscarEjerciciosRutinaPorRutina(int idRutina) {
        Rutina rutina = rutinaRepo.buscar(idRutina).orElseThrow(RutinaNoEncontrada::new);
        return rutina.getEjerciciosRutina();
    }

    public void eliminarEjercicio(int idEjercicio) {
        if (ejercicioRepo.buscar(idEjercicio).isEmpty()) {
            throw new EjercicioNoEncontrado();
        }
        try {
            ejercicioRepo.eliminar(idEjercicio);
        } catch (PersistenceException | DataIntegrityViolationException e) {
            throw new EjercicioNoSePuedeEliminar();
        }
    }

    public ClaseColectiva buscarClasePorSolicitud(int idSolicitud) {
        Solicitud solicitud = solicitudRepo.buscar(idSolicitud).orElseThrow(SolicitudNoEncontrada::new);
        List<ClaseColectiva> clases = claseRepo.buscarTodas();
        for (ClaseColectiva clase : clases) {
            if (clase.getSolicitudes().contains(solicitud)) {
                return clase;
            }
        }
        return null;
    }

    /**
     * Busca accesos aplicando filtros opcionales.
     * @param idGimnasio ID del gimnasio (opcional)
     * @param emailUsuario Email del usuario (opcional)
     * @param fechaInicio Fecha inicio del rango (opcional)
     * @param fechaFin Fecha fin del rango (opcional)
     * @return Lista de accesos que cumplen los filtros
     */
    public List<Acceso> buscarAccesosFiltrados(Integer idGimnasio, String emailUsuario, 
                                              LocalDate fechaInicio, LocalDate fechaFin) {
        List<Acceso> accesos = accesoRepo.buscarTodos();
        
        return accesos.stream()
            .filter(a -> idGimnasio == null || a.getGimnasio().getId() == idGimnasio)
            .filter(a -> emailUsuario == null || a.getUsuario().getEmail().equals(emailUsuario))
            .filter(a -> fechaInicio == null || 
                !a.getHoraEntrada().toLocalDate().isBefore(fechaInicio))
            .filter(a -> fechaFin == null || 
                !a.getHoraEntrada().toLocalDate().isAfter(fechaFin))
            .collect(Collectors.toList());
    }

    /**
     * Calcula estadísticas de accesos para un período.
     * @param accesos Lista de accesos a analizar
     * @return Mapa con las estadísticas calculadas
     */
    public Map<String, Object> calcularEstadisticasAccesos(List<Acceso> accesos) {
        Map<String, Object> stats = new HashMap<>();
        
        if (accesos.isEmpty()) {
            stats.put("mediaDiaria", 0.0);
            stats.put("maximoDiario", 0);
            stats.put("maximoDiarioFecha", null);
            stats.put("maximoSemanal", 0);
            stats.put("maximoSemanalFecha", null);
            stats.put("horaPico", "00:00");
            stats.put("personasHoraPico", 0);
            return stats;
        }

        // Agrupar por día
        Map<LocalDate, Long> accesosPorDia = accesos.stream()
            .collect(Collectors.groupingBy(
                a -> a.getHoraEntrada().toLocalDate(),
                Collectors.counting()
            ));

        // Agrupar por semana
        Map<Integer, Long> accesosPorSemana = accesos.stream()
            .collect(Collectors.groupingBy(
                a -> a.getHoraEntrada().get(WeekFields.ISO.weekOfWeekBasedYear()),
                Collectors.counting()
            ));

        // Agrupar por hora
        Map<Integer, Long> accesosPorHora = accesos.stream()
            .collect(Collectors.groupingBy(
                a -> a.getHoraEntrada().getHour(),
                Collectors.counting()
            ));

        // Calcular media diaria
        double mediaDiaria = (double) accesos.size() / accesosPorDia.size();

        // Encontrar máximo diario
        Map.Entry<LocalDate, Long> maximoDiario = accesosPorDia.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);

        // Encontrar máximo semanal
        Map.Entry<Integer, Long> maximoSemanal = accesosPorSemana.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);

        // Encontrar hora pico
        Map.Entry<Integer, Long> horaPico = accesosPorHora.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);

        stats.put("mediaDiaria", mediaDiaria);
        stats.put("maximoDiario", maximoDiario != null ? maximoDiario.getValue() : 0);
        stats.put("maximoDiarioFecha", maximoDiario != null ? maximoDiario.getKey() : null);
        stats.put("maximoSemanal", maximoSemanal != null ? maximoSemanal.getValue() : 0);
        stats.put("maximoSemanalFecha", maximoSemanal != null ? maximoSemanal.getKey() : null);
        stats.put("horaPico", horaPico != null ? String.format("%02d:00", horaPico.getKey()) : "00:00");
        stats.put("personasHoraPico", horaPico != null ? horaPico.getValue() : 0);

        return stats;
    }

    /**
     * Obtiene estadísticas de accesos para un periodo determinado.
     * @param idGimnasio ID del gimnasio (opcional)
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Mapa con las estadísticas calculadas
     */
    public Map<String, Object> obtenerEstadisticasAccesos(Optional<Integer> idGimnasio, 
            LocalDate fechaInicio, LocalDate fechaFin) {
        
        List<Acceso> accesos = buscarAccesosFiltrados(idGimnasio.orElse(null), null, fechaInicio, fechaFin);
        Map<String, Object> estadisticas = new HashMap<>();

        if (accesos.isEmpty()) {
            estadisticas.put("mediaDiaria", 0.0);
            estadisticas.put("maximoDiario", 0);
            estadisticas.put("maximoDiarioFecha", null);
            estadisticas.put("maximoSemanal", 0);
            estadisticas.put("maximoSemanalFecha", null);
            estadisticas.put("ocupacionPorHora", new int[24]);
            return estadisticas;
        }

        // Agrupar accesos por día
        Map<LocalDate, Long> accesosPorDia = accesos.stream()
            .collect(Collectors.groupingBy(
                a -> a.getHoraEntrada().toLocalDate(),
                Collectors.counting()
            ));

        // Agrupar accesos por semana
        Map<Integer, Long> accesosPorSemana = accesos.stream()
            .collect(Collectors.groupingBy(
                a -> a.getHoraEntrada().get(WeekFields.ISO.weekOfWeekBasedYear()),
                Collectors.counting()
            ));

        // Contar accesos por hora
        int[] ocupacionPorHora = new int[24];
        accesos.forEach(a -> {
            int hora = a.getHoraEntrada().getHour();
            ocupacionPorHora[hora]++;
        });

        // Calcular estadísticas
        double mediaDiaria = (double) accesos.size() / accesosPorDia.size();
        Map.Entry<LocalDate, Long> maximoDiario = accesosPorDia.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);
        Map.Entry<Integer, Long> maximoSemanal = accesosPorSemana.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);

        // Encontrar hora pico
        int horaPico = 0;
        for (int i = 1; i < 24; i++) {
            if (ocupacionPorHora[i] > ocupacionPorHora[horaPico]) {
                horaPico = i;
            }
        }

        // Construir resultado
        estadisticas.put("mediaDiaria", mediaDiaria);
        estadisticas.put("maximoDiario", maximoDiario != null ? maximoDiario.getValue() : 0);
        estadisticas.put("maximoDiarioFecha", maximoDiario != null ? maximoDiario.getKey() : null);
        estadisticas.put("maximoSemanal", maximoSemanal != null ? maximoSemanal.getValue() : 0);
        estadisticas.put("maximoSemanalFecha", maximoSemanal != null ? maximoSemanal.getKey() : null);
        estadisticas.put("horaPico", horaPico);
        estadisticas.put("personasHoraPico", ocupacionPorHora[horaPico]);
        estadisticas.put("ocupacionPorHora", ocupacionPorHora);

        return estadisticas;
    }

    /**
     * Genera accesos aleatorios para pruebas.
     * Para cada usuario del gimnasio, genera accesos aleatorios en los últimos 30 días.
     * @param idGimnasio ID del gimnasio
     * @param cantidadPorUsuario Cantidad de accesos a generar por usuario
     */
    public void generarAccesosAleatorios(int idGimnasio, int cantidadPorUsuario) {
        Gimnasio gimnasio = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        List<Usuario> usuarios = gimnasio.usuariosMatriculados();
        Random random = new Random();

        // Generar para los últimos 90 días
        LocalDate fechaInicio = LocalDate.now().minusDays(90);
        
        // Crear un conjunto para llevar el registro de fechas usadas por usuario
        Map<String, Set<LocalDate>> fechasUsadasPorUsuario = new HashMap<>();
        
        for (Usuario usuario : usuarios) {
            fechasUsadasPorUsuario.put(usuario.getEmail(), new HashSet<>());
            int intentos = 0;
            int accesosGenerados = 0;
            
            while (accesosGenerados < cantidadPorUsuario && intentos < 100) { // límite de intentos para evitar bucles infinitos
                // Generar fecha aleatoria en los últimos 90 días
                long diasAleatorios = random.nextInt(91); // 0-90 días atrás
                LocalDate fechaAcceso = fechaInicio.plusDays(diasAleatorios);
                
                // Verificar si ya existe un acceso para este usuario en esta fecha
                if (!fechasUsadasPorUsuario.get(usuario.getEmail()).contains(fechaAcceso)) {
                    fechasUsadasPorUsuario.get(usuario.getEmail()).add(fechaAcceso);
                
                // Generar hora aleatoria entre la apertura y cierre
                int horaInicio = gimnasio.getHoraApertura().getHour();
                int horaFin = gimnasio.getHoraCierre().getHour();
                int horaEntrada = horaInicio + random.nextInt(horaFin - horaInicio);
                int minutos = random.nextInt(60);
                
                LocalDateTime fechaHoraEntrada = LocalDateTime.of(
                    fechaAcceso, 
                    LocalTime.of(horaEntrada, minutos)
                );
                
                // Duración aleatoria entre 1 y 3 horas
                int duracionMinutos = 60 + random.nextInt(120); // 1-3 horas
                LocalDateTime fechaHoraSalida = fechaHoraEntrada.plusMinutes(duracionMinutos);
                
                Acceso acceso = new Acceso();
                acceso.setGimnasio(gimnasio);
                acceso.setUsuario(usuario);
                acceso.setHoraEntrada(fechaHoraEntrada);
                acceso.setHoraSalida(fechaHoraSalida);
                
                accesoRepo.guardar(acceso);
                accesosGenerados++;
                }
                intentos++;
            }
        }
    }

    /**
     * Genera usuarios aleatorios para pruebas.
     * @param idGimnasio ID del gimnasio donde crear los usuarios
     * @param cantidad Cantidad de usuarios a generar
     */
    public void generarUsuariosAleatorios(int idGimnasio, int cantidad) {
        Gimnasio gimnasio = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        Random random = new Random();
        
        String[] nombres = {"Juan", "Maria", "Carlos", "Ana", "Pedro", "Laura", "Miguel", "Sofia", 
                          "David", "Elena", "Pablo", "Carmen", "Jose", "Isabel", "Luis", "Patricia"};
        String[] apellidos = {"Garcia", "Rodriguez", "Gonzalez", "Fernandez", "Lopez", "Martinez", 
                            "Sanchez", "Perez", "Gomez", "Martin", "Jimenez", "Ruiz", "Hernandez", 
                            "Diaz", "Moreno", "Munoz", "Alvarez", "Romero", "Alonso", "Gutierrez"};
        
        for (int i = 0; i < cantidad; i++) {
            String nombre = nombres[random.nextInt(nombres.length)];
            String apellido1 = apellidos[random.nextInt(apellidos.length)];
            String apellido2 = apellidos[random.nextInt(apellidos.length)];
            String email = nombre.toLowerCase() + "." + apellido1.toLowerCase() + i + "@test.com";
            String telefono = "6" + String.format("%08d", random.nextInt(100000000));
            
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellidos(apellido1 + " " + apellido2);
            usuario.setEmail(email);
            usuario.setTlf(telefono);
            usuario.setClave(passwordEncoder.encode("password123"));
            usuario.setCuotaPagada(random.nextBoolean());
            if (usuario.getCuotaPagada()) {
                usuario.setCuotaValidaHasta(LocalDate.now().plusMonths(random.nextInt(3) + 1));
            }
            
            try {
                registroUsuario(gimnasio, usuario);
            } catch (Exception e) {
                // Ignorar errores y continuar con el siguiente usuario
                continue;
            }
        }
    }
        public int consultarAforo(Long idGimnasio, LocalDateTime momento) {
        //System.out.println("Consultando aforo para el gimnasio con ID: " + idGimnasio + " en el momento: " + momento);
        return accesoRepo.consultarAforo(
            idGimnasio,
            momento);
    }

    public Medalla crearMedalla(@Valid Medalla medalla) {
        if (medallaRepo.buscar(medalla.getId()).isPresent()) {
            throw new MedallaYaExiste();
        }
        
        List<Medalla> medallasDelMismoTipo = medallaRepo.buscarPorTipo(medalla.getTipo());
        boolean existeMedallaConMismoObjetivo = medallasDelMismoTipo.stream()
            .anyMatch(m -> m.getObjetivo() == medalla.getObjetivo());
            
        if (existeMedallaConMismoObjetivo) {
            throw new MedallaYaExiste();
        }
        
        medallaRepo.guardar(medalla);
        return medalla;
    }

    public void eliminarMedalla(int idMedalla) {
        if (medallaRepo.buscar(idMedalla).isEmpty()) {
            throw new MedallaNoEncontrada();
        }
        medallaRepo.eliminar(idMedalla);
    }

    public Medalla buscarMedallaPorId(int idMedalla) {
        return medallaRepo.buscar(idMedalla).orElseThrow(MedallaNoEncontrada::new);
    }

    public Medalla actualizarMedalla(@Valid Medalla medalla) {
        if (medallaRepo.buscar(medalla.getId()).isEmpty()) {
            throw new MedallaNoEncontrada();
        }
        medallaRepo.actualizar(medalla);
        return medalla;
    }

    public List<Medalla> buscarMedallas(){
        return medallaRepo.buscarTodas();
    }

    public List<Medalla> buscarMedallasPorTipo(String tipo) {
        return medallaRepo.buscarPorTipo(tipo);
    }

    public void otorgarMedallaAUsuario(String emailUsuario, int idMedalla) {
        Usuario usuario = usuarioRepo.buscar(emailUsuario).orElseThrow(UsuarioNoEncontrado::new);
        Medalla medalla = medallaRepo.buscar(idMedalla).orElseThrow(MedallaNoEncontrada::new);
        
        if (usuario.getMedallas().contains(medalla)) {
            throw new MedallaYaOtorgada();
        }
        
        usuario.getMedallas().add(medalla);
        usuarioRepo.actualizar(usuario);
    }

    public int totalMedallasUsuario(String emailUsuario){
        Usuario usuario = usuarioRepo.buscar(emailUsuario).orElseThrow(UsuarioNoEncontrado::new);
        return usuario.getMedallas().size();
    }

    public List<Medalla> listarMedallasPorUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepo.buscar(emailUsuario).orElseThrow(UsuarioNoEncontrado::new);
        return usuario.getMedallas();
    }

    public int totalMedallasGimnasio(int idGimnasio) {
        Gimnasio gimnasio = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        return gimnasio.usuariosMatriculados().stream()
            .mapToInt(usuario -> usuario.getMedallas().size())
            .sum();
    }

    public Acceso buscarUltimoAccesoUsuario(String emailUsuario) {
        return accesoRepo.buscarUltimoAccesoPorUsuario(emailUsuario);
    }

    public String obtenerVideoEjercicio(String nombreEjercicio) {
        Ejercicio ejercicio = ejercicioRepo.buscarPorNombre(nombreEjercicio)
            .orElseThrow(EjercicioNoEncontrado::new);
        return ejercicio.getVideo();
    }

    public List<Usuario> usuariosGimnasioConMedalla(int idGimnasio, int idMedalla) {
        Gimnasio gimnasio = gimnasioRepo.buscar(idGimnasio).orElseThrow(GimnasioNoEncontrado::new);
        Medalla medalla = medallaRepo.buscar(idMedalla).orElseThrow(MedallaNoEncontrada::new);
        
        return gimnasio.usuariosMatriculados().stream()
            .filter(usuario -> usuario.getMedallas().contains(medalla))
            .collect(Collectors.toList());
    }
    public ClaseColectiva claseDeSolicitud(int idSolicitud) {
        Solicitud solicitud = solicitudRepo.buscar(idSolicitud).orElseThrow(SolicitudNoEncontrada::new);
        return claseRepo.buscarClasePorSolicitud(solicitud)
            .orElseThrow(ClaseColectivaNoEncontrada::new);
    }


}