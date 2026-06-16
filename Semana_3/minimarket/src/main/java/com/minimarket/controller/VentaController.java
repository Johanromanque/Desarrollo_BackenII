package com.minimarket.controller;

import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // CANDADO DE SEGURIDAD: Solo usuarios con rol GERENTE pueden auditar el histórico total de ventas
    @GetMapping
    @PreAuthorize("hasRole('GERENTE')")
    public List<Venta> listarVentas() {
        return ventaService.findAll();
    }

    // CANDADO DE SEGURIDAD: Solo usuarios con rol GERENTE pueden consultar una venta específica por su ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        return (venta != null) ? ResponseEntity.ok(venta) : ResponseEntity.notFound().build();
    }

    // CANDADO DE SEGURIDAD: Tanto el CLIENTE (compra online) como el EMPLEADO (venta presencial) pueden registrar una transacción
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO')")
    public Venta guardarVenta(@RequestBody Venta venta) {
        return ventaService.save(venta);
    }
}