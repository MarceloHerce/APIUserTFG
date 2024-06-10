package org.example.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.example.service.IAzureBlobStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
@Service
public class AzureBlobStorageImpl implements IAzureBlobStorage {
    private  String connectionString;
    private  String containerName;

    public AzureBlobStorageImpl (@Value("${azureBlobStorageConnectionString}") String connectionString,
                                 @Value("${azureBlobStorageContainerName}") String containerName){
        this.connectionString = connectionString;
        this.containerName = containerName;

    }
    @Override
    public ResponseEntity<InputStreamResource> getMediaFromStorage(String mediaRef) {

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        BlobClient blobClient = containerClient.getBlobClient(mediaRef);

        if (!blobClient.exists()) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }

        InputStreamResource inputStreamResource = new InputStreamResource(blobClient.openInputStream());

        return ResponseEntity.ok()
                .contentLength(blobClient.getProperties().getBlobSize())
                .contentType(org.springframework.http.MediaType.valueOf("video/webm"))
                .body(inputStreamResource);
    }

    @Override
    public ResponseEntity<String> uploadMediaToStorage(MultipartFile file) {
        BlobContainerClient containerClient = connectToStorage(connectionString,containerName);

        String filename = file.getOriginalFilename();

        try (InputStream is = file.getInputStream()) {
            BlobClient blobClient = containerClient.getBlobClient(filename);
            blobClient.upload(is, file.getSize(), true);
        } catch (IOException ex) {
            return new ResponseEntity<>("Error uploading file", HttpStatus.I_AM_A_TEAPOT);
        }

        return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
    }

    @Override
    public BlobContainerClient connectToStorage(String connectionString, String containerName) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        return blobServiceClient.getBlobContainerClient(containerName);
    }


}
