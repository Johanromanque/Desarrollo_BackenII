package com.minimarket.security;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Limpiamos datos previos de pruebas que hayan quedado a medias en H2
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        // 1. Creamos e insertamos el rol con el formato exacto que espera el controlador
        Rol rolGerente = new Rol();
        rolGerente.setNombre("ROLE_GERENTE");
        rolGerente = rolRepository.save(rolGerente);

        Rol rolCliente = new Rol();
        rolCliente.setNombre("ROLE_CLIENTE");
        rolRepository.save(rolCliente);

        // 2. Creamos el usuario administrador y le enlazamos el rol guardado
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123"));
        
        // Inicializamos el Set de roles para evitar NullPointerException
        admin.setRoles(new HashSet<>());
        admin.getRoles().add(rolGerente);
        
        usuarioRepository.save(admin);
        
        System.out.println("--> [OK] Base de datos H2 reiniciada con éxito.");
        System.out.println("--> [OK] Usuario: admin | Clave: 123 | Rol: ROLE_GERENTE");
    }
}