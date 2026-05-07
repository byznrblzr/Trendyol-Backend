package org.bnb.trendyol_last.controller;

import org.bnb.trendyol_last.dto.ProductDTO;
import org.bnb.trendyol_last.dto.ProductRequestDTO;
import org.bnb.trendyol_last.model.Product;
import org.bnb.trendyol_last.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping(path = "/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {this.productService = productService;}

    @GetMapping(path = "/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    /*@GetMapping("/image/{id}")
public ResponseEntity<Resource> getProductImage(@PathVariable Long id) {
   // service image dosyasını bulur ve döndürür
}*/

    @GetMapping(path = "/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable long id) {
        if (id == 0) {return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }
 /*@GetMapping("/image/{id}")
public ResponseEntity<Resource> getProductImage(@PathVariable Long id) {
    // service image dosyasını bulur ve döndürür
}*/

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> createProduct(@RequestPart("product") ProductRequestDTO productRequestDTO, @RequestPart(value = "image", required = false) MultipartFile image) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(productService.createProduct(productRequestDTO, image));
}

    @PutMapping(path = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable long id, @RequestPart("product") ProductRequestDTO updateProduct, @RequestPart(value = "image", required = false) MultipartFile image) {
        ProductDTO updatedProduct = productService.updateProduct(id, updateProduct, image);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}