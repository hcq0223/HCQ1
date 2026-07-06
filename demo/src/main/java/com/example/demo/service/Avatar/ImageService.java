package com.example.demo.service.Avatar;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String upload(MultipartFile file);
    boolean deleteByUrl(String fileUrl);
}
