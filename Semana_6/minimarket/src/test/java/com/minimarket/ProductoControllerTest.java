package com.minimarket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.ProductoController;
import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testListarProductos() throws Exception {
        Producto prod = new Producto();
        prod.setId(1L);
        prod.setNombre("Pan");

        when(productoService.findAll()).thenReturn(Arrays.asList(prod));

        mockMvc.perform(get("/api/productos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Pan"));
    }

    @Test
    @WithMockUser
    void testObtenerProductoPorId_Exitoso() throws Exception {
        Producto prod = new Producto();
        prod.setId(1L);
        prod.setNombre("Pan");
        prod.setPrecio(1000.0);
        prod.setStock(10);

        // Forzamos el emparejamiento para cualquier llamada de ID en la petición HTTP
        when(productoService.findById(anyLong())).thenReturn(prod);

        mockMvc.perform(get("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pan"));
    }

    @Test
    @WithMockUser
    void testObtenerProductoPorId_NoEncontrado() throws Exception {
        when(productoService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/productos/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGuardarProducto() throws Exception {
        Producto prod = new Producto();
        prod.setNombre("Cereal");
        prod.setStock(5);

        when(productoService.save(any(Producto.class))).thenReturn(prod);

        mockMvc.perform(post("/api/productos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prod)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Cereal"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testActualizarProducto_Exitoso() throws Exception {
        Producto prod = new Producto();
        prod.setId(1L);
        prod.setNombre("Pan Actualizado");
        prod.setStock(20);
        prod.setPrecio(1000.0);

        // CORREGIDO: Ambos métodos del servicio simulado deben retornar el objeto para pasar las precondiciones
        when(productoService.findById(anyLong())).thenReturn(prod);
        when(productoService.save(any(Producto.class))).thenReturn(prod);

        mockMvc.perform(put("/api/productos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prod)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pan Actualizado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testActualizarProducto_NoEncontrado() throws Exception {
        Producto nuevo = new Producto();
        nuevo.setNombre("Atún Nuevo");

        when(productoService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/api/productos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEliminarProducto_Exitoso() throws Exception {
        Producto prod = new Producto();
        prod.setId(1L);
        prod.setNombre("Pan");

        // CORREGIDO: Simula la existencia previa requerida por tu flujo condicional de borrado de controladores
        when(productoService.findById(anyLong())).thenReturn(prod);
        doNothing().when(productoService).deleteById(anyLong());

        mockMvc.perform(delete("/api/productos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEliminarProducto_NoEncontrado() throws Exception {
        when(productoService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(delete("/api/productos/1")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}