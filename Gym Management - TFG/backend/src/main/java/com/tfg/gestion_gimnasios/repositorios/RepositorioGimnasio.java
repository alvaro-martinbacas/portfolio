package com.tfg.gestion_gimnasios.repositorios;
import com.tfg.gestion_gimnasios.entidades.ClaseColectiva;
import com.tfg.gestion_gimnasios.entidades.Entrenador;
import com.tfg.gestion_gimnasios.entidades.Gimnasio;
import com.tfg.gestion_gimnasios.entidades.Usuario;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Transactional
@Repository
public class RepositorioGimnasio {
    @PersistenceContext
    EntityManager em;

    public void guardar(Gimnasio gimnasio) {
        em.persist(gimnasio);
    }

    public void actualizar(Gimnasio gimnasio) {
        em.merge(gimnasio);
        em.flush(); // Forzar sincronización inmediata
    }

    public void eliminar(int idGimnasio) {
        Gimnasio gimnasio = em.find(Gimnasio.class, idGimnasio);
        em.remove(em.merge(gimnasio));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Gimnasio> buscar(int id) {
        return Optional.ofNullable(em.find(Gimnasio.class, id));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Gimnasio> buscarPorNombre(String nombre) {
        return em.createQuery("SELECT g FROM Gimnasio g WHERE g.nombre = :nombre", Gimnasio.class)
                 .setParameter("nombre", nombre)
                 .getResultStream()
                 .findFirst();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Gimnasio> buscarPorDireccion(String direccion) {
        return em.createQuery("SELECT g FROM Gimnasio g WHERE g.direccion = :direccion", Gimnasio.class)
                 .setParameter("direccion", direccion)
                 .getResultStream()
                 .findFirst();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Gimnasio> buscarPorCadena(String nombreCadena) {
        return em.createQuery("SELECT g FROM Gimnasio g WHERE g.nombreCadena = :nombreCadena", Gimnasio.class)
                 .setParameter("nombreCadena", nombreCadena)
                 .getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<ClaseColectiva> buscarClasesPorGimnasio(int idGimnasio) {
        Gimnasio g = em.find(Gimnasio.class, idGimnasio);
        return g.clasesDisponibles();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Usuario> buscarUsuarioPorEmail(int idGimnasio, String email){
        Gimnasio g = em.find(Gimnasio.class, idGimnasio);
        return Optional.ofNullable(g.buscarUsuarioPorEmail(email));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<ClaseColectiva> buscarClasePorId(int idGimnasio, int idClase) {
        Gimnasio g = em.find(Gimnasio.class, idGimnasio);
        return Optional.ofNullable(g.buscarClasePorId(idClase));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Entrenador> buscarEntrenadorPorEmail(int idGimnasio, String email) {
        Gimnasio g = em.find(Gimnasio.class, idGimnasio);
        return Optional.ofNullable(g.buscarEntrenadorPorEmail(email));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Gimnasio> buscarTodos() {
        return em.createQuery("SELECT g FROM Gimnasio g", Gimnasio.class).getResultList();
    }
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Gimnasio> buscarPorUsuario(String emailUsuario) {
        return em.createQuery("SELECT g FROM Gimnasio g JOIN g.usuarios u WHERE u.email = :email", Gimnasio.class)
                 .setParameter("email", emailUsuario)
                 .getResultStream()
                 .findFirst();
    }
}
