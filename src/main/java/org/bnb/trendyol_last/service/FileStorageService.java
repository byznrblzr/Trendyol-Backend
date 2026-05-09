package org.bnb.trendyol_last.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path productImagePath = Paths.get("uploads/products");


    public String saveProductImage(MultipartFile image) {
        try {
            // Eğer image hiç gönderilmemişse veya boş gönderilmişse kaydetme işlemi yapmayız.
            if (image == null || image.isEmpty()) {
                return null;
            }
            //tip kontrolü
            validateImage(image);

            Files.createDirectories(productImagePath);

            //orijinal adını alıyoruz
            String originalFileName = image.getOriginalFilename();
            String extension = getFileExtension(originalFileName);
            String imageName = UUID.randomUUID() + extension;
            Path targetPath = productImagePath.resolve(imageName);
            Files.copy(image.getInputStream(), targetPath);
            return imageName;

        }

        catch (IOException e) {
            throw new RuntimeException("Image could not be saved: " + e.getMessage());
        }
    }


    public void deleteProductImage(String imageName) {
        try {
            if (imageName == null || imageName.isEmpty()) {
                return;
            }

            Path imagePath = productImagePath.resolve(imageName);
            Files.deleteIfExists(imagePath);

        }
        catch (IOException e) {
            throw new RuntimeException("Image could not be deleted: " + e.getMessage());
        }
    }


    private void validateImage(MultipartFile image) {
        String contentType = image.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/jpg"))) {
            throw new RuntimeException("Only JPG, JPEG and PNG images are allowed.");
        }
    }


    private String getFileExtension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {return "";}

        return originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    public String getProductImagePath(String imageName) {
        return productImagePath.resolve(imageName).toString();
    }
}