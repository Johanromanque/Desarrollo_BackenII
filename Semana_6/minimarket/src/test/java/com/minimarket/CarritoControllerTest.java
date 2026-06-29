package com.minimarket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.CarritoController;
import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.service.CarritoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarritoService carritoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Carrito crearCarrito() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente1");
        usuario.setPassword("1234");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Pan");
        producto.setPrecio(1200.0);
        producto.setStock(20);

        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(2);
        return carrito;
    }

    @Test
    @WithMockUser
    void testListarCarrito() throws Exception {
        when(carritoService.findAll()).thenReturn(List.of(crearCarrito()));

        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidad").value(2))
                .andExpect(jsonPath("$[0].producto.nombre").value("Pan"))
                .andExpect(jsonPath("$[0].usuario.username").value("cliente1"));
    }

    @Test
    @WithMockUser
    void testObtenerCarritoPorId_Exitoso() throws Exception {
        when(carritoService.findById(1L)).thenReturn(crearCarrito());

        mockMvc.perform(get("/api/carrito/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cantidad").value(2));
    }

    @Test
    @WithMockUser
    void testObtenerCarritoPorId_NoEncontrado() throws Exception {
        when(carritoService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/carrito/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testAgregarProductoAlCarrito() throws Exception {
        Carrito carrito = crearCarrito();
        when(carritoService.save(any(Carrito.class))).thenReturn(carrito);

        mockMvc.perform(post("/api/carrito")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carrito)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.producto.nombre").value("Pan"))
                .andExpect(jsonPath("$.cantidad").value(2));
    }

    @Test
    @WithMockUser
    void testActualizarCarrito_Exitoso() throws Exception {
        Carrito existente = crearCarrito();
        Carrito actualizado = crearCarrito();
        actualizado.setCantidad(5);

        when(carritoService.findById(1L)).thenReturn(existente);
        when(carritoService.save(any(Carrito.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/carrito/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(5));
    }

    @Test
    @WithMockUser
    void testActualizarCarrito_NoEncontrado() throws Exception {
        Carrito carrito = crearCarrito();
        when(carritoService.findById(99L)).thenReturn(null);

        mockMvc.perform(put("/api/carrito/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carrito)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testEliminarProductoDelCarrito_Exitoso() throws Exception {
        when(carritoService.findById(1L)).thenReturn(crearCarrito());

        mockMvc.perform(delete("/api/carrito/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(carritoService).deleteById(1L);
    }

    @Test
    @WithMockUser
    void testEliminarProductoDelCarrito_NoEncontrado() throws Exception {
        when(carritoService.findById(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/carrito/99").with(csrf()))
                .andExpect(status().isNotFound());

        verify(carritoService, never()).deleteById(99L);
    }
}
