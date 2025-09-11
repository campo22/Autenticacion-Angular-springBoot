package com.diver.autenticacion.Services.Impl;

import com.diver.autenticacion.Dto.ProductDTO;
import com.diver.autenticacion.Dto.reques.ProductRequestDTO;
import com.diver.autenticacion.Exceptions.ResourceNotFoundException;
import com.diver.autenticacion.Repository.ProductRepository;
import com.diver.autenticacion.Services.ProductService;
import com.diver.autenticacion.entities.Product;
import com.diver.autenticacion.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Transactional(readOnly = true)
    @Override
    public List<ProductDTO> findAll()  {
        log.info("Buscando todos los productos");
        List<Product> products = productRepository.findAll();
        return productMapper.toProductDTOList( products);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductDTO findById(Long id) {
        log.info("Buscando producto por id: {}", id);
        Product product= productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException( "Producto no encontrado con id: {" + id + "}"));
        return productMapper.toProductDTO( product);
    }

    @Transactional
    @Override
    public ProductDTO save(ProductRequestDTO productRequestDTO) {
        log.info("Guardando producto: {}", productRequestDTO);
        Product product= productMapper.toproductEntity(productRequestDTO);
        productRepository.save(product);
        log.info("Producto guardado con id: {}", product.getId());
        return productMapper.toProductDTO(product);
    }

    @Transactional
    @Override
    public ProductDTO update(Long id, ProductRequestDTO productRequestDTO) {
        log.info("Actualizando producto con id: {}, id");
        Product productToUpdate = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Producto no encontrado con id: {" + id + "}"));

        productMapper.updateProductFromDto( productRequestDTO, productToUpdate);
        Product updatedProduct = productRepository.save(productToUpdate);
        log.info("Producto actualizado con id: {}", updatedProduct.getId());
        return productMapper.toProductDTO(updatedProduct);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {

       log.info("Eliminando producto con id: {}", id);
       if ( !productRepository.existsById(id)){
           throw new ResourceNotFoundException("Producto no encontrado con id: {" + id + "}");
       }
       productRepository.deleteById(id);
       log.info("Producto eliminado con id: {}", id);

    }
}
