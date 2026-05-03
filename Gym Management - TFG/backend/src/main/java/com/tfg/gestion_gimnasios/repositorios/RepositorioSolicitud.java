package com.tfg.gestion_gimnasios.repositorios;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import com.tfg.gestion_gimnasios.entidades.Solicitud;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Repository
@Transactional
public class RepositorioSolicitud {
    @PersistenceContext
    EntityManager em;

    public void guardar(Solicitud solicitud) {
        em.persist(solicitud);
    }

    public void actualizar(Solicitud solicitud) {
        em.merge(solicitud);
    }

    public void eliminar(int idSolicitud) {
        Solicitud solicitud = em.find(Solicitud.class, idSolicitud);
        em.remove(em.merge(solicitud));
    }

    public List<Solicitud> buscarTodas() {
        return em.createQuery("SELECT s FROM Solicitud s", Solicitud.class)
                .getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Solicitud> buscar(int id) {
        return Optional.ofNullable(em.find(Solicitud.class, id));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Solicitud> solicitudesCaducadas(LocalDate fechaActual) {
        return em.createQuery("SELECT s FROM Solicitud s WHERE s.fechaSolicitud < :fechaActual", Solicitud.class)
                .setParameter("fechaActual", fechaActual)
                .getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Solicitud> buscarSolicitudesPorUsuario(String emailUsuario) {
        return em.createQuery("SELECT s FROM Solicitud s WHERE s.usuario.email = :emailUsuario", Solicitud.class)
                .setParameter("emailUsuario", emailUsuario)
                .getResultList();
    }
}