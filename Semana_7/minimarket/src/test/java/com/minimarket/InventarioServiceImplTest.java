package com.minimarket;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    @Test
    void testInformacionMovimiento_NoNuloNiVacio() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("ENTRADA");
        inventario.setCantidad(100);
        inventario.setFechaMovimiento(new Date());

        assertNotNull(inventario.getTipoMovimiento(), "El tipo de movimiento no debe ser nulo");
        assertFalse(inventario.getTipoMovimiento().trim().isEmpty(), "El tipo de movimiento no debe estar vacío");
        assertNotNull(inventario.getCantidad(), "La cantidad no debe ser nula");
        assertTrue(inventario.getCantidad() > 0, "La cantidad debe ser mayor a cero");
        assertNotNull(inventario.getFechaMovimiento(), "La fecha del movimiento no debe ser nula");
    }

    @Test
    void testRelacionProductoInventario_Correcta() {
        Producto prodMock = new Producto();
        prodMock.setId(10L);
        prodMock.setNombre("Aceite de Oliva");

        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(prodMock);

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));

        Inventario resultado = inventarioService.findById(1L);

        assertNotNull(resultado);
        assertNotNull(resultado.getProducto(), "El inventario debe tener un producto asociado");
        assertEquals(10L, resultado.getProducto().getId(), "El ID del producto asociado no coincide");
        assertEquals("Aceite de Oliva", resultado.getProducto().getNombre());
        verify(inventarioRepository, times(1)).findById(1L);
    }

    @Test
    void testFindAll_RetornaMovimientosDeInventario() {
        Inventario entrada = crearMovimiento(1L, "ENTRADA", 50, 10L, "Arroz");
        Inventario salida = crearMovimiento(2L, "SALIDA", 15, 10L, "Arroz");
        when(inventarioRepository.findAll()).thenReturn(Arrays.asList(entrada, salida));

        List<Inventario> resultado = inventarioService.findAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("ENTRADA", resultado.get(0).getTipoMovimiento());
        assertEquals("SALIDA", resultado.get(1).getTipoMovimiento());
        verify(inventarioRepository, times(1)).findAll();
    }

    @Test
    void testFindById_NoEncontrado_RetornaNull() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        Inventario resultado = inventarioService.findById(99L);

        assertNull(resultado, "Cuando no existe el movimiento, el servicio debe retornar null");
        verify(inventarioRepository, times(1)).findById(99L);
    }

    @Test
    void testSave_RegistraMovimientoEntrada() {
        Inventario entrada = crearMovimiento(null, "ENTRADA", 30, 5L, "Fideos");
        Inventario guardado = crearMovimiento(1L, "ENTRADA", 30, 5L, "Fideos");
        when(inventarioRepository.save(entrada)).thenReturn(guardado);

        Inventario resultado = inventarioService.save(entrada);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("ENTRADA", resultado.getTipoMovimiento());
        assertEquals(30, resultado.getCantidad());
        assertEquals(5L, resultado.getProducto().getId());
        verify(inventarioRepository, times(1)).save(entrada);
    }

    @Test
    void testDeleteById_EliminaMovimiento() {
        doNothing().when(inventarioRepository).deleteById(1L);

        assertDoesNotThrow(() -> inventarioService.deleteById(1L));

        verify(inventarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByProductoId_RetornaMovimientosDelProducto() {
        Inventario entrada = crearMovimiento(1L, "ENTRADA", 100, 20L, "Leche");
        Inventario salida = crearMovimiento(2L, "SALIDA", 40, 20L, "Leche");
        when(inventarioRepository.findByProductoId(20L)).thenReturn(Arrays.asList(entrada, salida));

        List<Inventario> resultado = inventarioService.findByProductoId(20L);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(mov -> mov.getProducto().getId().equals(20L)));
        assertEquals("Leche", resultado.get(0).getProducto().getNombre());
        verify(inventarioRepository, times(1)).findByProductoId(20L);
    }

    @Test
    void testMovimientoSalida_CantidadValidaYProductoCorrecto() {
        Inventario salida = crearMovimiento(3L, "SALIDA", 12, 30L, "Azucar");

        assertEquals("SALIDA", salida.getTipoMovimiento());
        assertTrue(salida.getCantidad() > 0, "La salida de inventario debe registrar una cantidad positiva");
        assertNotNull(salida.getProducto());
        assertEquals(30L, salida.getProducto().getId());
        assertEquals("Azucar", salida.getProducto().getNombre());
    }

    private Inventario crearMovimiento(Long id, String tipoMovimiento, Integer cantidad, Long productoId, String nombreProducto) {
        Producto producto = new Producto();
        producto.setId(productoId);
        producto.setNombre(nombreProducto);

        Inventario inventario = new Inventario();
        inventario.setId(id);
        inventario.setProducto(producto);
        inventario.setTipoMovimiento(tipoMovimiento);
        inventario.setCantidad(cantidad);
        inventario.setFechaMovimiento(new Date());
        return inventario;
    }
}
