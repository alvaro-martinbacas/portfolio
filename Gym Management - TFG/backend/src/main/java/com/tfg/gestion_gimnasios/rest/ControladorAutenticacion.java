package com.tfg.gestion_gimnasios.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.tfg.gestion_gimnasios.entidades.Usuario;
import com.tfg.gestion_gimnasios.entidades.Entrenador;
import com.tfg.gestion_gimnasios.repositorios.RepositorioUsuario;
import com.tfg.gestion_gimnasios.repositorios.RepositorioEntrenador;
import com.tfg.gestion_gimnasios.rest.dto.DLogin;
import com.tfg.gestion_gimnasios.seguridad.JWTUtil;

@RestController
@RequestMapping("/gestiongimnasios/auth")
public class ControladorAutenticacion {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RepositorioUsuario usuarioRepo;

    @Autowired
    private RepositorioEntrenador entrenadorRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) DLogin login) {
        // Si no hay body, asumimos que es una validación de token
        if (login == null) {
            return ResponseEntity.ok().build();
        }
        String email = login.getEmail().toLowerCase();

        // Intentar autenticar como administrador
        if ("admin@gimnasio.com".equals(email)) {
            if (passwordEncoder.matches(login.getClave(), passwordEncoder.encode("admin1234"))) {
                String token = jwtUtil.generarToken(email, "ADMIN");
                return ResponseEntity.ok(Map.of(
                    "token", token,
                    "email", email,
                    "rol", "ADMIN"
                ));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales incorrectas"));
        }

        // Intentar autenticar como entrenador
        if (email.endsWith("@entrenador.com")) {
            Entrenador entrenador = entrenadorRepo.buscar(email).orElse(null);
            if (entrenador != null && passwordEncoder.matches(login.getClave(), entrenador.getClave())) {
                String token = jwtUtil.generarToken(email, "ENTRENADOR");
                return ResponseEntity.ok(Map.of(
                    "token", token,
                    "email", email,
                    "rol", "ENTRENADOR"
                ));
            }
        }

        Usuario usuario = usuarioRepo.buscar(email).orElse(null);
        if (usuario != null && passwordEncoder.matches(login.getClave(), usuario.getClave())) {
            String token = jwtUtil.generarToken(email, "USUARIO");
            return ResponseEntity.ok(Map.of(
                "token", token,
                "email", email,
                "rol", "USUARIO"
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Credenciales incorrectas"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().build();
    }
}
