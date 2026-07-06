package com.minimarket.service;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;

import java.util.List;

public interface CarritoService {
    List<Carrito> findAll();
    Carrito findById(Long id);
    Carrito save(Carrito carrito);
    void deleteById(Long id);
    List<Carrito> findByUsuarioId(Long usuarioId);
    
    void agregarProducto(Long carritoId, Producto producto, int cantidad);
}