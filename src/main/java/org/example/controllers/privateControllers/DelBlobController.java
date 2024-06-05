package org.example.controllers.privateControllers;

import lombok.RequiredArgsConstructor;
import org.example.service.impl.DelBlobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blob")
@CrossOrigin
public class DelBlobController {
    private static final Logger log = LoggerFactory.getLogger(DelBlobController.class);
    private DelBlobService deleteBlobService;
    public DelBlobController(DelBlobService deleteBlobService) {
        this.deleteBlobService = deleteBlobService;
    }

    @DeleteMapping
    public ResponseEntity<String> deleteBlobIfExists(@RequestParam String fileName) {
        log.info("Filename: " + fileName);
        return deleteBlobService.deleteBlobByName(fileName);
    }
}
