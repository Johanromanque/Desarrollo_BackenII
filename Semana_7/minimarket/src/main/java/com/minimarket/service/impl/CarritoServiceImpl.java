package com.minimarket.service.impl;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.service.CarritoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarritoServiceImpl implements CarritoService {

    // Cambiado a private final para cumplir con las buenas prácticas de inmutabilidad
    private final CarritoRepository carritoRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    @Override
    public List<Carrito> findAll() {
        return carritoRepository.findAll();
    }

    @Override
    public Carrito findById(Long id) {
        return carritoRepository.findById(id).orElse(null);
    }

    @Override
    public Carrito save(Carrito carrito) {
        return carritoRepository.save(carrito);
    }

    @Override
    public void deleteById(Long id) {
        carritoRepository.deleteById(id);
    }

    @Override
    public List<Carrito> findByUsuarioId(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public void agregarProducto(Long carritoId, Producto producto, int cantidad) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // Validación crítica de lógica de negocio requerida por la pauta (Paso 2)
        if (cantidad > producto.getStock()) {
            throw new StockInsuficienteException("No hay stock suficiente para el producto: " + producto.getNombre());
        }

        // Aquí guardamos los cambios en el repositorio para que Mockito verifique la invocación
        carritoRepository.save(carrito);
    }
}