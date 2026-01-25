package it.serravalle.cameras.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;


import it.serravalle.cameras.model.Camera;

import reactor.core.publisher.Mono;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@EnableAsync
@RequestMapping("/info")
public class MetadataController {
	
	private static Logger logger = LogManager.getLogger(MetadataController.class);
	
    
    @Autowired
    private WebClient webClient;
			
//	@RequestMapping("/")
//	public String index() {
//		logger.info("Call index");
//		return "index";
//	}


	@GetMapping("/{camera}")
	@ResponseBody
	public Mono<Camera> getCameraMetadata(@PathVariable("camera") final String camera) 
			throws IOException {	
		try {
			logger.info("Call get api {}", camera);
			return  webClient
		            .get()
		            .uri("https://ede.serravalle.it:8443/api/oauth2/cameras/{camera}", camera)
		            .retrieve()
		        .bodyToMono(Camera.class);
		} catch (Exception e) {
			logger.error("Error {}", e.getMessage());
			// return null;
			return new ResponseEntity<Mono<Camera>>(HttpStatus.BAD_REQUEST).getBody();
		}

	}

	@GetMapping("/all")
	@ResponseBody
	public Mono<Camera> getAllCameraMetadata() {
		try {
			logger.info("Call get all cameras api");
			return webClient
					.get()
					.uri("https://ede.serravalle.it:8443/api/oauth2/cameras/all")
					.retrieve()
					.bodyToMono(Camera.class);
		} catch (Exception e) {
			logger.error("Error {}", e.getMessage());
			return new ResponseEntity<Mono<Camera>>(HttpStatus.BAD_REQUEST).getBody();
		}
	}
	
}
