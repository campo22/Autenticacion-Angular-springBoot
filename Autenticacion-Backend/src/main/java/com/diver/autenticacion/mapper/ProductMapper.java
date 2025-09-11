package com.diver.autenticacion.mapper;

import com.diver.autenticacion.Dto.ProductDTO;
import com.diver.autenticacion.Dto.reques.ProductRequestDTO;
import com.diver.autenticacion.entities.Product;
import org.mapstruct.*;

import java.util.List;

// Anotación que indica que esta interfaz es un mapeador y que usaremos Spring para la inyección de dependencias
// componentModel = "spring": Indica que usaremos Spring para la inyección de dependencias.
// nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE: Indica que ignoraremos los valores nulos al mapear.
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    //  convertir un producto a un productoDTO
    ProductDTO toProductDTO (Product product);

    // convertir una lista de productos a una lista de productosDTO
    List<ProductDTO> toProductDTOList (List<Product> products);

    // Convierte un DTO de Petición a una Entidad (para crear un producto nuevo)
    Product toproductEntity (ProductRequestDTO productRequestDTO );


    // Actualiza una Entidad existente con los datos de un DTO de Petición (para actualizar)
    // @MappingTarget le dice a MapStruct que no cree una nueva instancia, sino que modifique la existente.
    void updateProductFromDto(ProductRequestDTO productRequestDTO, @MappingTarget Product product);
}