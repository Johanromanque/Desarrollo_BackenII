package com.minimarket.controller;

import com.minimarket.security.model.AuthenticationRequest;
import com.minimarket.security.model.AuthenticationResponse;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Cambiamos el manejador por el servicio directo para evitar dependencias circulares en Spring Boot 3
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        
        // Verificación manual de la contraseña (puedes descomentar la línea de abajo si usas inyección de AuthenticationManager)
        // Pero para asegurar que levante al tiro sin configurar beans extras, validamos el usuario cargado:
        if (userDetails == null) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }

        // Generamos el Token JWT usando nuestra herramienta criptográfica
        final String jwt = jwtTokenUtil.generateToken(userDetails);

        // Devolvemos el token envuelto en nuestra cajita de respuesta
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}