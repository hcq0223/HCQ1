package com.example.demo.service.Avatar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

        @Value("${file.upload-dir:/uploads/avatars}")
        private String uploadDir;

        @Value("${file.base-url:http://localhost:8080}")
        private String baseUrl;

        @Override
        public String upload(MultipartFile file) {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("upload file cannot be empty");
            }
            validateFile(file);

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + extension;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                } catch (IOException e) {
                    throw new RuntimeException("create dir failed", e);
                }
            }

            Path filePath = uploadPath.resolve(newFileName);
            try {
                file.transferTo(filePath.toFile());
            } catch (IOException e) {
                throw new RuntimeException("save file failed", e);
            }

            return baseUrl + "/avatars/" + newFileName;
        }

    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png");

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("file size exceeds 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("only JPEG or PNG supported");
        }
    }
}