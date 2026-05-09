package org.bnb.trendyol_last.service;

import org.bnb.trendyol_last.dto.ProductDTO;
import org.bnb.trendyol_last.dto.ProductRequestDTO;
import org.bnb.trendyol_last.model.Product;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    public List<ProductDTO> getAllProducts();
    public ProductDTO getProductById(long id);
    public ProductDTO createProduct(ProductRequestDTO requestProduct,  MultipartFile image);
    public ProductDTO updateProduct(long id, ProductRequestDTO updateProduct, MultipartFile image);
    public void deleteProduct(long id);
    Resource getProductImage(long id);

    String getProductImageContentType(long id);
}
