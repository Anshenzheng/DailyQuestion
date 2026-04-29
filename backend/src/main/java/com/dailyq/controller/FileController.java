package com.dailyq.controller;

import com.dailyq.dto.Result;
import com.dailyq.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadImage(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }
}
