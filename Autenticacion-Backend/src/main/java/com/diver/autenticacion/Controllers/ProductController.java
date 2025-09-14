package com.diver.autenticacion.Controllers;


import com.diver.autenticacion.Dto.ProductDTO;
import com.diver.autenticacion.Dto.reques.ProductRequestDTO;
import com.diver.autenticacion.Services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Obtener todos los productos.
     * Acceso: Cualquier usuario autenticado.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    /**
     * Obtener un producto por su ID.
     * Acceso: Cualquier usuario autenticado.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    /**
     * Crear un nuevo producto.
     * Acceso: Solo SUPERVISOR y ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductDTO savedProduct = productService.save(productRequestDTO);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    /**
     * Actualizar un producto existente.
     * Acceso: Solo SUPERVISOR y ADMIN.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductDTO updatedProduct = productService.update(id, productRequestDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Eliminar un producto.
     * Acceso: Solo ADMIN.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }
}