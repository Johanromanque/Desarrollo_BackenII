package com.minimarket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.CategoriaController;
import com.minimarket.entity.Categoria;
import com.minimarket.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testListarCategorias() throws Exception {
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("Limpieza");

        when(categoriaService.findAll()).thenReturn(Arrays.asList(cat));

        mockMvc.perform(get("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Limpieza"));
    }

    @Test
    @WithMockUser
    void testObtenerCategoriaPorId_Exitoso() throws Exception {
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("Limpieza");

        when(categoriaService.findById(1L)).thenReturn(cat);

        mockMvc.perform(get("/api/categorias/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Limpieza"));
    }

    @Test
    @WithMockUser
    void testObtenerCategoriaPorId_NoEncontrado() throws Exception {
        when(categoriaService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/categorias/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGuardarCategoria() throws Exception {
        Categoria cat = new Categoria();
        cat.setNombre("Congelados");

        when(categoriaService.save(any(Categoria.class))).thenReturn(cat);

        mockMvc.perform(post("/api/categorias")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cat)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Congelados"));
    }

    @Test
    @WithMockUser
    void testActualizarCategoria_Exitosa() throws Exception {
        Categoria existente = new Categoria();
        existente.setId(1L);
        existente.setNombre("Antiguo");

        Categoria nueva = new Categoria();
        nueva.setNombre("Nuevo Nombre");

        when(categoriaService.findById(1L)).thenReturn(existente);
        when(categoriaService.save(any(Categoria.class))).thenReturn(nueva);

        mockMvc.perform(put("/api/categorias/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testActualizarCategoria_NoEncontrada() throws Exception {
        Categoria nueva = new Categoria();
        nueva.setNombre("Nuevo Nombre");

        when(categoriaService.findById(1L)).thenReturn(null);

        mockMvc.perform(put("/api/categorias/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testEliminarCategoria_Exitosa() throws Exception {
        Categoria cat = new Categoria();
        cat.setId(1L);

        when(categoriaService.findById(1L)).thenReturn(cat);

        mockMvc.perform(delete("/api/categorias/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testEliminarCategoria_NoEncontrada() throws Exception {
        when(categoriaService.findById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/categorias/1")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}