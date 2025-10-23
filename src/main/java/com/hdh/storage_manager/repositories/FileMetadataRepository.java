package com.hdh.storage_manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hdh.storage_manager.entities.FileMetadata;


@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long>{
    
}
