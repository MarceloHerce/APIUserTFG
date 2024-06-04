package org.example.controllers.privateControllers;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.Block;
import com.azure.storage.blob.models.BlockList;
import com.azure.storage.blob.models.BlockListType;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlockBlobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/blob")
@CrossOrigin
public class PruebaBlock {
    private static final Logger log = LoggerFactory.getLogger(PruebaBlock.class);

    private final String containerName = "videocontainer";

    @Value("${azureBlobStorageConnectionString}")
    private String connectionString;

    @GetMapping
    public ResponseEntity<List<String>> getAllTurkeys() {
        List<String> videoUrls = new ArrayList<>();
        BlobContainerClient containerClient = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
        try {
            containerClient.listBlobs().forEach(blobItem -> {
                String sasUrl = generateSasForBlob(blobItem.getName());
                videoUrls.add(sasUrl);
            });
        } catch (Exception e) {
            log.error("Error listing blobs or generating SAS URLs", e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(videoUrls);
    }

    public String generateSasForBlob(String blobName) {
        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        BlobContainerClient containerClient = serviceClient.getBlobContainerClient(containerName);

        BlobClient blobClient = containerClient.getBlobClient(blobName);
        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(
                OffsetDateTime.now().plusDays(1), // SAS token expiration time
                BlobContainerSasPermission.parse("r") // read permission
        );

        String sasToken = blobClient.generateSas(values);
        return blobClient.getBlobUrl() + "?" + sasToken;
    }

    @PostMapping("/generate-block-id")
    public ResponseEntity<String> generateBlockId(@RequestParam("index") int index) {
        // Genera un Block ID único y consistente
        String rawBlockId = String.format("%05d-%s", index, UUID.randomUUID().toString());
        String blockId = Base64.getEncoder().encodeToString(rawBlockId.getBytes(StandardCharsets.UTF_8));
        log.info("Generated BlockId: " + blockId);
        return ResponseEntity.ok(blockId);
    }

    @PostMapping("/upload-chunk")
    public ResponseEntity<String> uploadChunk(@RequestParam("chunk") MultipartFile chunk,
                                              @RequestParam("fileName") String fileName,
                                              @RequestParam("blockId") String blockId) throws IOException {
        byte[] chunkBytes = chunk.getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(chunkBytes);
        BlockBlobClient blockBlobClient = new BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(fileName)
                .buildClient()
                .getBlockBlobClient();
        log.info("Uploading BlockId: " + blockId + " for file: " + fileName);
        blockBlobClient.stageBlock(blockId, byteArrayInputStream, chunkBytes.length);

        return ResponseEntity.ok("Chunk received");
    }

    @PostMapping("/commit-blocks")
    public ResponseEntity<String> commitBlocks(@RequestParam("fileName") String fileName,
                                               @RequestParam("blockList") String blockListJson) {
        BlockBlobClient blockBlobClient = new BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(fileName)
                .buildClient()
                .getBlockBlobClient();
        BlockList blockList1 = blockBlobClient.listBlocks(BlockListType.ALL);
        log.info("Uncommitted Blocks:");
        for (Block block : blockList1.getUncommittedBlocks()) {
            log.info("Block ID: " + block.getName() + ", Size: " + block.getSizeLong());
        }

        // Log the blockListJson to ensure it is being received correctly
        log.info("Received blockListJson: " + blockListJson);

        // Parse the blockListJson into a list of block IDs
        String cleanedBlockListJson = blockListJson.replace("[", "").replace("]", "").replace("\"", "");
        List<String> blockList = List.of(cleanedBlockListJson.split(","));

        // Log each block ID to ensure they are correct
        for (String blockId : blockList) {
            log.info("Block ID: " + blockId);
        }

        try {
            // Commit the block list
            blockBlobClient.commitBlockList(blockList);
        } catch (Exception e) {
            // Log the error
            log.error("Error committing block list", e);
            // Return a detailed error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error committing block list: " + e.getMessage());
        }

        // Return success message
        return ResponseEntity.ok("Upload complete");
    }
}
