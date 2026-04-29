package com.dailyq.service;

import com.dailyq.config.FileConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    private final FileConfig fileConfig;

    public FileService(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        
        String relativePath = datePath + "/" + fileName;
        String fullPath = fileConfig.getPath() + relativePath;
        
        File destFile = new File(fullPath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        
        try {
            file.transferTo(destFile);
            log.info("文件上传成功: {}", fullPath);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
        
        return fileConfig.getUrlPrefix() + relativePath;
    }
}
