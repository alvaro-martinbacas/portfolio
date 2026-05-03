package com.tfg.gestion_gimnasios.entidades;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
public class Entrenador {
    @NotBlank
    private String nombre;

    @NotBlank
    @Id
    @Email
    private String email;

    @NotBlank
    @Size(min=8)
    private String clave;

    @Pattern(regexp = "^(\\+34|0034|34)?[6789]\\d{8}$") 
    private String tlf;

    private Boolean activo;

    @OneToMany(mappedBy="entrenador")
    private List<Rutina> rutinas;

    @OneToMany(mappedBy="entrenador")
    private List<ClaseColectiva> clasesImpartidas;
    

    public Entrenador() {
    }

    public Entrenador(String nombre, String email, String clave, String tlf, Boolean activo) {
        this.nombre = nombre;
        this.email = email;
        this.clave = clave;
        this.tlf = tlf;
        this.activo = activo;
        this.rutinas = new LinkedList<>();
        this.clasesImpartidas = new LinkedList<>();
    }

    public void aniadirClase(ClaseColectiva clase) {
        if (clase != null && !clasesImpartidas.contains(clase)) {
            clasesImpartidas.add(clase);
        }
    }

    public List<ClaseColectiva> obtenerClasesImpartidas() {
        return clasesImpartidas;
    }

    public void aniadirRutina(Rutina rutina) {
        if (rutina != null && !rutinas.contains(rutina)) {
            rutinas.add(rutina);
        }
    }

    public void eliminarRutina(Rutina rutina) {
        if (rutina != null) {
            rutinas.remove(rutina);
        }
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getTlf() {
        return tlf;
    }

    public void setTlf(String tlf) {
        this.tlf = tlf;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public void eliminarClase(ClaseColectiva clase) {
        if (clase != null && clasesImpartidas.contains(clase)) {
            clasesImpartidas.remove(clase);
        }
    }
}
