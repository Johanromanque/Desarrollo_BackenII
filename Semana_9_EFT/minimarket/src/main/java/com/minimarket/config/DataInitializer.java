package com.minimarket.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, 
                           RolRepository rolRepository, 
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Crear el rol si no existe
        Rol rolAdmin = rolRepository.findByNombre("ROLE_USER")
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol("ROLE_USER");
                    return rolRepository.save(nuevoRol);
                });

        // 2. Crear el usuario si no existe
        if (!usuarioRepository.findByUsername("admin").isPresent()) {
            Usuario usuario = new Usuario();
            usuario.setUsername("admin");
            // Aquí se encripta de forma nativa con el BCrypt de tu SecurityConfig
            usuario.setPassword(passwordEncoder.encode("admin123")); 
            usuario.setRoles(Collections.singleton(rolAdmin));
            
            usuarioRepository.save(usuario);
            System.out.println("--> Usuario 'admin' creado exitosamente con contraseña encriptada nativa.");
        }
    }
}