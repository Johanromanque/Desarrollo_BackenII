package com.minimarket;

import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void testFindAll() {
        Usuario user = new Usuario();
        user.setId(1L);
        user.setUsername("cristian");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(user));

        List<Usuario> result = usuarioService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("cristian", result.get(0).getUsername());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Encontrado() {
        Usuario user = new Usuario();
        user.setId(1L);
        user.setUsername("admin");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<Usuario> result = usuarioService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getUsername());
    }

    @Test
    void testFindById_NoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> result = usuarioService.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername_Encontrado() {
        Usuario user = new Usuario();
        user.setUsername("user1");

        when(usuarioRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        Optional<Usuario> result = usuarioService.findByUsername("user1");

        assertTrue(result.isPresent());
        assertEquals("user1", result.get().getUsername());
    }

    @Test
    void testSave() {
        Usuario user = new Usuario();
        user.setUsername("nuevoUsuario");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(user);

        Usuario result = usuarioService.save(user);

        assertNotNull(result);
        assertEquals("nuevoUsuario", result.getUsername());
    }

    @Test
    void testDeleteById() {
        doNothing().when(usuarioRepository).deleteById(1L);

        assertDoesNotThrow(() -> usuarioService.deleteById(1L));

        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}