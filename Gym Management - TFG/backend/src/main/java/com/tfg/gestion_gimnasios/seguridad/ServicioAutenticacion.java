package com.tfg.gestion_gimnasios.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tfg.gestion_gimnasios.entidades.Usuario;
import com.tfg.gestion_gimnasios.entidades.Entrenador;
import com.tfg.gestion_gimnasios.repositorios.RepositorioUsuario;
import com.tfg.gestion_gimnasios.repositorios.RepositorioEntrenador;

@Service
public class ServicioAutenticacion implements UserDetailsService {

    @Autowired
    private RepositorioUsuario repositorioUsuarios;
    
    @Autowired
    private RepositorioEntrenador repositorioEntrenadores;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        String emailLowerCase = email.toLowerCase();
        
        
        if ("admin@gimnasio.com".equals(emailLowerCase)) {
            return User
                .withUsername("admin@gimnasio.com")
                .password(passwordEncoder.encode("admin1234"))
                .roles("ADMIN")
                .build();
        }

        try {
            Entrenador entrenador = repositorioEntrenadores.buscar(emailLowerCase)
                .orElseThrow(() -> new UsernameNotFoundException(""));
                
            return User
                .withUsername(entrenador.getEmail())
                .password(entrenador.getClave())
                .roles("ENTRENADOR")
                .build();
        } catch (UsernameNotFoundException e) {
            Usuario usuario = repositorioUsuarios.buscar(emailLowerCase)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró ningún usuario o entrenador con el email: " + emailLowerCase));
            
            return User
                .withUsername(usuario.getEmail())
                .password(usuario.getClave())
                .roles("USUARIO")
                .build();
        }
    }
}
