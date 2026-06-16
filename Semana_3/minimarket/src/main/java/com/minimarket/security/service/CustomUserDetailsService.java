package com.minimarket.security.service;

import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscamos el usuario en la base de datos por su username
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));

        // 2. Mapeamos los roles asegurando que lleven el prefijo ROLE_ de forma correcta para evitar el 403
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getRoles().stream()
                        .map(rol -> {
                            String nombreRol = rol.getNombre().toUpperCase();
                            // Si el rol ya empieza con ROLE_, lo dejamos; si no, se lo anteponemos
                            if (!nombreRol.startsWith("ROLE_")) {
                                nombreRol = "ROLE_" + nombreRol;
                            }
                            return new SimpleGrantedAuthority(nombreRol);
                        })
                        .collect(Collectors.toList())
        );
    }
}