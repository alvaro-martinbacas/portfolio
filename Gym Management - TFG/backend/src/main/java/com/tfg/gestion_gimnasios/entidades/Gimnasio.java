package com.tfg.gestion_gimnasios.entidades;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
public class Gimnasio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
  
    @NotBlank
    private String nombre;

    @NotBlank
    private String direccion;

    @NotBlank
    private String telefono;

    @NotNull
    private LocalTime horaApertura;
    @NotNull
    private LocalTime horaCierre;

    
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name="gimnasio_id")
    private List<Entrenador> entrenadores;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name="gimnasio_id")
    private List<Usuario> usuarios;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name="gimnasio_id")
    List<ClaseColectiva> clases;

    @OneToMany(mappedBy = "gimnasio")
    private List<Acceso> accesos;

    public Gimnasio() {
    }

    // Constructor con todos los campos
    public Gimnasio(int id, String nombre, String direccion, String telefono,
                    LocalTime horaApertura, LocalTime horaCierre) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.entrenadores = new LinkedList<>();
        this.usuarios = new LinkedList<>();
        this.clases = new LinkedList<>();
        this.accesos = new LinkedList<>();
    }

    public Gimnasio(String nombre, String direccion, String telefono,
                    LocalTime horaApertura, LocalTime horaCierre) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.entrenadores = new LinkedList<>();
        this.usuarios = new LinkedList<>();
        this.clases = new LinkedList<>();
        this.accesos = new LinkedList<>();
    }


    public void registrarAcceso(Acceso acceso) {
        accesos.add(acceso);
        acceso.setGimnasio(this);
    }

    public void aniadirUsuario(Usuario usuario) {
        if (usuario != null && !usuarios.contains(usuario)) {
            usuarios.add(usuario);
            usuario.pagarCuota(); // Aseguramos que el usuario paga la cuota al añadirlo        
        }
    }

    public void aniadirEntrenador(Entrenador entrenador) {
        if (entrenador != null && !entrenadores.contains(entrenador)) {
            entrenadores.add(entrenador);
            entrenador.setActivo(true); // Aseguramos que el entrenador está activo al añadirlo
        }
    }

    public void aniadirClaseColectiva(ClaseColectiva clase) {
        if (clase != null && !clases.contains(clase)) {
            clases.add(clase);
        }
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(email)) {
                return usuario;
            }
        }
        return null;
    }

    public ClaseColectiva buscarClasePorId(int idClase) {
        for (ClaseColectiva clase : clases) {
            if (clase.getId() == idClase) {
                return clase;
            }
        }
        return null;
    }

    public List<ClaseColectiva> clasesDisponibles() {
        return clases;
    }

    public Entrenador buscarEntrenadorPorEmail(String email) {
        for (Entrenador entrenador : entrenadores) {
            if (entrenador.getEmail().equals(email)) {
                return entrenador;
            }
        }
        return null;
    }

    public List<Entrenador> entrenadoresTrabajando() {
        List<Entrenador> entrenadoresActivos = new LinkedList<>();
        for (Entrenador entrenador : entrenadores) {
            if (entrenador.getActivo()) {
                entrenadoresActivos.add(entrenador);
            }
        }
        return entrenadoresActivos;
    }

    public List<Entrenador> entrenadores() {
        return entrenadores;
    }
    
    public void eliminarUsuario(String email) {
    Usuario usuarioAEliminar = null;
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(email)) {
                usuarioAEliminar = usuario;
                break;
            }
        }
        if (usuarioAEliminar != null) {
            usuarios.remove(usuarioAEliminar);
        }
    }

    public void desactivarEntrenador(String email) {
        for (Entrenador entrenador : entrenadores) {
            if (entrenador.getEmail().equals(email)) {
                entrenador.setActivo(false); // Desactiva al entrenador en lugar de eliminarlo
                break;
            }
        }
    }

    public void activarEntrenador(String email) {
        for (Entrenador entrenador : entrenadores) {
            if (entrenador.getEmail().equals(email)) {
                entrenador.setActivo(true); // Activa al entrenador
                break;
            }
        }
    }

    public void eliminarEntrenador(String email) {
        Entrenador entrenadorAEliminar = null;
        for (Entrenador entrenador : entrenadores) {
            if (entrenador.getEmail().equals(email)) {
                entrenadorAEliminar = entrenador;
                break;
            }
        }
        if (entrenadorAEliminar != null) {
            entrenadores.remove(entrenadorAEliminar);
        }
    }

    public void eliminarClaseColectiva(int idClase) {
        for (ClaseColectiva clase : clases) {
            if (clase.getId() == idClase) {
                clases.remove(clase);
                break;
            }
        }
    }

    public boolean estaMatriculado(Usuario usuario) {
        return usuarios.contains(usuario);
    }


    public List<Usuario> usuariosMatriculados() {
        return usuarios;
    }

    public List<Acceso> historialAccesos(){
        return accesos;
    }

    public boolean horarioPermitido(LocalTime horaInicio, LocalTime horaFin) {
        return !horaInicio.isBefore(horaApertura) && 
               !horaFin.isAfter(horaCierre) && 
               horaInicio.isBefore(horaFin);
    }

    public int getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(LocalTime horaApertura) {
        this.horaApertura = horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(LocalTime horaCierre) {
        this.horaCierre = horaCierre;
    }

}