package com.tfg.gestion_gimnasios.excepciones;

public class EjercicioNoSePuedeEliminar extends RuntimeException {
    public EjercicioNoSePuedeEliminar() {
        super("No se puede eliminar el ejercicio porque está siendo utilizado en alguna rutina.");
    }
}
