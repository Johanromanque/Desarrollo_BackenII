package com.minimarket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.CarritoController;
import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
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

@WebMvcTest(CarritoController.class)
public class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarritoService carritoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Carrito carritoEjemplo;

    @BeforeEach
    public void setUp() {
        carritoEjemplo = new Carrito();
        carritoEjemplo.setId(1L);
        carritoEjemplo.setCantidad(2);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void testListarCarrito() throws Exception {
        List<Carrito> lista = Arrays.asList(carritoEjemplo);
        Mockito.when(carritoService.findAll()).thenReturn(lista);

        mockMvc.perform(get("/api/carrito")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void testObtenerCarritoPorId() throws Exception {
        Mockito.when(carritoService.findById(1L)).thenReturn(carritoEjemplo);

        mockMvc.perform(get("/api/carrito/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cantidad").value(2))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void testAgregarProductoAlCarrito() throws Exception {
        Mockito.when(carritoService.save(any(Carrito.class))).thenReturn(carritoEjemplo);

        mockMvc.perform(post("/api/carrito")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carritoEjemplo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void testActualizarCarrito() throws Exception {
        Mockito.when(carritoService.findById(1L)).thenReturn(carritoEjemplo);
        Mockito.when(carritoService.save(any(Carrito.class))).thenReturn(carritoEjemplo);

        mockMvc.perform(put("/api/carrito/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carritoEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void testEliminarProductoDelCarrito() throws Exception {
        Mockito.when(carritoService.findById(1L)).thenReturn(carritoEjemplo);
        Mockito.doNothing().when(carritoService).deleteById(1L);

        mockMvc.perform(delete("/api/carrito/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}