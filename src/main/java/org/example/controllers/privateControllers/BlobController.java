package org.example.controllers.privateControllers;

import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import lombok.RequiredArgsConstructor;
import org.example.controllers.AuthController;
import org.example.service.impl.AzureBlobStorageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.ByteBuffer;

@RestController
@RequestMapping("media")
@RequiredArgsConstructor
@CrossOrigin
public class BlobController {



    private final AzureBlobStorageImpl azureBlobStorageService;
    private String a = "DefaultEndpointsProtocol=https;AccountName=videosrecorder;AccountKey=N+fC5MNlMYmznkVwwy67Wq53nd6mM8Y6e6/RqBaEEBnwHkGT64QxztP4EOC2fUr86WFcSz3vEWnv+ASti9Jalw==;EndpointSuffix=core.windows.net";
    private String b="videocontainer";

    @GetMapping("/{blobName}")
    public ResponseEntity<InputStreamResource> getVideo(@PathVariable String blobName) {
        return azureBlobStorageService.getMediaFromStorage(blobName);
    }

    private static final Logger log = LoggerFactory.getLogger(BlobController.class);


    @PostMapping("")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        return azureBlobStorageService.uploadMediaToStorage(file);

    }
    @PostMapping("/prueba")
    public String prueba() {
        return "El post funciona";
    }

    @PostMapping("/chunk")
    public String uploadChunk(@RequestParam("fileName") String fileName,
                              @RequestParam("chunk") MultipartFile chunk,
                              @RequestParam("blockId") String blockId) throws IOException {

        BlockBlobClient blobClient = azureBlobStorageService.connectToStorage(a,b).getBlobClient(fileName).getBlockBlobClient();
        log.info("fileName:" +fileName + " chunk:"+chunk+" blockId:"+blockId);

        String base64BlockId = Base64.getEncoder().encodeToString(blockId.getBytes());
        log.info("base64BlockID:" + base64BlockId);
        try (InputStream inputStream = new BufferedInputStream(chunk.getInputStream())) {
            blobClient.stageBlock(base64BlockId, inputStream, chunk.getSize());
        } catch (Exception e){
            throw e;
        }

        return "Chunk uploaded";
    }

    @PostMapping("/commit")
    public String commitBlocks(@RequestParam("fileName") String fileName,
                               @RequestParam("blockIds") List<String> blockIds) {
        log.info("---------Llamada al commit-------");
        BlockBlobClient blobClient = azureBlobStorageService.connectToStorage(a,b).getBlobClient(fileName).getBlockBlobClient();
        List<String> base64BlockIds = blockIds.stream()
                .map(id -> Base64.getEncoder().encodeToString(id.getBytes()))
                .collect(Collectors.toList());

        blobClient.commitBlockList(base64BlockIds);
        return "File uploaded successfully";
    }
    private ByteBuffer convertToByteBuffer(MultipartFile file) throws IOException {
        ByteBuffer byteBuffer;
        try (ReadableByteChannel channel = Channels.newChannel(file.getInputStream())) {
            byteBuffer = ByteBuffer.allocate((int) file.getSize());
            while (channel.read(byteBuffer) > 0) {
            }
            byteBuffer.flip();
        }
        return byteBuffer;
    }
}
