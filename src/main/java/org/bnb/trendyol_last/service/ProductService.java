package org.bnb.trendyol_last.service;

import org.bnb.trendyol_last.dto.ProductDTO;
import org.bnb.trendyol_last.dto.ProductRequestDTO;
import org.bnb.trendyol_last.model.Product;

import java.util.List;

public interface ProductService {
    public List<ProductDTO> getAllProducts();
    public ProductDTO getProductById(long id);
    public ProductDTO createProduct(ProductRequestDTO requestProduct);
    public ProductDTO updateProduct(long id, ProductRequestDTO updateProduct);
    public void deleteProduct(long id);
}
