package com.tfg.gestion_gimnasios.repositorios;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import com.tfg.gestion_gimnasios.entidades.Rutina;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;



@Repository
@Transactional
public class RepositorioRutina {
    @PersistenceContext
    EntityManager em;

    public void guardar(Rutina rutina) {
        em.persist(rutina);
    }

    public void actualizar(Rutina rutina) {
        em.merge(rutina);
    }

    public void eliminar(int idRutina) {
        Rutina r = em.find(Rutina.class, idRutina);
        em.remove(em.merge(r));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Rutina> buscar(int id) {
        return Optional.ofNullable(em.find(Rutina.class, id));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Rutina> buscarPorEntrenador(String emailEntrenador) {
        return em.createQuery("SELECT r FROM Rutina r WHERE r.entrenador.email = :email", Rutina.class)
                 .setParameter("email", emailEntrenador)
                 .getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Rutina> buscarTodas() {
        return em.createQuery("SELECT r FROM Rutina r", Rutina.class).getResultList();
    }


}
