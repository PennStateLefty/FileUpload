package com.microsoft.highqdemo.fileupload.controllers;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.microsoft.highqdemo.fileupload.storage.*;
import com.microsoft.highqdemo.fileupload.common.FileResponse;

@Controller
public class FileUploadController {

	private final StorageService storageService;

	@Autowired
	public FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {

		model.addAttribute("files", storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
						"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList()));

		return "uploadForm";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		storageService.store(file, -1);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/";
	}

	@PostMapping("/upload-file")
	@ResponseBody
	public FileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader(value = "Content-Range", required = false) String contentRange) {
		int currentChunk = -1;
		
		String fileName = file.getOriginalFilename();

		if (contentRange != null && !contentRange.isEmpty() && !contentRange.isBlank()) {
			//Trim "bytes " from the header
			currentChunk = calculateChunks(contentRange.substring(contentRange.indexOf(" ")));			
		}
		storageService.store(file, currentChunk);
		
		String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/").path(fileName).toUriString();

		return new FileResponse(fileName, url, file.getContentType(), file.getSize());
	}	

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

	//An hack method. Will return an integer representing the current chunk. Will return -1 if the chunk is the last
	private int calculateChunks(String contentRange) {		
		int totalSize = Integer.parseInt(contentRange.substring(contentRange.indexOf("/")+1));
		String[] byteRange = contentRange.substring(0, contentRange.indexOf("/")).split("-");

		int chunkSize = Integer.parseInt(byteRange[1].trim()) - Integer.parseInt(byteRange[0].trim());

		if (Integer.parseInt(byteRange[1]) == totalSize) {
			return -1;
		} else {
			return Integer.parseInt(byteRange[1]) / chunkSize;
		}
	}
}