package com.minimarket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.UsuarioController;
import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testListarUsuarios() throws Exception {
        Usuario user = new Usuario();
        user.setId(1L);
        user.setUsername("juan");

        when(usuarioService.findAll()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("juan"));
    }

    @Test
    @WithMockUser
    void testObtenerUsuarioPorId_Exitoso() throws Exception {
        Usuario user = new Usuario();
        user.setId(1L);
        user.setUsername("pedro");

        when(usuarioService.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("pedro"));
    }

    @Test
    @WithMockUser
    void testObtenerUsuarioPorId_NoEncontrado() throws Exception {
        when(usuarioService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGuardarUsuario() throws Exception {
        Usuario user = new Usuario();
        user.setUsername("marta");

        when(usuarioService.save(any(Usuario.class))).thenReturn(user);

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("marta"));
    }

    @Test
    @WithMockUser
    void testActualizarUsuario_Exitoso() throws Exception {
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setUsername("antiguo");

        Usuario nuevo = new Usuario();
        nuevo.setUsername("nuevo_username");

        when(usuarioService.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioService.save(any(Usuario.class))).thenReturn(nuevo);

        mockMvc.perform(put("/api/usuarios/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testActualizarUsuario_NoEncontrado() throws Exception {
        Usuario nuevo = new Usuario();
        nuevo.setUsername("nuevo_username");

        when(usuarioService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/usuarios/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testEliminarUsuario_Exitoso() throws Exception {
        Usuario user = new Usuario();
        user.setId(1L);

        when(usuarioService.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/usuarios/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testEliminarUsuario_NoEncontrado() throws Exception {
        when(usuarioService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/usuarios/1")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}