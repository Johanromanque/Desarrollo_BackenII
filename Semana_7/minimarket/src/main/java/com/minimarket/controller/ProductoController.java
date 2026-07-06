package com.minimarket.controller;

import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(
            summary = "Listar productos",
            description = "Obtiene la lista completa de productos registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @Operation(
            summary = "Crear producto",
            description = "Registra un nuevo producto en el minimarket."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.save(producto));
    }

    @Operation(
            summary = "Obtener producto por ID",
            description = "Busca y devuelve un producto específico según su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(
            @Parameter(description = "ID del producto a buscar", example = "1")
            @PathVariable Long id) {

        Producto producto = productoService.findById(id);

        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(producto);
    }

    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza los datos de un producto existente según su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @Parameter(description = "ID del producto a actualizar", example = "1")
            @PathVariable Long id,
            @RequestBody Producto producto) {

        Producto existente = productoService.findById(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        producto.setId(id);
        return ResponseEntity.ok(productoService.save(producto));
    }

    @Operation(
            summary = "Eliminar producto",
            description = "Elimina un producto existente según su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto a eliminar", example = "1")
            @PathVariable Long id) {

        Producto existente = productoService.findById(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}