package com.tfg.gestion_gimnasios.entidades;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
public class Usuario {
    @NotBlank
    private String nombre;

    @NotBlank
    private String apellidos;

    @Pattern(regexp = "^(\\+34|0034|34)?[6789]\\d{8}$") 
    private String tlf;

    @Id
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String clave;

    private Boolean cuotaPagada;

    private LocalDate cuotaValidaHasta;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rutina> rutinas;

    @OneToMany(mappedBy = "usuario")
    private List<Acceso> accesos;

    @PreRemove
    private void nullifyUserInAccesos() {
        if (accesos != null) {
            accesos.forEach(acceso -> acceso.setUsuario(null));
        }
    }

    @ManyToMany
    @JoinTable(
        name = "usuario_medalla",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "medalla_id")
    )
    private List<Medalla> medallas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solicitud> solicitudes = new LinkedList<>();

    public Usuario() {
    }

    public Usuario(String nombre, String apellidos, String tlf, String email, String clave, Boolean cuotaPagada, LocalDate cuotaValidaHasta) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.tlf = tlf;
        this.email = email;
        this.clave = clave;
        this.cuotaPagada = cuotaPagada;
        this.cuotaValidaHasta = cuotaValidaHasta;
        this.rutinas = new LinkedList<>();
        this.accesos = new LinkedList<>();
        this.medallas = new LinkedList<>();
    }

    public Usuario(String nombre, String apellidos, String tlf, String email, String clave) {
        this(nombre, apellidos, tlf, email, clave, false, null);
    }

    /**
     * Resetea el estado de la cuota del usuario.
     * Esto implica que la cuota no está pagada y la fecha de validez se establece
     */
    public void resetCuota(){
        this.cuotaPagada = false;
        this.cuotaValidaHasta = null;
    }

    /**
     * Paga la cuota del usuario por un número determinado de meses.
     * Si la cuota ya estaba pagada, se suma el número de meses a la fecha de validez.
     * Si la cuota no estaba pagada, se establece la fecha de validez a partir
     * @param meses Número de meses por los que se paga la cuota.
     */
    public void pagarCuota() {
        this.cuotaPagada = true;
        LocalDate now = LocalDate.now();
        LocalDate nextMonthFirst = now.withDayOfMonth(1).plusMonths(1);
        if (this.cuotaValidaHasta == null || this.cuotaValidaHasta.isBefore(now)) {
            this.cuotaValidaHasta = nextMonthFirst;
        } else {
            this.cuotaValidaHasta = this.cuotaValidaHasta.withDayOfMonth(1).plusMonths(1);
        }
    }

    public void pagarCuota(int meses){
        this.cuotaPagada = true;
        if (this.cuotaValidaHasta == null) {
            this.cuotaValidaHasta = LocalDate.now().plusMonths(meses);
        } else {
            this.cuotaValidaHasta = this.cuotaValidaHasta.plusMonths(meses);
        }
    }

    public void asignarRutina(Rutina rutina) {
        if (rutina != null && !rutinas.contains(rutina)) {
            rutinas.add(rutina);
        }
    }

    public void eliminarRutina(Rutina rutina) {
        if (rutina != null && rutinas.contains(rutina)) {
            rutinas.remove(rutina);
        }
    }

    public void registrarAcceso(Acceso acceso) {
        accesos.add(acceso);
        acceso.setUsuario(this);
    }

    public boolean tieneRutina(Rutina rutina) {
        return rutinas.contains(rutina);
    }

    public void aniadirAcceso(Acceso acceso) {
        if (acceso != null && !accesos.contains(acceso)) {
            accesos.add(acceso);
            acceso.setUsuario(this);
        }
    }

    public void quitarMedalla(Medalla medalla) {
        if (medalla != null) {
            medallas.remove(medalla);
        }
    }

    public List<Acceso> historialAccesos() {
        return accesos;
    }
    public List<Rutina> getRutinas() {
        return rutinas;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTlf() {
        return tlf;
    }

    public void setTlf(String tlf) {
        this.tlf = tlf;
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

    public Boolean getCuotaPagada() {
        return cuotaPagada;
    }

    public void setCuotaPagada(Boolean cuotaPagada) {
        this.cuotaPagada = cuotaPagada;
    }

    public LocalDate getCuotaValidaHasta() {
        return cuotaValidaHasta;
    }

    public void setCuotaValidaHasta(LocalDate cuotaValidaHasta) {
        this.cuotaValidaHasta = cuotaValidaHasta;
    }

    public List<Medalla> getMedallas() {
        return medallas;
    }

    
}
