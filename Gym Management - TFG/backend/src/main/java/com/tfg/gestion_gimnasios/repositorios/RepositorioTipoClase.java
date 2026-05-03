package com.tfg.gestion_gimnasios.repositorios;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;
import com.tfg.gestion_gimnasios.entidades.TipoClase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Repository
@Transactional
public class RepositorioTipoClase {
    @PersistenceContext
    EntityManager em;

    public void guardar(TipoClase tipoClase) {
        em.persist(tipoClase);
    }

    public void actualizar(TipoClase tipoClase) {
        em.merge(tipoClase);
    }

    public void eliminar(String nombre) {
        TipoClase t = em.find(TipoClase.class, nombre);
        em.remove(em.merge(t));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<TipoClase> buscar(String nombre) {
        return Optional.ofNullable(em.find(TipoClase.class, nombre));
    }
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<TipoClase> findAll() {
        return em.createQuery("SELECT t FROM TipoClase t", TipoClase.class).getResultList();
    }
}

