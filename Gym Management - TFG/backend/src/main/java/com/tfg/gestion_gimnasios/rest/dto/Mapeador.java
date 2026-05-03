package com.tfg.gestion_gimnasios.rest.dto;

import com.tfg.gestion_gimnasios.entidades.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Mapeador {

    @Autowired
    PasswordEncoder codificadorClaves;

    public DGimnasio toDTO(Gimnasio gimnasio) {
    return new DGimnasio(
        gimnasio.getId(),
        gimnasio.getNombre(),
        gimnasio.getDireccion(),
        gimnasio.getTelefono(),
        gimnasio.getHoraApertura(),
        gimnasio.getHoraCierre()
    );
    }

    public Gimnasio toEntity(DGimnasio dto) {
        return new Gimnasio(
            dto.id(),
            dto.nombre(),
            dto.direccion(),
            dto.telefono(),
            dto.horaApertura(),
            dto.horaCierre()
        );
    }

    public DUsuario toDTO(Usuario usuario) {
        return new DUsuario(
            usuario.getNombre(),
            usuario.getApellidos(),
            usuario.getTlf(),
            usuario.getEmail(),
            "**********",
            usuario.getCuotaPagada(),
            usuario.getCuotaValidaHasta()
        );
    }
    public Usuario toEntity(DUsuario dto) {
        return new Usuario(
            dto.nombre(),
            dto.apellidos(),
            dto.tlf(),
            dto.email(),
            dto.clave()
        );
    }

    public Usuario nuevaEntidad(DUsuario dto) {
        return new Usuario(
            dto.nombre(),
            dto.apellidos(),
            dto.tlf(),
            dto.email(),
            codificadorClaves.encode(dto.clave()),
            dto.cuotaPagada(),
            dto.cuotaValidaHasta()
        );
    }

    public ClaseColectiva toEntity(DClaseColectiva dto) {
        return new ClaseColectiva(
            dto.id(),
            dto.diaSemana(),
            dto.horaIni(),
            dto.horaFin()
        );
    }

    public DClaseColectiva toDTO(ClaseColectiva claseColectiva) {
        return new DClaseColectiva(
            claseColectiva.getId(),
            claseColectiva.getDiaSemana(),
            claseColectiva.getHoraIni(),
            claseColectiva.getHoraFin(),
            claseColectiva.getPlazasOcupadas(),
            claseColectiva.getTipoClase() != null ? claseColectiva.getTipoClase() : null,
            claseColectiva.getEntrenador() != null ? claseColectiva.getEntrenador().getNombre() : null
        );
    }

    public DEntrenador toDTO(Entrenador entrenador) {
        return new DEntrenador(
            entrenador.getNombre(),
            entrenador.getEmail(),
            "**********",
            entrenador.getTlf(),
            entrenador.getActivo()
        );
    }

    public Entrenador toEntity(DEntrenador dto) {
        return new Entrenador(
            dto.nombre(),
            dto.email(),
            dto.clave(),
            dto.tlf(),
            dto.activo()
        );
    }

    public Entrenador nuevaEntidad(DEntrenador dto) {
        return new Entrenador(
            dto.nombre(),
            dto.email(),
            codificadorClaves.encode(dto.clave()),
            dto.tlf(),
            dto.activo()
        );
    }

    public DSolicitud toDTO(Solicitud solicitud, String emailUsuario, String nombreClase) {
        return new DSolicitud(
            solicitud.getId(),
            solicitud.getFechaSolicitud(),
            solicitud.getConfReserva(),
            emailUsuario,
            nombreClase
        );
    }

    public Solicitud toEntity(DSolicitud dto, Usuario usuario) {
        return new Solicitud(
            dto.id(),
            dto.fechaSolicitud(),
            dto.confReserva()
        );
    }

    public DRutina toDTO(Rutina rutina) {
        return new DRutina(
            rutina.getId(),
            rutina.getNombre(),
            rutina.getDescripcion(),
            rutina.getEntrenador() != null ? rutina.getEntrenador().getEmail() : null,
            rutina.getUsuario() != null ? rutina.getUsuario().getEmail() : null);
    }

    public Rutina toEntity(DRutina dto, Entrenador entrenador) {
        Rutina rutina = new Rutina(
            dto.id(),
            dto.nombre(),
            dto.descripcion()
        );
        rutina.setEntrenador(entrenador);
        return rutina;
    }

    public DEjercicioRutina toDTO(EjercicioRutina ejercicioRutina) {
        return new DEjercicioRutina(
            ejercicioRutina.getId(),
            ejercicioRutina.getSeries(),
            ejercicioRutina.getRepeticiones(),
            ejercicioRutina.getDescanso(),
            ejercicioRutina.getIndicaciones(),
            ejercicioRutina.getEjercicio().getNombre()
        );
    }

    public EjercicioRutina toEntity(DEjercicioRutina dto) {
        return new EjercicioRutina(
            dto.id(),
            dto.series(),
            dto.repeticiones(),
            dto.descanso(),
            dto.indicaciones()
        );

    }

    public DEjercicio toDTO(Ejercicio ejercicio) {
        return new DEjercicio(
            ejercicio.getId(),
            ejercicio.getNombre(),
            ejercicio.getGrupoMuscular(),
            ejercicio.getEquipo(),
            ejercicio.getVideo()
        );
    }

    public Ejercicio toEntity(DEjercicio dto) {
        return new Ejercicio(
            dto.id(),
            dto.nombre(),
            dto.grupoMuscular(),
            dto.equipo(),
            dto.video()
        );
    }

    public DAcceso toDTO(Acceso acceso) {
        return new DAcceso(
            acceso.getId(),
            acceso.getHoraEntrada(),
            acceso.getHoraSalida()
        );
    }

    public DMedalla toDTO(Medalla medalla) {
        return new DMedalla(
            medalla.getId(),
            medalla.getNombre(),
            medalla.getDescripcion(),
            medalla.getIcono(),
            medalla.getTipo(),
            medalla.getObjetivo()
        );
    }

    public DTipoClase toDTO(TipoClase tipoClase) {
        return new DTipoClase(
            tipoClase.getNombre(),
            tipoClase.getDescripcion(),
            tipoClase.getMaxPlazas()
        );
    }

    public TipoClase toEntity(DTipoClase dto) {
        return new TipoClase(
            dto.nombre(),
            dto.descripcion(),
            dto.maxPlazas()
        );
    }

    public DAccesoNoConfidencial toDTONoConfidencial(Acceso acceso) {
        return new DAccesoNoConfidencial(
            acceso.getId(),
            acceso.getHoraEntrada(),
            acceso.getHoraSalida()
        );
    }
    
}

