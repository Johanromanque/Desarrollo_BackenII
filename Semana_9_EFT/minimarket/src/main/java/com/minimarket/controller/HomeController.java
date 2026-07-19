package com.minimarket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Home", description = "Punto de entrada principal de la API")
public class HomeController {

    @GetMapping("/public/home")
    @Operation(
        summary = "Obtener punto de entrada de la API", 
        description = "Provee un mensaje de bienvenida de Minimarket Plus y los enlaces dinámicos principales para navegación."
    )
    public ResponseEntity<Map<String, Object>> inicio() {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Bienvenido a la API REST de Minimarket Plus");
        respuesta.put("version", "1.0.0");
        respuesta.put("estado", "Servicio Operativo");
        
        // Estructura de enlaces HAL-JSON / HATEOAS para guiar al desarrollador
        Map<String, String> enlaces = new HashMap<>();
        enlaces.put("documentacion_ui", "http://localhost:8081/swagger-ui/index.html");
        enlaces.put("api_docs_json", "http://localhost:8081/v3/api-docs");
        enlaces.put("productos", "http://localhost:8081/api/productos");
        enlaces.put("carrito", "http://localhost:8081/api/carrito");
        enlaces.put("inventario", "http://localhost:8081/api/inventario");
        enlaces.put("usuarios", "http://localhost:8081/api/usuarios");
        
        respuesta.put("_links", enlaces);
        
        return ResponseEntity.ok(respuesta);
    }
}