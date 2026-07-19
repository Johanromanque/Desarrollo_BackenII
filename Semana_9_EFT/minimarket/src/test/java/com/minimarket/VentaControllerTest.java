package com.minimarket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.VentaController;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VentaController.class)
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VentaService ventaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Venta crearVenta() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente1");
        usuario.setPassword("1234");

        Venta venta = new Venta();
        venta.setId(1L);
        venta.setUsuario(usuario);
        venta.setFecha(new Date());
        venta.setDetalles(List.of());
        return venta;
    }

    @Test
    @WithMockUser
    void testListarVentas() throws Exception {
        when(ventaService.findAll()).thenReturn(List.of(crearVenta()));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].usuario.username").value("cliente1"));
    }

    @Test
    @WithMockUser
    void testObtenerVentaPorId_Exitoso() throws Exception {
        when(ventaService.findById(1L)).thenReturn(crearVenta());

        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuario.username").value("cliente1"));
    }

    @Test
    @WithMockUser
    void testObtenerVentaPorId_NoEncontrado() throws Exception {
        when(ventaService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/ventas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGuardarVenta() throws Exception {
        Venta venta = crearVenta();
        when(ventaService.save(any(Venta.class))).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuario.username").value("cliente1"));
    }
}
