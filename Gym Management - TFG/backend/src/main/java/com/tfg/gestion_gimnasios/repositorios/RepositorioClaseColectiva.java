package com.tfg.gestion_gimnasios.repositorios;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import com.tfg.gestion_gimnasios.entidades.ClaseColectiva;
import com.tfg.gestion_gimnasios.entidades.Solicitud;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional
public class RepositorioClaseColectiva {
    @PersistenceContext
    EntityManager em;

    public void guardar(ClaseColectiva clase) {
        em.persist(clase);
    }

    public void actualizar(ClaseColectiva clase) {
        em.merge(clase);
    }

    public void eliminar(int idClase) {
        ClaseColectiva clase = em.find(ClaseColectiva.class, idClase);
        em.remove(em.merge(clase));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<ClaseColectiva> buscar(int id) {
        return Optional.ofNullable(em.find(ClaseColectiva.class, id));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Solicitud> buscarSolicitudesPorClase(int idClase) {
        ClaseColectiva clase = em.find(ClaseColectiva.class, idClase);
        return clase.getSolicitudes();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<ClaseColectiva> buscarTodas() {
        return em.createQuery("SELECT c FROM ClaseColectiva c", ClaseColectiva.class).getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<ClaseColectiva> buscarClasePorSolicitud(Solicitud solicitud) {
        List<ClaseColectiva> clases = em.createQuery("SELECT c FROM ClaseColectiva c JOIN c.solicitudes s WHERE s.id = :idSolicitud", ClaseColectiva.class)
            .setParameter("idSolicitud", solicitud.getId())
            .getResultList();
        if (clases.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(clases.get(0));
        }
    }
    
}