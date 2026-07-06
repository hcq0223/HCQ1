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
    @Value("${file.server-url:http://localhost:8080}")
    private String baseUrl;

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
        return baseUrl + "/template-images/" + filename;
    }

    @Override
    public boolean deleteByUrl(String fileUrl) {

        if (fileUrl == null || fileUrl.isBlank()) {
            return false;
        }

        // 构建期望的 URL 前缀（与 upload 返回的格式保持一致）
        String expectedPrefix = baseUrl + "/template-images/";
        if (!fileUrl.startsWith(expectedPrefix)) {
            throw new IllegalArgumentException("无效的模板图片 URL，请确保以 " + expectedPrefix + " 开头");
        }

        String fileName = fileUrl.substring(expectedPrefix.length());
        // 防止路径遍历攻击
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("非法文件名");
        }

        Path filePath = Paths.get(uploadDir).resolve(fileName);
        if (!Files.exists(filePath)) {
            return false;
        }

        try {
            Files.delete(filePath);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("删除模板图片文件失败：" + e.getMessage(), e);
        }
    }
}
