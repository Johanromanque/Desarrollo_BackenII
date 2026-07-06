package com.minimarket;

import com.minimarket.entity.Venta;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.VentaServiceImpl;
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
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    @Test
    void testFindAll() {
        Venta venta = new Venta();
        when(ventaRepository.findAll()).thenReturn(Arrays.asList(venta));

        List<Venta> result = ventaService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Encontrado() {
        Venta venta = new Venta();
        venta.setId(1L);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        Venta result = ventaService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_NoEncontrado() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        Venta result = ventaService.findById(99L);

        assertNull(result);
    }

    @Test
    void testSave() {
        Venta venta = new Venta();
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        Venta result = ventaService.save(venta);

        assertNotNull(result);
    }

    @Test
    void testFindByUsuarioId() {
        Venta venta = new Venta();
        when(ventaRepository.findByUsuarioId(1L)).thenReturn(Arrays.asList(venta));

        List<Venta> result = ventaService.findByUsuarioId(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}