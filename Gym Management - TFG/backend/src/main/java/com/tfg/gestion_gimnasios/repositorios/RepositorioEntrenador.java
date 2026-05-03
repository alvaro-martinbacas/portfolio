package com.tfg.gestion_gimnasios.repositorios;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import com.tfg.gestion_gimnasios.entidades.Entrenador;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional
public class RepositorioEntrenador {
    @PersistenceContext
    EntityManager em;

    public void guardar(Entrenador entrenador) {
        em.persist(entrenador);
    }

    public void actualizar(Entrenador entrenador) {
        em.merge(entrenador);
    }

    public void eliminar(String email) {
        Entrenador entrenador = em.find(Entrenador.class, email);
        em.remove(em.merge(entrenador));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Entrenador> buscar(String email) {
        return Optional.ofNullable(em.find(Entrenador.class, email));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Entrenador> buscarPorTLF(String tlf) {
        return em.createQuery("SELECT e FROM Entrenador e WHERE e.tlf = :tlf", Entrenador.class)
                 .setParameter("tlf", tlf)
                 .getResultStream()
                 .findFirst();
    }
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    Optional<Entrenador> findByEmailAndClaveAndActivoTrue(String email, String clave){
        return em.createQuery("SELECT e FROM Entrenador e WHERE e.email = :email AND e.clave = :clave AND e.activo = true", Entrenador.class)
                 .setParameter("email", email)
                 .setParameter("clave", clave)
                 .getResultStream()
                 .findFirst();
    }
}