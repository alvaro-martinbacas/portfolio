package com.tfg.gestion_gimnasios.repositorios;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import com.tfg.gestion_gimnasios.entidades.Acceso;
import com.tfg.gestion_gimnasios.entidades.Rutina;
import com.tfg.gestion_gimnasios.entidades.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional
public class RepositorioUsuario {
    @PersistenceContext
    EntityManager em;

    public void guardar(Usuario usuario) {
        em.persist(usuario);
    }

    public void actualizar(Usuario usuario) {
        em.merge(usuario);
    }

    public void eliminar(String email) {
        Usuario usuario = em.find(Usuario.class, email);
        em.remove(em.merge(usuario));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Usuario> buscar(String email) {
        return Optional.ofNullable(em.find(Usuario.class, email));
    }

    public Optional<Usuario> buscarPorTLF(String tlf) {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.tlf = :tlf", Usuario.class)
                 .setParameter("tlf", tlf)
                 .getResultStream()
                 .findFirst();
    }

    public List<Usuario> usuariosCuotaCaducaHoy(){
        return em.createQuery("SELECT u FROM Usuario u WHERE u.cuotaValidaHasta = :hoy", Usuario.class)
                 .setParameter("hoy", LocalDate.now())
                 .getResultList();
    }

    public Optional<Acceso> buscarUltimoAcceso(String email) {
        return Optional.ofNullable(em.createQuery(
                "SELECT a FROM Acceso a WHERE a.usuario.email = :email AND a.horaSalida IS NULL ORDER BY a.horaEntrada DESC",
                Acceso.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null));
    }

    public List<Usuario> findAll() {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.email <> :adminEmail", Usuario.class)
                 .setParameter("adminEmail", "admin@gimnasio.com")
                 .getResultList();
    }

    public List<Rutina> buscarRutinasPorUsuario(String emailUsuario) {
        Usuario usuario = em.find(Usuario.class, emailUsuario);
        if (usuario != null) {
            return usuario.getRutinas();
        } else {
            return List.of();
        }
    }
}
