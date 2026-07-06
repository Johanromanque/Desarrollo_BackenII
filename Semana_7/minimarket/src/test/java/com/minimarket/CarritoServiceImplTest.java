package com.minimarket;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.service.impl.CarritoServiceImpl;
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
class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    @Test
    void testFindAll() {
        Carrito cart = new Carrito();
        when(carritoRepository.findAll()).thenReturn(Arrays.asList(cart));

        List<Carrito> result = carritoService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carritoRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Carrito cart = new Carrito();
        cart.setId(1L);
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(cart));

        Carrito result = carritoService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testSave() {
        Carrito cart = new Carrito();
        when(carritoRepository.save(any(Carrito.class))).thenReturn(cart);

        Carrito result = carritoService.save(cart);

        assertNotNull(result);
    }

    @Test
    void testDeleteById() {
        doNothing().when(carritoRepository).deleteById(1L);
        assertDoesNotThrow(() -> carritoService.deleteById(1L));
    }

    @Test
    void testFindByUsuarioId() {
        Carrito cart = new Carrito();
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(Arrays.asList(cart));

        List<Carrito> result = carritoService.findByUsuarioId(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testAgregarProducto_Exitoso() {
        Carrito cart = new Carrito();
        cart.setId(1L);
        
        Producto prod = new Producto();
        prod.setNombre("Arroz");
        prod.setStock(10);

        when(carritoRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(cart);

        assertDoesNotThrow(() -> carritoService.agregarProducto(1L, prod, 5));
        verify(carritoRepository, times(1)).save(cart);
    }

    @Test
    void testAgregarProducto_StockInsuficiente() {
        Carrito cart = new Carrito();
        cart.setId(1L);
        
        Producto prod = new Producto();
        prod.setNombre("Arroz");
        prod.setStock(3); // Menor a la cantidad solicitada

        when(carritoRepository.findById(1L)).thenReturn(Optional.of(cart));

        assertThrows(StockInsuficienteException.class, () -> 
            carritoService.agregarProducto(1L, prod, 5)
        );
        verify(carritoRepository, never()).save(any(Carrito.class));
    }
}