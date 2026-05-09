package org.bnb.trendyol_last.service;

import org.bnb.trendyol_last.dto.ProductDTO;
import org.bnb.trendyol_last.dto.ProductRequestDTO;
import org.bnb.trendyol_last.exception.ErrorMessages;
import org.bnb.trendyol_last.exception.ResourceAlreadyExistsException;
import org.bnb.trendyol_last.exception.ResourceNotFoundException;
import org.bnb.trendyol_last.mapper.ProductMapper;
import org.bnb.trendyol_last.model.Product;
import org.bnb.trendyol_last.repository.ProductRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final Path productImagePath = Paths.get("uploads/products");

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;}

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(ProductMapper::toDTO).toList();}

    public ProductDTO getProductById(long id) {
         Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + id));
         return ProductMapper.toDTO(product);}


    public ProductDTO createProduct(ProductRequestDTO requestProduct, MultipartFile image) {
        Product product = ProductMapper.toEntity(requestProduct);

        // Önce product kaydedilir ki id oluşsun.
        Product savedProduct = productRepository.save(product);

        // Image varsa product id ile ilişkili isimle kaydedilir.
        if (image != null && !image.isEmpty()) {
            String imageName = saveProductImage(image, savedProduct.getId());
            savedProduct.setImageName(imageName);
            savedProduct.setImageType(image.getContentType());
            savedProduct.setImagePath("uploads/products/" + imageName);
            savedProduct = productRepository.save(savedProduct);
        }
        return ProductMapper.toDTO(savedProduct);}


    public ProductDTO updateProduct(long id, ProductRequestDTO updateProduct, MultipartFile image) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + id));

        ProductMapper.updateEntity(existingProduct, updateProduct);

        // Yeni image geldiyse eski image silinir, yenisi kaydedilir.
        if (image != null && !image.isEmpty()) {

            if (existingProduct.getImageName() != null) {
                deleteProductImage(existingProduct.getImageName());
            }
            String imageName = saveProductImage(image, existingProduct.getId());
            existingProduct.setImageName(imageName);
            existingProduct.setImageType(image.getContentType());
            existingProduct.setImagePath("uploads/products/" + imageName);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductMapper.toDTO(updatedProduct);}

    public void deleteProduct(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + id
                ));

        if (product.getImageName() != null) {
            deleteProductImage(product.getImageName());}

        productRepository.delete(product);}





    //id'ye göre ürünün image dosyasını bulup Resource olarak döndürür.
    // Controller bu Resource'u HTTP response içinde kullanıcıya gönderir.
    public Resource getProductImage(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + id
                ));
        if (product.getImageName() == null || product.getImageName().isEmpty()) {
            throw new ResourceNotFoundException("Product image not found: " + id);}
        // Image dosyasını uploads/products klasöründen bulur ve Resource olarak döndürür.
        return loadProductImage(product.getImageName());}



    // Product id'ye göre ürün resminin dosya tipini döndürür.
    // Örnek dönüş: image/jpeg, image/png
    public String getProductImageContentType(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + id
                ));

        if (product.getImageType() == null || product.getImageType().isEmpty()) {
            return "application/octet-stream";}

        return product.getImageType();}


    //Bu metot ürün resmi kaydetmek için kullanılır.
     //yüklenen resim dosyasını ve var olan product id sini alır
     //dosyanın adını döndürür.Örnek: product-1.jpg
    private String saveProductImage(MultipartFile image, Long productId) {
        try {
            // Yüklenen dosyanın içerik tipini alıyoruz.
            //dosyanın gerçekten image olup olmadığını kontrol edilir
            String contentType = image.getContentType();

            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Sadece image dosyası yüklenebilir.");
            }
            //getOriginalFilename() photo.jpeg gibi bir şey. contentType tipini verir image/jpeg gibi
            String extension = getFileExtension(image.getOriginalFilename(), contentType);

            //Kaydedilecek dosyanın adını oluştur
            //Örneğin productId = 1 ve extension = ".jpg" ise: imageName = "product-1.jpg"
            //Aynı product'a tekrar image yüklenirse eski dosyanın üstüne yazabilmek için yapıyorum
            String imageName = "product-" + productId + extension;

            //resolve(...) bir Path metodudur. klasör yolu ile dosya adını birleştirir.
            //Klasör yolu + dosya adını birleştirip, resmin kaydedileceği tam yolu oluşturur uploads/products/product-1.jpg gibi
            Path targetPath = productImagePath.resolve(imageName);

            //Postman’dan gelen image dosyasının içeriğini oku ve targetPath konumuna fiziksel dosya olarak kaydet.
            //Files.copy(kaynak, hedef,seçenek)
            Files.copy(
                    image.getInputStream(), // yüklenen dosyanın içeriğini okur.
                    targetPath, // tam yolu az önce oluşturdum
                    StandardCopyOption.REPLACE_EXISTING //Eğer aynı isimde dosya varsa üstüne yaz demektir.
            );

            //product-1.jpeg döndürür.
            return imageName;
        } catch (IOException e) {
            throw new RuntimeException("Product image kaydedilemedi.", e);}}


    //Verilen dosyanın adını kullanarak image dosyasını bulmak ve döndürmek.
    private Resource loadProductImage(String imageName) {
        try {
            // uploads/products klasörü ile imageName'i birleştirir.
            //normalize Dosya yolunu temizler,düzeltir.
            Path imagePath = productImagePath.resolve(imageName).normalize();

            //UrlResource dosyayı URI/URL formatında almak ister. o yüzden Dosya yolunu URI formatına çevirir.
            // Dosya yolundaki gerçek dosyayı Spring’in döndürebileceği Resource nesnesine çevirir
            Resource resource = new UrlResource(imagePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Image file not found: " + imageName);
            }
            //her şey tamamsa controller bu resmi kullanıcıya gönderebilir.
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Image dosyası okunamadı.", e);}}


    //Verilen image dosya adını kullanarak resmi uploads/products klasöründen siler.
    private void deleteProductImage(String imageName) {
        try {
            Path imagePath = productImagePath.resolve(imageName).normalize();
            //Verilen path’te dosya varsa siler, yoksa hata vermez.
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("Product image silinemedi.", e);}}


    // Yüklenen dosyanın uzantısını bulur.
    //önce dosya adına, bulamazsa contentType'a bakar.
    private String getFileExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf(".")); }
        if ("image/png".equals(contentType)) {return ".png";}
        if ("image/jpeg".equals(contentType)) {return ".jpg";}
        return ".jpg";}
}
