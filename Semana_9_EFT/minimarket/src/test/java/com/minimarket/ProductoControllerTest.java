package com.minimarket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.ProductoController;
import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Producto productoEjemplo;

    @BeforeEach
    public void setUp() {
        productoEjemplo = new Producto();
        productoEjemplo.setId(1L);
        productoEjemplo.setNombre("Bebida Cola 3L");
        productoEjemplo.setPrecio(2500.0);
        productoEjemplo.setStock(50);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void testListarProductos() throws Exception {
        List<Producto> lista = Arrays.asList(productoEjemplo);
        Mockito.when(productoService.findAll()).thenReturn(lista);

        mockMvc.perform(get("/api/productos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productoList").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void testObtenerPorId() throws Exception {
        Mockito.when(productoService.findById(1L)).thenReturn(productoEjemplo);

        mockMvc.perform(get("/api/productos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Bebida Cola 3L"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void testCrear() throws Exception {
        Mockito.when(productoService.save(any(Producto.class))).thenReturn(productoEjemplo);

        mockMvc.perform(post("/api/productos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoEjemplo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void testActualizar() throws Exception {
        Mockito.when(productoService.findById(1L)).thenReturn(productoEjemplo);
        Mockito.when(productoService.save(any(Producto.class))).thenReturn(productoEjemplo);

        mockMvc.perform(put("/api/productos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void testEliminar() throws Exception {
        Mockito.when(productoService.findById(1L)).thenReturn(productoEjemplo);
        Mockito.doNothing().when(productoService).deleteById(1L);

        mockMvc.perform(delete("/api/productos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}