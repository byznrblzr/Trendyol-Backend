package org.bnb.trendyol_last.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bnb.trendyol_last.dto.ProductDTO;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String supplier;

    private Double price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Order> orders;

    /*public Product(ProductDTO ProductDTO) {
        this.id = ProductDTO.getId();
        this.name = ProductDTO.getName();
        this.supplier = ProductDTO.getSupplier();
        this.price = ProductDTO.getPrice();
    }

    public ProductDTO viewAsProductDTO() {
        return new ProductDTO(id, name, supplier, price);
    }*/
}
