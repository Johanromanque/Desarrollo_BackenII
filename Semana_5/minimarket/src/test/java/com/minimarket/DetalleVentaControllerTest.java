package com.minimarket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.DetalleVentaController;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.service.DetalleVentaService;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DetalleVentaController.class)
class DetalleVentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DetalleVentaService detalleVentaService;

    @Autowired
    private ObjectMapper objectMapper;

    private DetalleVenta crearDetalleVenta() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente1");
        usuario.setPassword("1234");

        Venta venta = new Venta();
        venta.setId(1L);
        venta.setUsuario(usuario);
        venta.setFecha(new Date());

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Leche");
        producto.setPrecio(1000.0);
        producto.setStock(30);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(1L);
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        detalle.setPrecio(2000.0);
        return detalle;
    }

    @Test
    @WithMockUser
    void testListarDetalleVentas() throws Exception {
        when(detalleVentaService.findAll()).thenReturn(List.of(crearDetalleVenta()));

        mockMvc.perform(get("/api/detalle-ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidad").value(2))
                .andExpect(jsonPath("$[0].precio").value(2000.0))
                .andExpect(jsonPath("$[0].producto.nombre").value("Leche"));
    }

    @Test
    @WithMockUser
    void testObtenerDetalleVentaPorId_Exitoso() throws Exception {
        when(detalleVentaService.findById(1L)).thenReturn(crearDetalleVenta());

        mockMvc.perform(get("/api/detalle-ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cantidad").value(2));
    }

    @Test
    @WithMockUser
    void testObtenerDetalleVentaPorId_NoEncontrado() throws Exception {
        when(detalleVentaService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/detalle-ventas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGuardarDetalleVenta() throws Exception {
        DetalleVenta detalle = crearDetalleVenta();
        when(detalleVentaService.save(any(DetalleVenta.class))).thenReturn(detalle);

        mockMvc.perform(post("/api/detalle-ventas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detalle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.producto.nombre").value("Leche"))
                .andExpect(jsonPath("$.cantidad").value(2));
    }

    @Test
    @WithMockUser
    void testActualizarDetalleVenta_Exitoso() throws Exception {
        DetalleVenta existente = crearDetalleVenta();
        DetalleVenta actualizado = crearDetalleVenta();
        actualizado.setCantidad(4);
        actualizado.setPrecio(4000.0);

        when(detalleVentaService.findById(1L)).thenReturn(existente);
        when(detalleVentaService.save(any(DetalleVenta.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/detalle-ventas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(4))
                .andExpect(jsonPath("$.precio").value(4000.0));
    }

    @Test
    @WithMockUser
    void testActualizarDetalleVenta_NoEncontrado() throws Exception {
        DetalleVenta detalle = crearDetalleVenta();
        when(detalleVentaService.findById(99L)).thenReturn(null);

        mockMvc.perform(put("/api/detalle-ventas/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detalle)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testEliminarDetalleVenta_Exitoso() throws Exception {
        when(detalleVentaService.findById(1L)).thenReturn(crearDetalleVenta());

        mockMvc.perform(delete("/api/detalle-ventas/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(detalleVentaService).deleteById(1L);
    }

    @Test
    @WithMockUser
    void testEliminarDetalleVenta_NoEncontrado() throws Exception {
        when(detalleVentaService.findById(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/detalle-ventas/99").with(csrf()))
                .andExpect(status().isNotFound());

        verify(detalleVentaService, never()).deleteById(99L);
    }
}
