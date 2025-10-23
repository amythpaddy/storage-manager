package com.hdh.storage_manager.controller;

import com.hdh.storage_manager.entities.FileMetadata;
import com.hdh.storage_manager.services.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class StorageController {
    private final FileStorageService fileStorageService;
    public StorageController(FileStorageService fileStorageService){
        this.fileStorageService = fileStorageService;
    }
    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> uploadFile(@RequestParam("file") MultipartFile file){
        FileMetadata fileMetadata = fileStorageService.storeFile(file);
        return ResponseEntity.ok(fileMetadata);
    }
    
}
