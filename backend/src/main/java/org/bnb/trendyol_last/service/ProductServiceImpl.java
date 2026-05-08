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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    public ProductServiceImpl(ProductRepository productRepository,FileStorageService fileStorageService) {this.productRepository = productRepository;
    this.fileStorageService = fileStorageService;}

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(ProductMapper::toDTO).toList();
    }

    public ProductDTO getProductById(long id) {
         Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + id));
         return ProductMapper.toDTO(product);
    }

    public ProductDTO createProduct(ProductRequestDTO requestProduct, MultipartFile image) {
        Product product = ProductMapper.toEntity(requestProduct);

        //FOR IMAGE
        if (image != null && !image.isEmpty()) {
            String imageName = fileStorageService.saveProductImage(image);

            product.setImageName(imageName);
            product.setImageType(image.getContentType());
            product.setImagePath("uploads/products/" + imageName);
        }

        Product savedProduct = productRepository.save(product);
        return ProductMapper.toDTO(savedProduct);
    }

    public ProductDTO updateProduct(long id, ProductRequestDTO updateProduct, MultipartFile image) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + id
                ));
        ProductMapper.updateEntity(existingProduct, updateProduct);

        //FOR IMAGE
        if (image != null && !image.isEmpty()) {
            if (existingProduct.getImageName() != null) {
                fileStorageService.deleteProductImage(existingProduct.getImageName());
            }

            String imageName = fileStorageService.saveProductImage(image);

            existingProduct.setImageName(imageName);
            existingProduct.setImageType(image.getContentType());
            existingProduct.setImagePath("uploads/products/" + imageName);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductMapper.toDTO(updatedProduct);
    }

    public void deleteProduct(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + id
                ));

        if (product.getImageName() != null) {
            fileStorageService.deleteProductImage(product.getImageName());
        }

        productRepository.delete(product);
    }

}