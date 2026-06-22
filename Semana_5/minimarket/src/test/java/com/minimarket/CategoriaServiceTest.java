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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Test
    void testFindAll() {
        Categoria cat = new Categoria();
        cat.setNombre("Bebidas");
        
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(cat));

        List<Categoria> resultado = categoriaService.findAll();
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Bebidas", resultado.get(0).getNombre());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("Lácteos");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));

        Categoria resultado = categoriaService.findById(1L);

        assertNotNull(resultado);
        assertEquals("Lácteos", resultado.getNombre());
    }
}