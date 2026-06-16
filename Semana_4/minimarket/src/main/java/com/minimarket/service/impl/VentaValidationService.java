package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VentaValidationService {

    private static final Set<String> ROLES_PERMITIDOS_REGISTRAR_VENTA = Set.of("ADMIN", "VENDEDOR");

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;

    public VentaValidationService(UsuarioRepository usuarioRepository,
                                  ProductoRepository productoRepository,
                                  VentaRepository ventaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
    }

    public Usuario obtenerUsuarioValido(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .filter(this::tieneDatosCompletos)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no válido o con datos incompletos"));
    }

    public boolean tieneDatosCompletos(Usuario usuario) {
        return usuario != null
                && tieneTexto(usuario.getNombre())
                && tieneTexto(usuario.getApellido())
                && tieneTexto(usuario.getEmail())
                && tieneTexto(usuario.getDireccion());
    }

    public boolean tieneRolValidoParaRegistrarVenta(Usuario usuario) {
        if (usuario == null || usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            return false;
        }
        return usuario.getRoles().stream()
                .map(Rol::getNombre)
                .filter(this::tieneTexto)
                .anyMatch(ROLES_PERMITIDOS_REGISTRAR_VENTA::contains);
    }

    public boolean hayStockSuficiente(Producto producto, int cantidadSolicitada) {
        return producto != null
                && producto.getStock() != null
                && cantidadSolicitada > 0
                && producto.getStock() >= cantidadSolicitada;
    }

    public int calcularStockRestante(Producto producto, int cantidadVendida) {
        if (!hayStockSuficiente(producto, cantidadVendida)) {
            throw new IllegalArgumentException("Stock insuficiente para el producto");
        }
        return producto.getStock() - cantidadVendida;
    }

    public double calcularTotal(List<DetalleVenta> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return 0.0;
        }
        return detalles.stream()
                .mapToDouble(detalle -> detalle.getCantidad() * detalle.getPrecio())
                .sum();
    }

    public Venta registrarVenta(Venta venta) {
        if (venta == null || venta.getUsuario() == null || venta.getUsuario().getId() == null) {
            throw new IllegalArgumentException("La venta debe estar asociada a un usuario válido");
        }

        Usuario usuario = obtenerUsuarioValido(venta.getUsuario().getId());
        if (!tieneRolValidoParaRegistrarVenta(usuario)) {
            throw new IllegalArgumentException("El usuario no tiene permisos para registrar ventas");
        }

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un detalle");
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            Long productoId = Optional.ofNullable(detalle.getProducto())
                    .map(Producto::getId)
                    .orElseThrow(() -> new IllegalArgumentException("Detalle de venta sin producto"));

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            if (!hayStockSuficiente(producto, detalle.getCantidad())) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }
        }

        venta.setUsuario(usuario);
        return ventaRepository.save(venta);
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }
}
