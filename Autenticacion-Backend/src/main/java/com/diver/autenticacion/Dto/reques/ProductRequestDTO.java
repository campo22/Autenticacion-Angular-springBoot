package com.diver.autenticacion.Dto.reques;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link com.diver.autenticacion.entities.Product}
 */
@Data
@Builder
public class ProductRequestDTO{
    private Long id;
    @Size(message = "El nombre no puede exceder los 100 caracteres.", max = 100)
    @NotBlank(message = "El nombre no puede estar en blanco.")
    private String name;
    @Size(message = "La descripción no puede exceder los 255 caracteres.", max = 255)
    private String description;
    @Positive(message = "El precio debe ser mayor a 0.")
    private double price;
}