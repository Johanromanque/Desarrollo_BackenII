package com.minimarket.controller;

import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/usuarios")
@Tag(
        name = "Usuarios",
        description = "Operaciones para consultar, registrar, actualizar y eliminar usuarios"
)
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene todos los usuarios registrados y agrega enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuarios obtenidos correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Usuario>>> listarUsuarios() {

        List<EntityModel<Usuario>> usuarios = usuarioService.findAll()
                .stream()
                .map(this::crearModeloUsuario)
                .toList();

        CollectionModel<EntityModel<Usuario>> coleccion =
                CollectionModel.of(
                        usuarios,
                        linkTo(methodOn(UsuarioController.class)
                                .listarUsuarios())
                                .withSelfRel(),

                        linkTo(methodOn(UsuarioController.class)
                                .guardarUsuario(null))
                                .withRel("registrar-usuario")
                );

        return ResponseEntity.ok(coleccion);
    }

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Busca un usuario mediante su identificador y devuelve enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(
            @Parameter(
                    description = "Identificador del usuario",
                    example = "1"
            )
            @PathVariable Long id) {

        Optional<Usuario> usuario = usuarioService.findById(id);

        return usuario
                .map(valor -> ResponseEntity.ok(crearModeloUsuario(valor)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Registrar usuario",
            description = "Registra un nuevo usuario y devuelve el recurso creado con enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del usuario no válidos"
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> guardarUsuario(
            @RequestBody Usuario usuario) {

        Usuario usuarioGuardado = usuarioService.save(usuario);
        EntityModel<Usuario> modelo = crearModeloUsuario(usuarioGuardado);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(modelo);
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente y devuelve enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de actualización no válidos"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> actualizarUsuario(
            @Parameter(
                    description = "Identificador del usuario que se actualizará",
                    example = "1"
            )
            @PathVariable Long id,
            @RequestBody Usuario usuario) {

        Optional<Usuario> usuarioExistente = usuarioService.findById(id);

        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        usuario.setId(id);
        Usuario usuarioActualizado = usuarioService.save(usuario);

        return ResponseEntity.ok(crearModeloUsuario(usuarioActualizado));
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario según su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @Parameter(
                    description = "Identificador del usuario que se eliminará",
                    example = "1"
            )
            @PathVariable Long id) {

        Optional<Usuario> usuario = usuarioService.findById(id);

        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Convierte un usuario en una representación con enlaces HATEOAS.
     */
    private EntityModel<Usuario> crearModeloUsuario(Usuario usuario) {

        Long id = usuario.getId();

        return EntityModel.of(
                usuario,

                linkTo(methodOn(UsuarioController.class)
                        .obtenerUsuarioPorId(id))
                        .withSelfRel(),

                linkTo(methodOn(UsuarioController.class)
                        .listarUsuarios())
                        .withRel("usuarios"),

                linkTo(methodOn(UsuarioController.class)
                        .actualizarUsuario(id, null))
                        .withRel("actualizar"),

                linkTo(methodOn(UsuarioController.class)
                        .eliminarUsuario(id))
                        .withRel("eliminar")
        );
    }
}