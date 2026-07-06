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

    @Value("${file.server-url:http://localhost:8080}")
    private String baseUrl;

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

    @Override
    public boolean deleteByUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return false;
        }

        String base = baseUrl + "/avatars/";
        if (!fileUrl.startsWith(base)) {
            throw new IllegalArgumentException("无效的头像URL，请确保URL以 " + base + " 开头");
        }

        String fileName = fileUrl.substring(base.length());
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("非法文件名");
        }

        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(fileName);

        if (!Files.exists(filePath)) {
            return false;
        }

        try {
            Files.delete(filePath);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("删除头像文件失败：" + e.getMessage(), e);
        }
    }

}