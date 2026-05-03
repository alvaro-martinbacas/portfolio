package com.tfg.gestion_gimnasios.repositorios;
import java.util.Optional;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.tfg.gestion_gimnasios.entidades.Acceso;

import jakarta.persistence.Query;
import java.time.LocalDateTime;

@Repository
@Transactional
public class RepositorioAcceso {
    @PersistenceContext
    EntityManager em;

    public void guardar(Acceso acceso) {
        em.persist(acceso);
    }

    public void actualizar(Acceso acceso) {
        em.merge(acceso);
    }

    public void eliminar(Acceso acceso) {
        em.remove(acceso);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Acceso> buscar(int id) {
        return Optional.ofNullable(em.find(Acceso.class, id));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Acceso> buscarTodos() {
        return em.createQuery("SELECT a FROM Acceso a", Acceso.class).getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public int consultarAforo(Long idGimnasio, LocalDateTime momento) {
        // El aforo en un momento concreto es el número de accesos cuya horaEntrada <= momento y (horaSalida > momento o horaSalida es null)
        //System.out.println("[Aforo] Consulta en repositorio: gimnasio=" + idGimnasio + ", momento=" + momento);
        Query query = em.createQuery(
            "SELECT COUNT(a) FROM Acceso a WHERE a.gimnasio.id = :idGimnasio " +
            "AND a.horaEntrada <= :momento " +
            "AND (a.horaSalida IS NULL OR a.horaSalida > :momento)",
            Long.class);
        query.setParameter("idGimnasio", idGimnasio);
        query.setParameter("momento", momento);
        int aforo = ((Number) query.getSingleResult()).intValue();
        //System.out.println("[Aforo] Total presentes: " + aforo);
        return aforo;
    }
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Acceso buscarUltimoAccesoPorUsuario(String emailUsuario) {
        return em.createQuery(
            "SELECT a FROM Acceso a WHERE a.usuario.email = :emailUsuario ORDER BY a.horaEntrada DESC", Acceso.class)
            .setParameter("emailUsuario", emailUsuario)
            .setMaxResults(1)
            .getResultStream()
            .findFirst()
            .orElse(null);
    }
}