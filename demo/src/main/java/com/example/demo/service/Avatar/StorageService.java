package com.example.demo.service.Avatar;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * 上传文件到本地存储，返回可访问的 URL
     * @param file 上传的文件
     * @return 文件的访问 URL
     */
    String upload(MultipartFile file);
}
