package org.example.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class AzureBlockBlobService {
    private static final Logger logger = LoggerFactory.getLogger(AzureBlockBlobService.class);

    @Value("${azureBlobStorageConnectionString}")
    private String connectionString;

/*
    public AzureBlockBlobService(@Value("${azureBlobStorageConnectionString}") String connectionString) {
        this.connectionString = connectionString;
    }

    public String uploadBlock(String containerName, String blobName, MultipartFile filePart, String blockId) {
        BlobServiceClient serviceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        BlobClient blobClient = serviceClient.getBlobContainerClient(containerName).getBlobClient(blobName);

        try (InputStream inputStream = new BufferedInputStream(filePart.getInputStream())) {
            long fileSize = filePart.getSize();
            logger.info("Uploading block ID: {} with size: {} bytes", blockId, fileSize);
            blobClient.getBlockBlobClient().stageBlock(blockId, inputStream, fileSize);
            return blobClient.getBlobUrl();
        } catch (BlobStorageException e) {
            logger.error("Failed to upload block ID: {}. Error Code: {}, Message: {}", blockId, e.getStatusCode(), e.getMessage(), e);
            throw new RuntimeException("Error uploading block to Azure Blob Storage", e);
        } catch (Exception e) {
            logger.error("Error reading file contents", e);
            throw new RuntimeException("Error processing the file", e);
        }
    }

    public String commitBlocks(String containerName, String blobName, List<String> blockIds) {
        BlobServiceClient serviceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        BlobClient blobClient = serviceClient.getBlobContainerClient(containerName).getBlobClient(blobName);

        try {
            logger.info("Committing blocks for blob: {}", blobName);
            blobClient.getBlockBlobClient().commitBlockList(blockIds);
            logger.info("Blocks committed successfully.");
            return blobClient.getBlobUrl();
        } catch (BlobStorageException e) {
            logger.error("Failed to commit blocks for blob: {}. Error Code: {}, Message: {}", blobName, e.getStatusCode(), e.getMessage(), e);
            throw new RuntimeException("Error committing blocks to Azure Blob Storage", e);
        }
    }*/
}
