package org.bnb.trendyol_last.service;

import org.bnb.trendyol_last.dto.ProductDTO;
import org.bnb.trendyol_last.dto.ProductRequestDTO;
import org.bnb.trendyol_last.exception.ErrorMessages;
import org.bnb.trendyol_last.exception.ResourceAlreadyExistsException;
import org.bnb.trendyol_last.exception.ResourceNotFoundException;
import org.bnb.trendyol_last.mapper.ProductMapper;
import org.bnb.trendyol_last.model.Product;
import org.bnb.trendyol_last.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {this.productRepository = productRepository;}

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(ProductMapper::toDTO).toList();
    }

    public ProductDTO getProductById(long id) {
         Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + id));
         return ProductMapper.toDTO(product);
    }

    public ProductDTO createProduct(ProductRequestDTO requestProduct) {
        Product product = ProductMapper.toEntity(requestProduct);
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toDTO(savedProduct);
    }

    public ProductDTO updateProduct(long id, ProductRequestDTO updateProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + id
                ));
        ProductMapper.updateEntity(existingProduct, updateProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        return ProductMapper.toDTO(updatedProduct);
    }

    public void deleteProduct(long id) {
        productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + id));
        productRepository.deleteById(id);
    }

}