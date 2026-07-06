package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @Operation(
            summary = "Listar carritos",
            description = "Obtiene todos los registros del carrito almacenados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de carritos obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping
    public ResponseEntity<List<Carrito>> listarCarrito() {
        return ResponseEntity.ok(carritoService.findAll());
    }

    @Operation(
            summary = "Obtener carrito por ID",
            description = "Busca un registro específico del carrito según su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerCarritoPorId(
            @Parameter(description = "ID del carrito a buscar", example = "1")
            @PathVariable Long id) {

        Carrito carrito = carritoService.findById(id);

        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(carrito);
    }

    @Operation(
            summary = "Agregar producto al carrito",
            description = "Registra un producto dentro del carrito de compras."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto agregado al carrito correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    public ResponseEntity<Carrito> agregarProductoAlCarrito(@RequestBody Carrito carrito) {
        return ResponseEntity.ok(carritoService.save(carrito));
    }

    @Operation(
            summary = "Actualizar carrito",
            description = "Actualiza la información de un registro existente del carrito."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizarCarrito(
            @Parameter(description = "ID del carrito a actualizar", example = "1")
            @PathVariable Long id,
            @RequestBody Carrito carrito) {

        Carrito existente = carritoService.findById(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        carrito.setId(id);
        return ResponseEntity.ok(carritoService.save(carrito));
    }

    @Operation(
            summary = "Eliminar producto del carrito",
            description = "Elimina un registro del carrito según su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado del carrito correctamente"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(
            @Parameter(description = "ID del carrito a eliminar", example = "1")
            @PathVariable Long id) {

        Carrito carrito = carritoService.findById(id);

        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }

        carritoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}