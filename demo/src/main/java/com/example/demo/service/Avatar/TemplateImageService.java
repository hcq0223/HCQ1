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
public class TemplateImageService implements ImageService {

    @Value("${file.template-image-dir}")
    private String uploadDir;

    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png");

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new RuntimeException("file is empty");
        if (file.getSize() > MAX_SIZE) throw new RuntimeException("file exceeds 5MB");
        String ct = file.getContentType();
        if (ct == null || !ALLOWED_TYPES.contains(ct)) throw new RuntimeException("only JPEG/PNG allowed");

        String orig = file.getOriginalFilename();
        String ext = "";
        if (orig != null && orig.contains(".")) ext = orig.substring(orig.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + ext;

        Path dir = Paths.get(uploadDir);
        try {
            if (!Files.exists(dir)) Files.createDirectories(dir);
            file.transferTo(dir.resolve(filename).toFile());
        } catch (IOException e) {
            throw new RuntimeException("save failed: " + e.getMessage(), e);
        }
        return "http://localhost:8080/template-images/" + filename;
    }
}