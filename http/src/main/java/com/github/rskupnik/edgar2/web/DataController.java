package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.domain.DataEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    private final Edgar edgar;

    @Autowired
    public DataController(Edgar edgar) {
        this.edgar = edgar;
    }

    @PostMapping("data/{deviceId}")
    public ResponseEntity<Void> ingestImageData(@PathVariable String deviceId, @RequestParam String id, @RequestParam MultipartFile contents) {
        try {
            return edgar.addData(deviceId, id, contents.getBytes()) ?
                    ResponseEntity.ok().build() :
                    ResponseEntity.notFound().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(503).build();
        }
    }

    @GetMapping("data/{deviceId}")
    public ResponseEntity<?> getImage(@PathVariable String deviceId, @RequestParam String id) {
        var device = edgar.getDevices().stream()
                .filter(d -> d.getId().equals(deviceId))
                .findFirst();

        if (device.isPresent()) {
            var dataEntry = device.get().getData().stream()
                    .filter(d -> d.getId().equals(id))
                    .findFirst();

            if (dataEntry.isPresent()) {
                var headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
                return new ResponseEntity<>(dataEntry.get().getContents(), headers, HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
