package com.tfg.gestion_gimnasios.rest.dto;

import com.tfg.gestion_gimnasios.entidades.Acceso;
import java.time.LocalDateTime;

public record DAcceso(
    Integer id,
    Integer idGimnasio,
    String emailUsuario,
    String nombreUsuario,
    String apellidosUsuario,
    String nombreGimnasio,
    LocalDateTime horaEntrada,
    LocalDateTime horaSalida
) {
    public DAcceso(Integer id, LocalDateTime horaEntrada, LocalDateTime horaSalida) {
        this(id, null, null, null, null, null, horaEntrada, horaSalida);
    }
    public static DAcceso of(Acceso acceso) {
        return new DAcceso(
            acceso.getId(),
            acceso.getGimnasio().getId(),
            acceso.getUsuario().getEmail(),
            acceso.getUsuario().getNombre(),
            acceso.getUsuario().getApellidos(),
            acceso.getGimnasio().getNombre(),
            acceso.getHoraEntrada(),
            acceso.getHoraSalida()
        );
    }
}
