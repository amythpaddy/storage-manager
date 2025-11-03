package com.hdh.storage_manager.services;

import com.hdh.storage_manager.entities.FileMetadata;
import com.hdh.storage_manager.repositories.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;
    private final FileMetadataRepository fileMetadataRepository;
    private final Path tempStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir, FileMetadataRepository fileMetadataRepository){
        this.fileMetadataRepository = fileMetadataRepository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.tempStorageLocation = Paths.get(uploadDir , "temp").toAbsolutePath().normalize();
        try{
            Files.createDirectories(this.fileStorageLocation);
            Files.createDirectories(this.tempStorageLocation);
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

    public void storeChunk(MultipartFile chunk, int chunkNumber, String uploadId){
        try{
            Path chunkDir = this.tempStorageLocation.resolve(uploadId);
            Files.createDirectories(chunkDir);
            Path chunkFile = chunkDir.resolve(String.valueOf(chunkNumber));
            Files.copy(chunk.getInputStream(), chunkFile, StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException ex){
            throw new RuntimeException("Could not store chunk " + chunkNumber + " for upload " + uploadId + ". Please try again!", ex);
        }
    }

    public FileMetadata mergeChunks(String fileName, String uploadId, int totalChunks, String contentType){
        Path chunkDir = this.tempStorageLocation.resolve(uploadId);
        Path finalFile = this.fileStorageLocation.resolve(fileName);
        try(FileChannel dest = new FileOutputStream(finalFile.toFile()).getChannel()){
            for(int i =0; i<totalChunks;i++){
                Path chunkFile = chunkDir.resolve(String.valueOf(i));
                try(FileChannel src = new FileInputStream(chunkFile.toFile()).getChannel()){
                    dest.transferFrom(src, dest.size(), src.size());
                }
                Files.delete(chunkFile);
            }
            Files.delete(chunkDir);
        }catch(IOException ex){
            throw new RuntimeException("Could not merge chunks for upload " + uploadId + ". Please try again!", ex);
        }
        FileMetadata fileMetadata = new FileMetadata(null, fileName, contentType, finalFile.toString());
        return fileMetadataRepository.save(fileMetadata);
    }


}
