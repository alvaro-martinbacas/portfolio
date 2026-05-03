package com.tfg.gestion_gimnasios.repositorios;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.tfg.gestion_gimnasios.entidades.Medalla;


@Repository
@Transactional
public class RepositorioMedalla {
    @PersistenceContext
    EntityManager em;

    public void guardar(Medalla medalla) {
        em.persist(medalla);
    }

    public void actualizar(Medalla medalla) {
        em.merge(medalla);
    }

    public void eliminar(int id) {
        Medalla medalla = em.find(Medalla.class, id);
        em.remove(em.merge(medalla));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Medalla> buscar(int id) {
        return Optional.ofNullable(em.find(Medalla.class, id));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Medalla> buscarTodas() {
        return em.createQuery("SELECT m FROM Medalla m", Medalla.class)
                    .getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Medalla> findByTipo(String tipo) {
        return em.createQuery("SELECT m FROM Medalla m WHERE m.tipo = :tipo", Medalla.class)
                 .setParameter("tipo", tipo)
                 .getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Medalla> buscarPorTipo(String tipo) {
        return em.createQuery("SELECT m FROM Medalla m WHERE m.tipo = :tipo", Medalla.class)
                 .setParameter("tipo", tipo)
                 .getResultList();
    }
}

