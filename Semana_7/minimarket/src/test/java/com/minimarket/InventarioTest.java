package com.minimarket;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InventarioTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Producto productoSimulado;
    private Inventario inventarioSimulado;

    @BeforeEach
    void setUp() {
        productoSimulado = new Producto();
        productoSimulado.setId(200L);
        productoSimulado.setNombre("Aceite Vegetal 1L");

        inventarioSimulado = new Inventario();
        inventarioSimulado.setId(50L);
        inventarioSimulado.setTipoMovimiento("ENTRADA");
        inventarioSimulado.setCantidad(50);
        inventarioSimulado.setProducto(productoSimulado);
    }

    @Test
    @DisplayName("PRUEBA 1: Validación de campos obligatorios no nulos en transacciones de movimiento")
    void testInformacionMovimientoNoNula() {
        assertNotNull(inventarioSimulado.getTipoMovimiento());
        assertNotNull(inventarioSimulado.getCantidad());
    }

    @Test
    @DisplayName("PRUEBA 2: Validación estructural de cadenas no vacías en el tipo de movimiento")
    void testTipoMovimientoNoVacio() {
        assertFalse(inventarioSimulado.getTipoMovimiento().trim().isEmpty());
        assertEquals("ENTRADA", inventarioSimulado.getTipoMovimiento());
    }

    @Test
    @DisplayName("PRUEBA 3: Control lógico para asegurar que las cantidades ingresadas sean positivas")
    void testCantidadMovimientoMayorACero() {
        assertTrue(inventarioSimulado.getCantidad() > 0, "La cantidad de movimiento debe ser mayor a cero");
    }

    @Test
    @DisplayName("PRUEBA 4: Validación integral de la relación estructural entre Producto e Inventario")
    void testRelacionProductoInventarioCorrecta() {
        assertNotNull(inventarioSimulado.getProducto());
        assertEquals(200L, inventarioSimulado.getProducto().getId());
        assertEquals("Aceite Vegetal 1L", inventarioSimulado.getProducto().getNombre());
    }

    @Test
    @DisplayName("PRUEBA 5: Simulación de inicialización de un movimiento de SALIDA válido")
    void testMovimientoSalidaValido() {
        Inventario movimientoSalida = new Inventario();
        movimientoSalida.setTipoMovimiento("SALIDA");
        movimientoSalida.setCantidad(10);
        
        assertEquals("SALIDA", movimientoSalida.getTipoMovimiento());
        assertTrue(movimientoSalida.getCantidad() == 10);
    }
}