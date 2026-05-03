package com.tfg.gestion_gimnasios.repositorios;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import com.tfg.gestion_gimnasios.entidades.Ejercicio;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional
public class RepositorioEjercicio {
    @PersistenceContext
    EntityManager em;

    public void guardar(Ejercicio ejercicio) {
        em.persist(ejercicio);
    }

    public void actualizar(Ejercicio ejercicio) {
        em.merge(ejercicio);
    }

    public void eliminar(int idEjercicio) {
        Ejercicio e = em.find(Ejercicio.class, idEjercicio);
        em.remove(em.merge(e));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Ejercicio> buscar(int id) {
        return Optional.ofNullable(em.find(Ejercicio.class, id));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Ejercicio> buscarPorNombre(String nombre) {
        return em.createQuery("SELECT e FROM Ejercicio e WHERE e.nombre = :nombre", Ejercicio.class)
                 .setParameter("nombre", nombre)
                 .getResultStream()
                 .findFirst();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Ejercicio> buscarTodos() {
        return em.createQuery("SELECT e FROM Ejercicio e", Ejercicio.class).getResultList();
    }

}