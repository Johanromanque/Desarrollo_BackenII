package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Controlador para la gestion del carrito de compras con soporte hipermedia HATEOAS")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los elementos del carrito", description = "Retorna una coleccion con todos los registros del carrito de compras enriquecidos con enlaces relacionales")
    @ApiResponse(responseCode = "200", description = "Listado del carrito obtenido de forma exitosa")
    public ResponseEntity<CollectionModel<EntityModel<Carrito>>> listarCarrito() {
        List<Carrito> listaCarrito = carritoService.findAll();

        List<EntityModel<Carrito>> carritoConEnlaces = listaCarrito.stream()
                .map(carrito -> EntityModel.of(carrito,
                        linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(carrito.getId())).withSelfRel(),
                        linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("carritos")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Carrito>> recursoGeneral = CollectionModel.of(carritoConEnlaces);
        recursoGeneral.add(linkTo(methodOn(CarritoController.class).listarCarrito()).withSelfRel());

        return ResponseEntity.ok(recursoGeneral);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un elemento del carrito por ID", description = "Busca un registro especifico en el carrito de compras y proporciona enlaces dinamicos para interactuar con el")
    @ApiResponse(responseCode = "200", description = "Elemento del carrito encontrado correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontro el elemento solicitado")
    public ResponseEntity<EntityModel<Carrito>> obtenerCarritoPorId(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Carrito> recurso = EntityModel.of(carrito,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(id)).withSelfRel(),
                linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("carritos"),
                linkTo(methodOn(CarritoController.class).actualizarCarrito(id, carrito)).withRel("actualizar"),
                linkTo(methodOn(CarritoController.class).eliminarProductoDelCarrito(id)).withRel("eliminar"));

        return ResponseEntity.ok(recurso);
    }

    @PostMapping
    @Operation(summary = "Agregar un elemento al carrito", description = "Registra una nueva adicion al carrito y devuelve la informacion con su estructura hipermedia")
    @ApiResponse(responseCode = "201", description = "Elemento agregado al carrito con exito")
    public ResponseEntity<EntityModel<Carrito>> agregarProductoAlCarrito(@RequestBody Carrito carrito) {
        Carrito nuevoCarrito = carritoService.save(carrito);

        EntityModel<Carrito> recurso = EntityModel.of(nuevoCarrito,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(nuevoCarrito.getId())).withSelfRel(),
                linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("carritos"));

        return ResponseEntity.status(HttpStatus.CREATED).body(recurso);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un elemento del carrito", description = "Modifica los datos de un registro del carrito por su ID y entrega el recurso actualizado")
    @ApiResponse(responseCode = "200", description = "Carrito modificado satisfactoriamente")
    @ApiResponse(responseCode = "404", description = "No se localizo el registro de carrito especificado")
    public ResponseEntity<EntityModel<Carrito>> actualizarCarrito(@PathVariable Long id, @RequestBody Carrito carrito) {
        Carrito existente = carritoService.findById(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        carrito.setId(id);
        Carrito carritoActualizado = carritoService.save(carrito);

        EntityModel<Carrito> recurso = EntityModel.of(carritoActualizado,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(id)).withSelfRel(),
                linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("carritos"));

        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un elemento del carrito", description = "Remueve permanentemente un producto o registro del carrito mediante su identificador")
    @ApiResponse(responseCode = "204", description = "Elemento removido del carrito correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontro el elemento a eliminar")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }

        carritoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}