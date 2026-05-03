package com.tfg.gestion_gimnasios.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.tfg.gestion_gimnasios.entidades.*;
import com.tfg.gestion_gimnasios.excepciones.*;
import com.tfg.gestion_gimnasios.repositorios.*;

import jakarta.transaction.Transactional;

@SpringBootTest(classes = com.tfg.gestion_gimnasios.app.GestionGimnasiosApplication.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ServicioGimnasioTest {


    @Autowired
    ServicioGimnasio servicio;

    @Autowired
    RepositorioGimnasio gimnasioRepo;

    @Test
    void testNuevoGimnasio() {

        Gimnasio g = new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        );

        servicio.nuevoGimnasio(g);

        assertTrue(servicio.buscarGimnasio(g.getId()) != null);
    }


    @Test
    void crearGimnasiosGeneraIdCorrecto(){
        Gimnasio g1 = new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        );
        Gimnasio g2 = new Gimnasio(
            "FitZone Norte", "Calle Norte 456", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        );
        Gimnasio g3 = new Gimnasio(
            "FitZone Sur", "Calle Sur 789", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        );

        servicio.nuevoGimnasio(g1);
        servicio.nuevoGimnasio(g2);
        servicio.nuevoGimnasio(g3);

        var gimnasios = gimnasioRepo.buscarTodos();
        assertEquals(3, gimnasios.size());
        assertEquals(gimnasios.get(0).getId(), 1);
        assertEquals(gimnasios.get(1).getId(), 2);
        assertEquals(gimnasios.get(2).getId(), 3);
    }

    @Test
    void testActualizarGimnasio() {
        Gimnasio g = new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        );

        servicio.nuevoGimnasio(g);

        g.setNombre("FitZone Centro Renovado");
        g.setTelefono("123456789");
        
        servicio.actualizarGimnasio(g);

        
        Gimnasio actualizado = servicio.buscarGimnasio(g.getId());
        assertTrue(actualizado != null);
        assertEquals("FitZone Centro Renovado", actualizado.getNombre());
        assertEquals("123456789", actualizado.getTelefono());
    }

    @Test
    void testEliminarGimnasio() {
        Gimnasio g = new Gimnasio(
            "FitZone Centro2", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        );

        servicio.nuevoGimnasio(g);

        assertTrue(servicio.buscarGimnasio(g.getId()) != null);

        servicio.eliminarGimnasio(g.getId());

        assertThrows(GimnasioNoEncontrado.class, () -> servicio.buscarGimnasio(g.getId()));
    }

    @Test
    @Transactional
    void testNuevoUsuario() {
        Gimnasio g = new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        );
        servicio.nuevoGimnasio(g);

        Usuario u = new Usuario(
            "Juan", "Pérez", "600111222", "juan.perez@email.com", "pass1234", false, LocalDate.now().plusMonths(1)
        );

        servicio.registroUsuario(g, u);

        assertTrue(servicio.buscarUsuario("juan.perez@email.com").isPresent());
    }

    @Test
    @Transactional

    void testNuevoUsuarioValoresReservados() {
        Gimnasio g = new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        );
        servicio.nuevoGimnasio(g);

        Usuario u = new Usuario(
            "Admin", "Apellido", "600111222", "otro@email.com", "pass1234", false, LocalDate.now().plusMonths(1)
        );

        Exception ex = org.junit.jupiter.api.Assertions.assertThrows(
            com.tfg.gestion_gimnasios.excepciones.ValoresReservadosParaDireccion.class,
            () -> servicio.registroUsuario(g, u)
        );
        assertEquals(com.tfg.gestion_gimnasios.excepciones.ValoresReservadosParaDireccion.class, ex.getClass());
    }

    @Test
    @Transactional
    void testBuscarUsuario() {
        Gimnasio g = new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        );
        servicio.nuevoGimnasio(g);

        Usuario u = new Usuario(
            "Ana", "López", "600333444", "ana.lopez@email.com", "pass5678", false, LocalDate.now().plusMonths(1)
        );
        servicio.registroUsuario(g, u);

        var encontrado = servicio.buscarUsuario("ana.lopez@email.com");
        assertTrue(encontrado.isPresent());
        assertEquals("Ana", encontrado.get().getNombre());
    }

    @Test
    @Transactional
    void testActualizarUsuario() {
        Gimnasio g = new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        );
        servicio.nuevoGimnasio(g);

        Usuario u = new Usuario(
            "Luis", "Martínez", "600555666", "luis.martinez@email.com", "pass9999", false, LocalDate.now().plusMonths(1)
        );
        servicio.registroUsuario(g, u);

        u.setNombre("Luis Actualizado");
        u.setTlf("600000000");
        servicio.actualizarUsuario(u);

        var actualizado = servicio.buscarUsuario("luis.martinez@email.com");
        assertTrue(actualizado.isPresent());
        assertEquals("Luis Actualizado", actualizado.get().getNombre());
        assertEquals("600000000", actualizado.get().getTlf());
    }

    @Test
    @Transactional
    void testEliminarUsuario() {
        Gimnasio g = new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        );
        servicio.nuevoGimnasio(g);

        Usuario u = new Usuario(
            "Carlos", "Ruiz", "600777888", "carlos.ruiz@email.com", "pass0000", false, LocalDate.now().plusMonths(1)
        );
        servicio.registroUsuario(g, u);

        assertTrue(servicio.buscarUsuario("carlos.ruiz@email.com").isPresent());
        int usuariosAntes = servicio.buscarUsuariosPorGimnasio(g.getId()).size();

        servicio.eliminarUsuario(g.getId(), "carlos.ruiz@email.com");

        assertTrue(servicio.buscarUsuario("carlos.ruiz@email.com").isEmpty());
        int usuariosDespues = servicio.buscarUsuariosPorGimnasio(g.getId()).size();
        assertEquals(usuariosAntes - 1, usuariosDespues);

    }

    @Test
    @Transactional
    void testPagarCuota() {
        Gimnasio g = new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        );
        servicio.nuevoGimnasio(g);

        Usuario u = new Usuario(
            "Mario", "Sánchez", "600999000", "mario.sanchez@email.com", "pass1111", false, LocalDate.now().plusMonths(1)
        );
        servicio.registroUsuario(g, u);

        LocalDate fechaAnterior = u.getCuotaValidaHasta();
        servicio.pagarCuota("mario.sanchez@email.com");

        Usuario actualizado = servicio.buscarUsuario("mario.sanchez@email.com").get();
        assertEquals(fechaAnterior.plusMonths(1), actualizado.getCuotaValidaHasta());
    }

    @Test
    @Transactional
    void testEliminarEntrenadorNoExistenteLanzaExcepcion() {
        Gimnasio g = servicio.nuevoGimnasio(new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        ));
        Exception ex = Assertions.assertThrows(
            EntrenadorNoEncontrado.class,
            () -> servicio.desactivarEntrenador(g.getId(), "noexiste@email.com")
        );
        assertEquals(EntrenadorNoEncontrado.class, ex.getClass());
    }

    @Test
    @Transactional
    void testAniadirEjercicioRutinaAumentaLista() {
        Gimnasio g = servicio.nuevoGimnasio(new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        ));
        Usuario u = new Usuario(
            "Juan", "Pérez", "600111222", "juan.perez@email.com", "pass1234", false, LocalDate.now().plusMonths(1)
        );
        servicio.registroUsuario(g, u);
        Entrenador entrenador = new Entrenador(
            "Pedro Entrenador", "pedro.entrenador@email.com", "clave1234", "600123456", true
        );
        servicio.nuevoEntrenador(g, entrenador);

        Rutina rutina = new Rutina("Rutina 1", "desc");
        servicio.nuevaRutina(entrenador.getEmail(), u.getEmail(), g.getId(), rutina);

        Ejercicio ejercicio = new Ejercicio("Press Banca", "Pecho", "Descripcion", "-");
        servicio.nuevoEjercicio(ejercicio);

        EjercicioRutina ejercicioRutina = new EjercicioRutina(3, 10, 60, "Realizar con cuidado");
        int antes = rutina.getEjerciciosRutina().size();
        servicio.aniadirEjercicioRutina(entrenador.getEmail(), rutina.getId(), ejercicioRutina, ejercicio.getId());
        int despues = rutina.getEjerciciosRutina().size();

        assertEquals(antes + 1, despues);
    }

    @Test
    @Transactional
    void testEliminarEjercicioRutinaReduceLista() {
        Gimnasio g = servicio.nuevoGimnasio(new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        ));

        Gimnasio gServicio = servicio.buscarGimnasio(g.getId());
        Usuario u = new Usuario(
            "Juan", "Pérez", "600111222", "juan.perez@email.com", "pass1234", false, LocalDate.now().plusMonths(1)
        );
        servicio.registroUsuario(gServicio, u);

        Usuario usuServicio = servicio.buscarUsuario(u.getEmail()).orElseThrow();
        Entrenador entrenador = new Entrenador(
            "Pedro Entrenador", "pedro.entrenador@email.com", "clave1234", "600123456", true
        );
        servicio.nuevoEntrenador(gServicio, entrenador);
        Entrenador entrenServicio = servicio.buscarEntrenador(entrenador.getEmail()).orElseThrow();
        Rutina rutina = new Rutina("Rutina 1", "desc");
        servicio.nuevaRutina(entrenServicio.getEmail(), usuServicio.getEmail(), gServicio.getId(), rutina);

        Ejercicio ejercicio = new Ejercicio("Press Banca", "Pecho", "Descripcion", "-");
        servicio.nuevoEjercicio(ejercicio);

        EjercicioRutina ejercicioRutina = new EjercicioRutina(3, 10, 60, "Realizar con cuidado");
        servicio.aniadirEjercicioRutina(entrenServicio.getEmail(), rutina.getId(), ejercicioRutina, ejercicio.getId());

        int antes = rutina.getEjerciciosRutina().size();
        servicio.eliminarEjercicioRutina(entrenServicio.getEmail(), rutina.getId(), ejercicioRutina.getId());
        int despues = rutina.getEjerciciosRutina().size();

        assertEquals(antes - 1, despues);
    }

    @Test
    @Transactional
    void testEliminarEjercicioRutinaNoPerteneceARutinaLanzaExcepcion() {
        Gimnasio g = servicio.nuevoGimnasio(new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        ));

        Gimnasio g1 = servicio.buscarGimnasio(g.getId());
        Usuario u = new Usuario(
            "Juan", "Pérez", "600111222", "juan.perez@email.com", "pass1234", false, LocalDate.now().plusMonths(1)
        );
        servicio.registroUsuario(g1, u);

        Usuario u1 = servicio.buscarUsuario(u.getEmail()).orElseThrow();

        Entrenador entrenador = new Entrenador(
            "Pedro Entrenador", "pedro.entrenador@email.com", "clave1234", "600123456", true
        );
        servicio.nuevoEntrenador(g1, entrenador);

        Rutina rutina = new Rutina("Rutina 1", "desc");
        servicio.nuevaRutina(entrenador.getEmail(), u1.getEmail(), g1.getId(), rutina);

        Rutina rutina2 = new Rutina("Rutina 2", "descr");
        servicio.nuevaRutina(entrenador.getEmail(), u1.getEmail(), g1.getId(), rutina2);

        Ejercicio ejercicio = new Ejercicio("Press Banca", "Pecho", "Descripcion", "-");
        servicio.nuevoEjercicio(ejercicio);

        EjercicioRutina ejercicioRutina = new EjercicioRutina(3, 10, 60, "Realizar con cuidado");
        servicio.aniadirEjercicioRutina(entrenador.getEmail(), rutina.getId(), ejercicioRutina, ejercicio.getId());

        Exception ex = assertThrows(
            EjercicioRutinaNoPerteneceARutina.class,
            () -> servicio.eliminarEjercicioRutina(entrenador.getEmail(), rutina2.getId(), ejercicioRutina.getId())
        );
        assertEquals(EjercicioRutinaNoPerteneceARutina.class, ex.getClass());
    }

    @Test
    @Transactional
    void testEliminarRutinaReduceLista() {
        Gimnasio g = servicio.nuevoGimnasio(new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        ));
        Usuario u = new Usuario(
            "Juan", "Pérez", "600111222", "juan.perez@email.com", "pass1234", false, LocalDate.now().plusMonths(1)
        );
        servicio.registroUsuario(g, u);
        Usuario u1 = servicio.buscarUsuario(u.getEmail()).orElseThrow();
        Entrenador entrenador = new Entrenador(
            "Pedro Entrenador", "pedro.entrenador@email.com", "clave1234", "600123456", true
        );
        servicio.nuevoEntrenador(g, entrenador);

        Rutina rutina = new Rutina("Rutina 1", "desc");
        servicio.nuevaRutina(entrenador.getEmail(), u1.getEmail(), g.getId(), rutina);

        int antesUsuario = u1.getRutinas().size();
        int antesEntrenador = servicio.buscarRutinasPorEntrenador(entrenador.getEmail()).size();
        
        servicio.eliminarRutina(rutina.getId(), entrenador.getEmail());
        
        // Volver a obtener el usuario actualizado y las rutinas del entrenador de la base de datos
        Usuario usuarioActualizado = servicio.buscarUsuario(u1.getEmail()).orElseThrow();
        int despuesUsuario = usuarioActualizado.getRutinas().size();
        int despuesEntrenador = servicio.buscarRutinasPorEntrenador(entrenador.getEmail()).size();

        assertEquals(antesUsuario - 1, despuesUsuario, "La lista de rutinas del usuario debería reducirse en 1");
        assertEquals(antesEntrenador - 1, despuesEntrenador, "La lista de rutinas del entrenador debería reducirse en 1");
    }

    @Test
    @Transactional
    void testEliminarRutinaNoExistenteLanzaExcepcion() {
        Gimnasio g = servicio.nuevoGimnasio(new Gimnasio(
            "FitZone Centro", "Calle Mayor 123", "987654321",
            LocalTime.of(8, 0), LocalTime.of(22, 0)
        ));
        Usuario u = new Usuario(
            "Juan", "Pérez", "600111222", "juan.perez@email.com", "pass1234", false, LocalDate.now().plusMonths(1)
        );
        servicio.registroUsuario(g, u);
        Entrenador entrenador = new Entrenador(
            "Pedro Entrenador", "pedro.entrenador@email.com", "clave1234", "600123456", true
        );
        servicio.nuevoEntrenador(g, entrenador);

        Exception ex = Assertions.assertThrows(
            RutinaNoEncontrada.class,
            () -> servicio.eliminarRutina(999, entrenador.getEmail())
        );
        assertEquals(RutinaNoEncontrada.class, ex.getClass());
    }

    @Test
    @Transactional
    void testBuscarUsuariosPorGimnasio() {
        Gimnasio g = servicio.nuevoGimnasio(new Gimnasio(
            "Centro", "Calle 1", "900000001", LocalTime.of(8, 0), LocalTime.of(22, 0)
        ));
        Usuario u1 = new Usuario("Pepe", "García", "600000001", "pepe@email.com", "pass1234", false, LocalDate.now().plusMonths(1));
        Usuario u2 = new Usuario("Ana", "Martín", "600000002", "ana@email.com", "pass1234", false, LocalDate.now().plusMonths(1));
        servicio.registroUsuario(g, u1);
        servicio.registroUsuario(g, u2);

        var usuarios = servicio.buscarUsuariosPorGimnasio(g.getId());
        assertEquals(2, usuarios.size());
        assertTrue(usuarios.stream().anyMatch(u -> u.getEmail().equals("pepe@email.com")));
        assertTrue(usuarios.stream().anyMatch(u -> u.getEmail().equals("ana@email.com")));
    }

    @Test
    @Transactional
    void testBuscarUsuarioPorTlf() {
        Gimnasio g = servicio.nuevoGimnasio(new Gimnasio(
            "Centro", "Calle 1", "900000001", LocalTime.of(8, 0), LocalTime.of(22, 0)
        ));
        Usuario u = new Usuario("Pepe", "García", "600000003", "pepe2@email.com", "pass1234", false, LocalDate.now().plusMonths(1));
        servicio.registroUsuario(g, u);

        var encontrado = servicio.buscarUsuarioPorTlf("600000003");
        assertTrue(encontrado.isPresent());
        assertEquals("pepe2@email.com", encontrado.get().getEmail());
    }

    

    @Test
    @Transactional
    void testBuscarEntrenadoresPorGimnasio() {
        Gimnasio g = servicio.nuevoGimnasio(new Gimnasio(
            "Centro", "Calle 1", "900000001", LocalTime.of(8, 0), LocalTime.of(22, 0)
        ));
        Entrenador e1 = new Entrenador("Pedro", "pedro@email.com", "clave1234", "600123000", true);
        Entrenador e2 = new Entrenador("Ana", "ana@email.com", "clave1234", "600123001", true);
        servicio.nuevoEntrenador(g, e1);
        servicio.nuevoEntrenador(g, e2);

        var entrenadores = servicio.buscarEntrenadoresPorGimnasio(g.getId());
        assertEquals(2, entrenadores.size());
        assertTrue(entrenadores.stream().anyMatch(e -> e.getEmail().equals("pedro@email.com")));
        assertTrue(entrenadores.stream().anyMatch(e -> e.getEmail().equals("ana@email.com")));
    }

    @Test
    @Transactional
    void testBuscarEntrenador() {
        Gimnasio g = servicio.nuevoGimnasio(new Gimnasio(
            "Centro", "Calle 1", "900000001", LocalTime.of(8, 0), LocalTime.of(22, 0)
        ));
        Entrenador e = new Entrenador("Pedro", "pedro@email.com", "clave1234", "600123000", true);
        servicio.nuevoEntrenador(g, e);

        var encontrado = servicio.buscarEntrenador("pedro@email.com");
        assertTrue(encontrado.isPresent());
        assertEquals("Pedro", encontrado.get().getNombre());
    }

    @Test
    @Transactional
    void testBuscarEjercicioRutina() {
        Gimnasio g = servicio.nuevoGimnasio(new Gimnasio(
            "Centro", "Calle 1", "900000001", LocalTime.of(8, 0), LocalTime.of(22, 0)
        ));
        Usuario u = new Usuario("Pepe", "García", "600000004", "pepe3@email.com", "pass1234", false, LocalDate.now().plusMonths(1));
        servicio.registroUsuario(g, u);
        Entrenador e = new Entrenador("Pedro", "pedro@email.com", "clav1234e", "600123002", true);
        servicio.nuevoEntrenador(g, e);
        Rutina rutina = new Rutina("Rutina", "desc");
        servicio.nuevaRutina(e.getEmail(), u.getEmail(), g.getId(), rutina);
        Ejercicio ejercicio = new Ejercicio("Sentadilla", "Piernas", "desc", "-");
        servicio.nuevoEjercicio(ejercicio);
        EjercicioRutina er = new EjercicioRutina(3, 10, 60, "desc");
        servicio.aniadirEjercicioRutina(e.getEmail(), rutina.getId(), er, ejercicio.getId());

        var encontrado = servicio.buscarEjercicioRutina(er.getId());
        assertTrue(encontrado.isPresent());
        assertEquals(er.getId(), encontrado.get().getId());
    }

    @Test
    @Transactional
    void testNuevoEjercicio() {
        Ejercicio ejercicio = new Ejercicio("Dominadas", "Espalda", "desc", "-");
        Ejercicio creado = servicio.nuevoEjercicio(ejercicio);
        assertEquals("Dominadas", creado.getNombre());
        assertTrue(creado.getId() > 0);
    }

    @Test
    @Transactional
    void testAniadirEjercicioAEjercicioRutina() {
        Gimnasio g = servicio.nuevoGimnasio(new Gimnasio(
            "Centro", "Calle 1", "900000001", LocalTime.of(8, 0), LocalTime.of(22, 0)
        ));
        Usuario u = new Usuario("Pepe", "García", "600000005", "pepe4@email.com", "pass1234", false, LocalDate.now().plusMonths(1));
        servicio.registroUsuario(g, u);
        Entrenador e = new Entrenador("Pedro", "pedro@email.com", "clave1234", "600123003", true);
        servicio.nuevoEntrenador(g, e);
        Rutina rutina = new Rutina("Rutina", "desc");
        servicio.nuevaRutina(e.getEmail(), u.getEmail(), g.getId(), rutina);
        Ejercicio ejercicio = new Ejercicio("Remo", "Espalda", "desc", "-");
        servicio.nuevoEjercicio(ejercicio);
        EjercicioRutina er = new EjercicioRutina(3, 10, 60, "desc");
        servicio.aniadirEjercicioRutina(e.getEmail(), rutina.getId(), er, ejercicio.getId());

        Ejercicio ejercicio2 = new Ejercicio("Curl", "Biceps", "desc", "-");
        servicio.nuevoEjercicio(ejercicio2);

        servicio.aniadirEjercicioAEjercicioRutina(ejercicio2.getId(), er.getId());
        var actualizado = servicio.buscarEjercicioRutina(er.getId()).orElseThrow();
        assertEquals("Curl", actualizado.getEjercicio().getNombre());
    }

    @Test
    @Transactional
    void testAsignarYQuitarEntrenadorClaseColectiva() {
        Gimnasio g = new Gimnasio(
            "Centro", "Calle 1", "900000001", LocalTime.of(8, 0), LocalTime.of(22, 0)
        );
        servicio.nuevoGimnasio(g);

        Gimnasio gBuscado = servicio.buscarGimnasio(g.getId());

        Entrenador entrenador = new Entrenador("Pedro", "pedro@email.com", "clave1234", "600123003", true);
        servicio.nuevoEntrenador(gBuscado, entrenador);

        Entrenador entrenadorBuscado = servicio.buscarEntrenador(entrenador.getEmail()).orElseThrow();

        TipoClase tipoClase = new TipoClase("Yoga", "Clase de yoga", 20);
        servicio.nuevoTipoClase(tipoClase);

        TipoClase tipoClaseBuscado = servicio.buscarTipoClase(tipoClase.getNombre()).orElseThrow();

        ClaseColectiva clase = new ClaseColectiva(DayOfWeek.MONDAY,
            LocalTime.of(10, 0), LocalTime.of(11, 0)
        );
        servicio.nuevaClaseColectiva(gBuscado.getId(), clase, tipoClaseBuscado.getNombre());

        ClaseColectiva claseBuscada = servicio.buscarClase(clase.getId()).orElseThrow();

        servicio.anadirEntrenadorAClaseColectiva(claseBuscada.getId(), entrenadorBuscado.getEmail());

        ClaseColectiva claseConEntrenador = servicio.buscarClase(claseBuscada.getId()).orElseThrow();
        assertEquals(entrenadorBuscado.getEmail(), claseConEntrenador.getEntrenador().getEmail());

        servicio.quitarEntrenadorClaseColectiva(claseConEntrenador.getId());

        ClaseColectiva claseSinEntrenador = servicio.buscarClase(claseConEntrenador.getId()).orElseThrow();
        assertTrue(claseSinEntrenador.getEntrenador() == null);
    }
        
    
}