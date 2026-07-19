package com.minimarket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.InventarioController;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.service.InventarioService;
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

@WebMvcTest(InventarioController.class)
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventarioService inventarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Inventario crearInventario() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(1500.0);
        producto.setStock(50);

        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(10);
        inventario.setTipoMovimiento("ENTRADA");
        inventario.setFechaMovimiento(new Date());

        return inventario;
    }

    @Test
    @WithMockUser
    void testListarMovimientosDeInventario() throws Exception {
        when(inventarioService.findAll())
                .thenReturn(List.of(crearInventario()));

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$._embedded.inventarioList[0].tipoMovimiento"
                ).value("ENTRADA"))
                .andExpect(jsonPath(
                        "$._embedded.inventarioList[0].cantidad"
                ).value(10))
                .andExpect(jsonPath(
                        "$._embedded.inventarioList[0].producto.nombre"
                ).value("Arroz"))
                .andExpect(jsonPath(
                        "$._embedded.inventarioList[0]._links.self.href"
                ).exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @WithMockUser
    void testObtenerMovimientoPorId_Exitoso() throws Exception {
        when(inventarioService.findById(1L))
                .thenReturn(crearInventario());

        mockMvc.perform(get("/api/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipoMovimiento").value("ENTRADA"))
                .andExpect(jsonPath("$.cantidad").value(10))
                .andExpect(jsonPath("$.producto.nombre").value("Arroz"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath(
                        "$._links.movimientos-inventario.href"
                ).exists())
                .andExpect(jsonPath("$._links.actualizar.href").exists())
                .andExpect(jsonPath("$._links.eliminar.href").exists());
    }

    @Test
    @WithMockUser
    void testObtenerMovimientoPorId_NoEncontrado() throws Exception {
        when(inventarioService.findById(99L))
                .thenReturn(null);

        mockMvc.perform(get("/api/inventario/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testRegistrarMovimiento() throws Exception {
        Inventario inventario = crearInventario();

        when(inventarioService.save(any(Inventario.class)))
                .thenReturn(inventario);

        mockMvc.perform(post("/api/inventario")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cantidad").value(10))
                .andExpect(jsonPath("$.tipoMovimiento").value("ENTRADA"))
                .andExpect(jsonPath("$.producto.nombre").value("Arroz"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath(
                        "$._links.movimientos-inventario.href"
                ).exists());
    }

    @Test
    @WithMockUser
    void testActualizarMovimiento_Exitoso() throws Exception {
        Inventario existente = crearInventario();
        Inventario actualizado = crearInventario();

        actualizado.setTipoMovimiento("SALIDA");
        actualizado.setCantidad(3);

        when(inventarioService.findById(1L))
                .thenReturn(existente);

        when(inventarioService.save(any(Inventario.class)))
                .thenReturn(actualizado);

        mockMvc.perform(put("/api/inventario/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoMovimiento").value("SALIDA"))
                .andExpect(jsonPath("$.cantidad").value(3))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath(
                        "$._links.movimientos-inventario.href"
                ).exists());
    }

    @Test
    @WithMockUser
    void testActualizarMovimiento_NoEncontrado() throws Exception {
        Inventario inventario = crearInventario();

        when(inventarioService.findById(99L))
                .thenReturn(null);

        mockMvc.perform(put("/api/inventario/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testEliminarMovimiento_Exitoso() throws Exception {
        when(inventarioService.findById(1L))
                .thenReturn(crearInventario());

        mockMvc.perform(delete("/api/inventario/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(inventarioService).deleteById(1L);
    }

    @Test
    @WithMockUser
    void testEliminarMovimiento_NoEncontrado() throws Exception {
        when(inventarioService.findById(99L))
                .thenReturn(null);

        mockMvc.perform(delete("/api/inventario/99")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(inventarioService, never())
                .deleteById(99L);
    }
}