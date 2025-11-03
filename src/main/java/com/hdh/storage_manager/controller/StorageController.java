package com.hdh.storage_manager.controller;

import com.hdh.storage_manager.entities.FileMetadata;
import com.hdh.storage_manager.services.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class StorageController {
    private final FileStorageService fileStorageService;

    public StorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("uploading file :" + file.getOriginalFilename());
        FileMetadata fileMetadata = fileStorageService.storeFile(file);
        return ResponseEntity.ok(fileMetadata);
    }

    @PostMapping("/upload-chunk")
    public ResponseEntity<?> uploadChunk(@RequestParam("file") MultipartFile file,
            @RequestParam("chunkNumber") int chunkNumber,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("uploadId") String uploadId,
            @RequestParam("fileName") String fileName) {
        System.out.println("uploading chunk :" + chunkNumber + "\n for file name :" + fileName);
        fileStorageService.storeChunk(file, chunkNumber, uploadId);
        if (chunkNumber == totalChunks - 1) {
            FileMetadata fileMetadata = fileStorageService.mergeChunks(fileName, uploadId, totalChunks,
                    file.getContentType());
            return ResponseEntity.ok(fileMetadata);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/helloWorld")
    public ResponseEntity<String> helloWorld() {
        System.out.println("Hello World");
        // ObjectNode jsonResponse = new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode();
        // jsonResponse.put("message", "Hello World");
        return ResponseEntity.ok("Hello World");
    }

    @GetMapping("/helloWorlds")
    public ResponseEntity<String> helloWorlds(){
        System.out.println("Hello World");
        return ResponseEntity.ok("{'message':'Hello World'}");
    }

}
