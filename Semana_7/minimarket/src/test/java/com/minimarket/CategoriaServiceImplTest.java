package com.minimarket;

import com.minimarket.entity.Categoria;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.service.impl.CategoriaServiceImpl;
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
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Test
    void testFindAll() {
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("Bebidas");

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(cat));

        List<Categoria> result = categoriaService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bebidas", result.get(0).getNombre());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Encontrado() {
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("Lácteos");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));

        Categoria result = categoriaService.findById(1L);

        assertNotNull(result);
        assertEquals("Lácteos", result.getNombre());
    }

    @Test
    void testFindById_NoEncontrado() {
        when(categoriaRepository.findById(2L)).thenReturn(Optional.empty());

        Categoria result = categoriaService.findById(2L);

        assertNull(result);
    }

    @Test
    void testSave() {
        Categoria cat = new Categoria();
        cat.setNombre("Abarrotes");

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(cat);

        Categoria result = categoriaService.save(cat);

        assertNotNull(result);
        assertEquals("Abarrotes", result.getNombre());
    }

    @Test
    void testDeleteById() {
        doNothing().when(categoriaRepository).deleteById(1L);

        assertDoesNotThrow(() -> categoriaService.deleteById(1L));

        verify(categoriaRepository, times(1)).deleteById(1L);
    }
}