package com.minimarket.service.impl;

import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional
    public Producto save(Producto producto) {
        // CORREGIDO: Validación contra nulos preventiva para evitar NullPointerException en testSave
        if (producto != null && producto.getStock() != null && producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock inicial no puede ser negativo");
        }
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // CORREGIDO: Mantiene la verificación preventiva sin romper el aislamiento del mock
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("No se puede eliminar: Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByCategoriaId(Long categoriaId) {
        // CORREGIDO: Reemplaza el lanzamiento de UnsupportedOperationException por la consulta real al repositorio
        return productoRepository.findByCategoriaId(categoriaId);
    }
}