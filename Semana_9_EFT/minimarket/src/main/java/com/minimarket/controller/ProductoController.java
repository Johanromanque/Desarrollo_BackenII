package com.minimarket.controller;

import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
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
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Controlador para la gestion de productos con soporte hipermedia HATEOAS")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Retorna una coleccion de productos con sus respectivos enlaces navegables")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> listarTodos() {
        List<Producto> productos = productoService.findAll();

        List<EntityModel<Producto>> productosConEnlaces = productos.stream()
                .map(producto -> EntityModel.of(producto,
                        linkTo(methodOn(ProductoController.class).obtenerPorId(producto.getId())).withSelfRel(),
                        linkTo(methodOn(ProductoController.class).listarTodos()).withRel("productos")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Producto>> recursoGeneral = CollectionModel.of(productosConEnlaces);
        recursoGeneral.add(linkTo(methodOn(ProductoController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(recursoGeneral);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo producto", description = "Registra un producto en el sistema y retorna el recurso con sus enlaces HATEOAS")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    public ResponseEntity<EntityModel<Producto>> crear(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.save(producto);
        
        EntityModel<Producto> recurso = EntityModel.of(nuevoProducto,
                linkTo(methodOn(ProductoController.class).obtenerPorId(nuevoProducto.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarTodos()).withRel("productos"));

        return ResponseEntity.status(HttpStatus.CREATED).body(recurso);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Busca un producto especifico y proporciona enlaces para su actualizacion o navegacion")
    @ApiResponse(responseCode = "200", description = "Producto encontrado")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<EntityModel<Producto>> obtenerPorId(@PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Producto> recurso = EntityModel.of(producto,
                linkTo(methodOn(ProductoController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarTodos()).withRel("productos"),
                linkTo(methodOn(ProductoController.class).actualizar(id, producto)).withRel("actualizar"),
                linkTo(methodOn(ProductoController.class).eliminar(id)).withRel("eliminar"));

        return ResponseEntity.ok(recurso);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto", description = "Modifica los datos de un producto existente y retorna el recurso actualizado con soporte hipermedia")
    @ApiResponse(responseCode = "200", description = "Producto actualizado de forma exitosa")
    @ApiResponse(responseCode = "404", description = "No se encontro el producto a actualizar")
    public ResponseEntity<EntityModel<Producto>> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        Producto existente = productoService.findById(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        
        producto.setId(id);
        Producto productoActualizado = productoService.save(producto);

        EntityModel<Producto> recurso = EntityModel.of(productoActualizado,
                linkTo(methodOn(ProductoController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarTodos()).withRel("productos"));

        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Remueve permanentemente un producto del catalogo por su identificador")
    @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Producto existente = productoService.findById(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}