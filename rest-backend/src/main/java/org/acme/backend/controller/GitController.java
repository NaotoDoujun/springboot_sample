package org.acme.backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.acme.backend.service.GitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class GitController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitController.class);

    @Autowired
    GitService gitService;
    
    @GetMapping("/git")
    public ResponseEntity<List<String>> getAllFiles() {
        try {
            List<String> files = gitService.findAll();
            LOGGER.info("files list: {}", files);
            return new ResponseEntity<>(files, HttpStatus.OK);
        }catch(Exception e) {
            LOGGER.error(e.toString());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/git", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> handleFileUpload(@RequestParam("files") MultipartFile[] files) {
        try{
            gitService.store(files);
            return ResponseEntity.ok().build();
        }catch(Exception e) {
            LOGGER.error(e.toString());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/git/commit")
    public ResponseEntity<Resource> handleCommit(@RequestParam("commit") String message) {
        try {
            gitService.commit(message);
            return ResponseEntity.ok().build();
        }catch(Exception e) {
            LOGGER.error(e.toString());
            return ResponseEntity.internalServerError().build();
        }
    }
}
