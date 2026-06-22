package com.minimarket;

import com.minimarket.entity.Producto;
import com.minimarket.entity.Categoria;
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
        prod.setPrecio(1200.0);
        prod.setStock(50);

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
        prod.setNombre("Aceite");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(prod));

        Producto result = productoService.findById(1L);

        assertNotNull(result);
        assertEquals("Aceite", result.getNombre());
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

        when(productoRepository.save(any(Producto.class))).thenReturn(prod);

        Producto result = productoService.save(prod);

        assertNotNull(result);
        assertEquals("Fideos", result.getNombre());
    }

    @Test
    void testDeleteById() {
        doNothing().when(productoRepository).deleteById(1L);

        assertDoesNotThrow(() -> productoService.deleteById(1L));

        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByCategoriaId() {
        Producto prod = new Producto();
        prod.setNombre("Bebida");

        when(productoRepository.findByCategoriaId(1L)).thenReturn(Arrays.asList(prod));

        List<Producto> result = productoService.findByCategoriaId(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Bebida", result.get(0).getNombre());
    }
}