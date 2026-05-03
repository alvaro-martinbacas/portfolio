package com.tfg.gestion_gimnasios.repositorios;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.tfg.gestion_gimnasios.entidades.EjercicioRutina;


@Repository
@Transactional
public class RepositorioEjercicioRutina {
    @PersistenceContext
    EntityManager em;

    public void guardar(EjercicioRutina er) {
        em.persist(er);
    }

    public void actualizar(EjercicioRutina er) {
        em.merge(er);
    }

    public void eliminar(int idEr) {
        EjercicioRutina er = em.find(EjercicioRutina.class, idEr);
        em.remove(em.merge(er));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<EjercicioRutina> buscar(int id) {
        return Optional.ofNullable(em.find(EjercicioRutina.class, id));
    }
}

