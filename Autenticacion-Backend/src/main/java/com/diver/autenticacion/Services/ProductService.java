package com.diver.autenticacion.Services;

import com.diver.autenticacion.Dto.ProductDTO;
import com.diver.autenticacion.Dto.reques.ProductRequestDTO;

import java.util.List;

public interface ProductService {

    List<ProductDTO> findAll();

    ProductDTO findById(Long id);

    ProductDTO save(ProductRequestDTO productRequestDTO);

    ProductDTO update(Long id, ProductRequestDTO productRequestDTO);

    void deleteById(Long id);
}