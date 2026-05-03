package com.tfg.gestion_gimnasios.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private LocalDateTime horaEntrada;

    private LocalDateTime horaSalida;

    @Column(name = "usuario_nombre_registro")
    private String usuarioNombreRegistro;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "gimnasio_id")
    private Gimnasio gimnasio;

    public Acceso() {}

    public Acceso(LocalDateTime horaEntrada, LocalDateTime horaSalida, Usuario usuario, Gimnasio gimnasio) {
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.usuario = usuario;
        this.gimnasio = gimnasio;
        if (usuario != null) {
            this.usuarioNombreRegistro = usuario.getNombre() + " " + usuario.getApellidos();
        }
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalDateTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalDateTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            this.usuarioNombreRegistro = usuario.getNombre() + " " + usuario.getApellidos();
        }
    }

    public Gimnasio getGimnasio() {
        return gimnasio;
    }

    public void setGimnasio(Gimnasio gimnasio) {
        this.gimnasio = gimnasio;
    }


    public String getUsuarioNombreRegistro() {
        return usuarioNombreRegistro;
    }

    public void setUsuarioNombreRegistro(String usuarioNombreRegistro) {
        this.usuarioNombreRegistro = usuarioNombreRegistro;
    }
}
