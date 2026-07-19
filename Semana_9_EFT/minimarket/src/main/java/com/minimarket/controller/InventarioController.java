package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/inventario")
@Tag(
        name = "Inventario",
        description = "Operaciones para registrar, consultar, actualizar y eliminar movimientos de inventario"
)
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @Operation(
            summary = "Listar movimientos de inventario",
            description = "Obtiene todos los movimientos de inventario registrados y agrega enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimientos de inventario obtenidos correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Inventario>>> listarMovimientosDeInventario() {

        List<EntityModel<Inventario>> movimientos = inventarioService.findAll()
                .stream()
                .map(this::crearModeloInventario)
                .toList();

        CollectionModel<EntityModel<Inventario>> coleccion =
                CollectionModel.of(
                        movimientos,
                        linkTo(methodOn(InventarioController.class)
                                .listarMovimientosDeInventario())
                                .withSelfRel(),

                        linkTo(methodOn(InventarioController.class)
                                .registrarMovimiento(null))
                                .withRel("registrar-movimiento")
                );

        return ResponseEntity.ok(coleccion);
    }

    @Operation(
            summary = "Obtener movimiento por ID",
            description = "Busca un movimiento de inventario por su identificador y agrega enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimiento encontrado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimiento de inventario no encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> obtenerMovimientoPorId(
            @Parameter(
                    description = "Identificador del movimiento de inventario",
                    example = "1"
            )
            @PathVariable Long id) {

        Inventario inventario = inventarioService.findById(id);

        if (inventario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(crearModeloInventario(inventario));
    }

    @Operation(
            summary = "Registrar movimiento de inventario",
            description = "Registra un nuevo movimiento de inventario y retorna el recurso creado con enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Movimiento registrado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del movimiento no válidos"
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<Inventario>> registrarMovimiento(
            @RequestBody Inventario inventario) {

        Inventario inventarioGuardado = inventarioService.save(inventario);
        EntityModel<Inventario> modelo = crearModeloInventario(inventarioGuardado);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(modelo);
    }

    @Operation(
            summary = "Actualizar movimiento de inventario",
            description = "Actualiza completamente un movimiento existente y devuelve el recurso con enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimiento actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimiento de inventario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de actualización no válidos"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> actualizarMovimiento(
            @Parameter(
                    description = "Identificador del movimiento que se actualizará",
                    example = "1"
            )
            @PathVariable Long id,
            @RequestBody Inventario inventario) {

        Inventario existente = inventarioService.findById(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        inventario.setId(id);
        Inventario inventarioActualizado = inventarioService.save(inventario);

        return ResponseEntity.ok(crearModeloInventario(inventarioActualizado));
    }

    @Operation(
            summary = "Eliminar movimiento de inventario",
            description = "Elimina un movimiento de inventario según su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Movimiento eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimiento de inventario no encontrado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(
            @Parameter(
                    description = "Identificador del movimiento que se eliminará",
                    example = "1"
            )
            @PathVariable Long id) {

        Inventario inventario = inventarioService.findById(id);

        if (inventario == null) {
            return ResponseEntity.notFound().build();
        }

        inventarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Convierte un movimiento de inventario en una representación HATEOAS.
     */
    private EntityModel<Inventario> crearModeloInventario(Inventario inventario) {

        Long id = inventario.getId();

        return EntityModel.of(
                inventario,

                linkTo(methodOn(InventarioController.class)
                        .obtenerMovimientoPorId(id))
                        .withSelfRel(),

                linkTo(methodOn(InventarioController.class)
                        .listarMovimientosDeInventario())
                        .withRel("movimientos-inventario"),

                linkTo(methodOn(InventarioController.class)
                        .actualizarMovimiento(id, null))
                        .withRel("actualizar"),

                linkTo(methodOn(InventarioController.class)
                        .eliminarMovimiento(id))
                        .withRel("eliminar")
        );
    }
}