package com.minimarket;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.service.impl.CarritoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarritoTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private Usuario usuarioSimulado;
    private Producto productoSimulado;
    private Carrito carritoSimulado;

    @BeforeEach
    void setUp() {
        usuarioSimulado = new Usuario();
        usuarioSimulado.setId(1L);
        usuarioSimulado.setUsername("cristian.olivares");

        productoSimulado = new Producto();
        productoSimulado.setId(100L);
        productoSimulado.setNombre("Arroz Grado 1");
        productoSimulado.setStock(5); // Inventario controlado para las pruebas

        carritoSimulado = new Carrito();
        carritoSimulado.setId(10L);
        carritoSimulado.setUsuario(usuarioSimulado);
    }

    @Test
    @DisplayName("PRUEBA 1: Adición exitosa de producto cuando el stock en bodega es suficiente")
    void testAgregarProductoConStockSuficiente() {
        int cantidadSolicitada = 3;
        when(carritoRepository.findById(10L)).thenReturn(Optional.of(carritoSimulado));
        
        assertDoesNotThrow(() -> {
            carritoService.agregarProducto(10L, productoSimulado, cantidadSolicitada);
        });
        
        verify(carritoRepository, times(1)).save(carritoSimulado);
    }

    @Test
    @DisplayName("PRUEBA 2: Lanzamiento de StockInsuficienteException cuando la cantidad supera las existencias")
    void testAgregarProductoExcedeStock() {
        int cantidadSolicitada = 10; // Supera las 5 unidades reales
        when(carritoRepository.findById(10L)).thenReturn(Optional.of(carritoSimulado));

        assertThrows(StockInsuficienteException.class, () -> {
            carritoService.agregarProducto(10L, productoSimulado, cantidadSolicitada);
        });

        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    @DisplayName("PRUEBA 3: Validación estricta del límite exacto de stock disponible (Borde)")
    void testAgregarProductoLimiteExactoDeStock() {
        int cantidadLimite = 5; // Igual al stock máximo
        when(carritoRepository.findById(10L)).thenReturn(Optional.of(carritoSimulado));

        assertDoesNotThrow(() -> {
            carritoService.agregarProducto(10L, productoSimulado, cantidadLimite);
        });

        verify(carritoRepository, times(1)).save(carritoSimulado);
    }

    @Test
    @DisplayName("PRUEBA 4: Validación integral de la relación Producto-Usuario vinculada al Carrito")
    void testValidarUsuarioAsociadoAlCarrito() {
        when(carritoRepository.findById(10L)).thenReturn(Optional.of(carritoSimulado));
        Carrito carritoObtenido = carritoService.findById(10L);

        assertNotNull(carritoObtenido);
        assertEquals("cristian.olivares", carritoObtenido.getUsuario().getUsername());
        assertEquals(1L, carritoObtenido.getUsuario().getId());
    }

    @Test
    @DisplayName("PRUEBA 5: Control de excepciones ante búsquedas de carritos inexistentes")
    void testBuscarCarritoNoEncontrado() {
        when(carritoRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            carritoService.agregarProducto(999L, productoSimulado, 1);
        });
    }
}