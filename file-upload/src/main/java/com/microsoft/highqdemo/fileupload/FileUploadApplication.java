package com.microsoft.highqdemo.fileupload;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.microsoft.highqdemo.fileupload.storage.*;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class FileUploadApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(FileUploadApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
}
