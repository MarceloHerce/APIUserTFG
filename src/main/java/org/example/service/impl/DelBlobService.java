package org.example.service.impl;

import com.azure.storage.blob.BlobClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DelBlobService {
    private String connectionString;
    private String containerName;

    public DelBlobService(@Value("${azureBlobStorageConnectionString}") String connectionString,
                          @Value("${azureBlobStorageContainerName}") String containerName) {
        this.connectionString = connectionString;
        this.containerName = containerName;
    }

    public ResponseEntity<String> deleteBlobByName(String fileName) {
        try {
            boolean isDeletedIfExist = new BlobClientBuilder()
                    .connectionString(connectionString)
                    .containerName(containerName)
                    .blobName(fileName)
                    .buildClient()
                    .deleteIfExists();
            if (isDeletedIfExist){
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Blob deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blob not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred while deleting the blob: " + e.getMessage());
        }

    }
}
