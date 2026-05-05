package org.bnb.trendyol_last.mapper;

import org.bnb.trendyol_last.dto.ProductDTO;
import org.bnb.trendyol_last.dto.ProductRequestDTO;
import org.bnb.trendyol_last.model.Product;

public class ProductMapper {

    public static ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getSupplier(),
                product.getPrice()
        );
    }

    public static Product toEntity(ProductRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setSupplier(requestDTO.getSupplier());
        product.setPrice(requestDTO.getPrice());

        return product;
    }

    public static void updateEntity(Product product, ProductRequestDTO requestDTO) {
        if (product == null || requestDTO == null) {
            return;
        }

        product.setName(requestDTO.getName());
        product.setSupplier(requestDTO.getSupplier());
        product.setPrice(requestDTO.getPrice());
    }
}