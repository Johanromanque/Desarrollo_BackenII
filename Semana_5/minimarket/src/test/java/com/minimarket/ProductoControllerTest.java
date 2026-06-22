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
        prod.setNombre("Leche");

        when(productoService.findById(1L)).thenReturn(prod);

        mockMvc.perform(get("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Leche"));
    }

    @Test
    @WithMockUser
    void testObtenerProductoPorId_NoEncontrado() throws Exception {
        when(productoService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/productos/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGuardarProducto() throws Exception {
        Producto prod = new Producto();
        prod.setNombre("Cereal");

        when(productoService.save(any(Producto.class))).thenReturn(prod);

        mockMvc.perform(post("/api/productos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prod)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cereal"));
    }

    @Test
    @WithMockUser
    void testActualizarProducto_Exitoso() throws Exception {
        Producto existente = new Producto();
        existente.setId(1L);
        existente.setNombre("Atún Antiguo");

        Producto nuevo = new Producto();
        nuevo.setNombre("Atún Nuevo");

        when(productoService.findById(1L)).thenReturn(existente);
        when(productoService.save(any(Producto.class))).thenReturn(nuevo);

        mockMvc.perform(put("/api/productos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testActualizarProducto_NoEncontrado() throws Exception {
        Producto nuevo = new Producto();
        nuevo.setNombre("Atún Nuevo");

        when(productoService.findById(1L)).thenReturn(null);

        mockMvc.perform(put("/api/productos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testEliminarProducto_Exitoso() throws Exception {
        Producto prod = new Producto();
        prod.setId(1L);

        when(productoService.findById(1L)).thenReturn(prod);

        mockMvc.perform(delete("/api/productos/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testEliminarProducto_NoEncontrado() throws Exception {
        when(productoService.findById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/productos/1")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}