package com.hdh.storage_manager.services;

import com.hdh.storage_manager.entities.FileMetadata;
import com.hdh.storage_manager.repositories.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;
    private final FileMetadataRepository fileMetadataRepository;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir, FileMetadataRepository fileMetadataRepository){
        this.fileMetadataRepository = fileMetadataRepository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try{
            Files.createDirectories(this.fileStorageLocation);
        }catch(Exception ex){
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public FileMetadata storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try{
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            FileMetadata fileMetadata = new FileMetadata(null, fileName, file.getContentType(), targetLocation.toString());
            return fileMetadataRepository.save(fileMetadata);
        }catch(IOException ex){
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }


}
