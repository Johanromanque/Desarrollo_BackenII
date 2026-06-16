package com.minimarket.service;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.VentaValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaValidationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaValidationService ventaValidationService;

    private Usuario vendedorValido;
    private Producto leche;
    private Producto pan;

    @BeforeEach
    void setUp() {
        vendedorValido = crearUsuario(1L, "Juan", "Pérez", "juan.perez@minimarket.cl", "Av. Siempre Viva 123", "VENDEDOR");
        leche = crearProducto(10L, "Leche", 1250.0, 20);
        pan = crearProducto(11L, "Pan", 1800.0, 15);
    }

    @Test
    @DisplayName("Debe validar usuario con nombre, apellido, email y dirección completos")
    void deberiaValidarUsuarioConDatosCompletos() {
        assertTrue(ventaValidationService.tieneDatosCompletos(vendedorValido));
        assertEquals("Juan", vendedorValido.getNombre());
        assertEquals("Pérez", vendedorValido.getApellido());
        assertEquals("juan.perez@minimarket.cl", vendedorValido.getEmail());
        assertEquals("Av. Siempre Viva 123", vendedorValido.getDireccion());
    }

    @Test
    @DisplayName("Debe rechazar usuario con datos obligatorios incompletos")
    void deberiaRechazarUsuarioConDatosIncompletos() {
        Usuario usuarioIncompleto = crearUsuario(2L, "", "Pérez", "juan.perez@minimarket.cl", "Av. Siempre Viva 123", "VENDEDOR");

        assertFalse(ventaValidationService.tieneDatosCompletos(usuarioIncompleto));
    }

    @Test
    @DisplayName("Debe obtener usuario válido simulando consulta al repositorio")
    void deberiaObtenerUsuarioValidoDesdeRepositorioMockeado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(vendedorValido));

        Usuario resultado = ventaValidationService.obtenerUsuarioValido(1L);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        assertEquals("juan.perez@minimarket.cl", resultado.getEmail());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario simulado no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ventaValidationService.obtenerUsuarioValido(99L));
        verify(usuarioRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Debe permitir registrar ventas solo con rol ADMIN o VENDEDOR")
    void deberiaValidarRolPermitidoParaRegistrarVenta() {
        Usuario admin = crearUsuario(3L, "Ana", "Admin", "ana@minimarket.cl", "Calle 1", "ADMIN");
        Usuario cliente = crearUsuario(4L, "Luis", "Cliente", "luis@minimarket.cl", "Calle 2", "CLIENTE");

        assertTrue(ventaValidationService.tieneRolValidoParaRegistrarVenta(vendedorValido));
        assertTrue(ventaValidationService.tieneRolValidoParaRegistrarVenta(admin));
        assertFalse(ventaValidationService.tieneRolValidoParaRegistrarVenta(cliente));
    }

    @Test
    @DisplayName("Debe confirmar stock suficiente para una venta")
    void deberiaConfirmarStockSuficiente() {
        assertTrue(ventaValidationService.hayStockSuficiente(leche, 5));
    }

    @Test
    @DisplayName("Debe rechazar venta cuando no hay stock suficiente")
    void deberiaRechazarVentaSinStockSuficiente() {
        assertFalse(ventaValidationService.hayStockSuficiente(leche, 25));
        assertThrows(IllegalArgumentException.class, () -> ventaValidationService.calcularStockRestante(leche, 25));
    }

    @Test
    @DisplayName("Debe calcular correctamente el total de una venta")
    void deberiaCalcularTotalDeVenta() {
        DetalleVenta detalleLeche = crearDetalle(leche, 2, 1250.0);
        DetalleVenta detallePan = crearDetalle(pan, 3, 1800.0);

        double total = ventaValidationService.calcularTotal(List.of(detalleLeche, detallePan));

        assertEquals(7900.0, total);
    }

    @Test
    @DisplayName("Debe calcular correctamente el stock restante")
    void deberiaCalcularStockRestante() {
        int stockRestante = ventaValidationService.calcularStockRestante(leche, 7);

        assertEquals(13, stockRestante);
    }

    @Test
    @DisplayName("Debe registrar venta válida simulando usuario, producto y repositorio de venta")
    void deberiaRegistrarVentaValidaUsandoMocks() {
        Venta venta = crearVentaConDetalles(vendedorValido, List.of(crearDetalle(leche, 2, 1250.0)));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(vendedorValido));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(leche));
        when(ventaRepository.save(venta)).thenReturn(venta);

        Venta resultado = ventaValidationService.registrarVenta(venta);

        assertNotNull(resultado);
        assertEquals(vendedorValido, resultado.getUsuario());
        assertEquals(1, resultado.getDetalles().size());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).findById(10L);
        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    @DisplayName("Debe validar relaciones venta-usuario, venta-detalle y detalle-producto")
    void deberiaValidarRelacionesEntreObjetosDelModelo() {
        DetalleVenta detalle = crearDetalle(leche, 2, 1250.0);
        Venta venta = crearVentaConDetalles(vendedorValido, List.of(detalle));
        detalle.setVenta(venta);

        assertSame(vendedorValido, venta.getUsuario());
        assertSame(venta, detalle.getVenta());
        assertSame(leche, detalle.getProducto());
        assertEquals("Lácteos", detalle.getProducto().getCategoria().getNombre());
    }

    private Usuario crearUsuario(Long id, String nombre, String apellido, String email, String direccion, String rol) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername(email);
        usuario.setPassword("password123");
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setDireccion(direccion);
        usuario.setRoles(Set.of(new Rol(rol)));
        return usuario;
    }

    private Producto crearProducto(Long id, String nombre, Double precio, Integer stock) {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre(nombre.equals("Leche") ? "Lácteos" : "Panadería");

        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setCategoria(categoria);
        return producto;
    }

    private DetalleVenta crearDetalle(Producto producto, Integer cantidad, Double precio) {
        DetalleVenta detalleVenta = new DetalleVenta();
        detalleVenta.setProducto(producto);
        detalleVenta.setCantidad(cantidad);
        detalleVenta.setPrecio(precio);
        return detalleVenta;
    }

    private Venta crearVentaConDetalles(Usuario usuario, List<DetalleVenta> detalles) {
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(new Date());
        venta.setDetalles(detalles);
        return venta;
    }
}
