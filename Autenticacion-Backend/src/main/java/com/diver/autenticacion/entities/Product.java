package com.diver.autenticacion.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Identificador único del producto.
     * Es la clave primaria de la tabla.
     * Se genera automáticamente por la base de datos usando una estrategia de identidad (autoincremento).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del producto.
     * No puede ser nulo o vacío.
     * La longitud está limitada a 100 caracteres.
     */
    @NotBlank(message = "El nombre no puede estar en blanco.")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Descripción detallada del producto.
     * Puede ser nula.
     * La longitud está limitada a 255 caracteres.
     */
    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres.")
    @Column(length = 255)
    private String description;

    /**
     * Precio del producto.
     * No puede ser nulo y debe ser un valor mayor o igual a 0.0.
     * Se utiliza un tipo 'double' para permitir decimales.
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser un número positivo.")
    @Column(nullable = false)
    private double price;


}