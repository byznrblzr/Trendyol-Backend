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

    @Column(length = 255)
    private String imageName;

    @Column(length = 500)
    private String imagePath;

    @Column(length = 100)
    private String imageType;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Order> orders;

}
