package com.minimarket;

import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void testFindAll() {
        Producto prod = new Producto();
        prod.setId(1L);
        prod.setNombre("Arroz");

        when(productoRepository.findAll()).thenReturn(Arrays.asList(prod));

        List<Producto> result = productoService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Arroz", result.get(0).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Encontrado() {
        Producto prod = new Producto();
        prod.setId(1L);
        prod.setNombre("Leche");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(prod));

        Producto result = productoService.findById(1L);

        assertNotNull(result);
        assertEquals("Leche", result.getNombre());
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_NoEncontrado() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Producto result = productoService.findById(99L);

        assertNull(result);
    }

    @Test
    void testSave() {
        Producto prod = new Producto();
        prod.setNombre("Fideos");
        prod.setStock(50);

        when(productoRepository.save(any(Producto.class))).thenReturn(prod);

        Producto result = productoService.save(prod);

        assertNotNull(result);
        assertEquals("Fideos", result.getNombre());
        verify(productoRepository, times(1)).save(prod);
    }

    @Test
    void testSave_StockNegativo_LanzaExcepcion() {
        Producto prod = new Producto();
        prod.setNombre("Galletas");
        prod.setStock(-10);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.save(prod);
        });

        assertEquals("El stock inicial no puede ser negativo", exception.getMessage());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testDeleteById() {
        Long id = 1L;

        when(productoRepository.existsById(id)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(id);

        productoService.deleteById(id);

        verify(productoRepository, times(1)).existsById(id);
        verify(productoRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteById_NoExiste_LanzaExcepcion() {
        Long id = 99L;

        when(productoRepository.existsById(id)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.deleteById(id);
        });

        assertTrue(exception.getMessage().contains("No se puede eliminar: Producto no encontrado con ID:"));
        verify(productoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testFindByCategoriaId() {
        Producto prod = new Producto();
        prod.setId(1L);
        prod.setNombre("Bebida");

        when(productoRepository.findByCategoriaId(1L)).thenReturn(Arrays.asList(prod));

        List<Producto> result = productoService.findByCategoriaId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bebida", result.get(0).getNombre());
        verify(productoRepository, times(1)).findByCategoriaId(1L);
    }
}