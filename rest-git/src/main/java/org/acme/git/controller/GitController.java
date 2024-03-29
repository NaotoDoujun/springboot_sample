package org.acme.git.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.acme.git.storage.StorageFileNotFoundException;
import org.acme.git.storage.StorageService;

@RestController
@RequestMapping("/api")
public class GitController {

	private final StorageService storageService;

	@Autowired
	public GitController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/git")
	public List<String> listUploadedFiles(Model model) throws IOException {
		return storageService.loadAll().map(path -> path.getFileName().toString()).toList();
	}

	@GetMapping("/git/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		Resource file = storageService.loadAsResource(filename);
		if (file == null)
			return ResponseEntity.notFound().build();
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@PostMapping("/git")
	public ResponseEntity<Resource> handleFileUpload(@RequestParam("files") MultipartFile[] files) {
		try{
			Arrays.asList(files).stream().forEach(file -> {
				storageService.store(file);
			  });
			return ResponseEntity.ok().build();
		}catch(Exception e){
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/git/commit")
	public ResponseEntity<Resource> handleCommit(@RequestParam("commit") String message) {
		try{
			storageService.commit(message);
			return ResponseEntity.ok().build();
		}catch(Exception e){
			return ResponseEntity.internalServerError().build();
		}
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
