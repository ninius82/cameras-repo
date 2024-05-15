package it.serravalle.cameras_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import it.serravalle.cameras_api.data.model.Camera;
import it.serravalle.cameras_api.service.CameraService;
import it.serravalle.cameras_api.exception.CameraNotFoundException;
import it.serravalle.cameras_api.exception.CameraSavingException;

/*
All requests are received from the client and sent to the service for processing. 
*/
@RestController
@RequestMapping({"/cameras","/oauth2/cameras"})
public class CameraController {

	@Autowired
	private CameraService cameraService;

	// Get a camera by its ID
	@GetMapping(value = "/{id}")
	public ResponseEntity<Camera> getCamera(@PathVariable("id") String id) {
		try {
			return new ResponseEntity<Camera>(cameraService.findById(id), HttpStatus.OK);
			
		} catch (CameraNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera Not Found", ex);
		}

	}

	@GetMapping(value = "/all")
	public ResponseEntity<List<Camera>> getAllCameras() {
		try {
			return new ResponseEntity<List<Camera>>(cameraService.findAll(), HttpStatus.OK);
			
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Coud not retrive the list", ex);
		}

	}

	@PostMapping(value = "/private/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Camera> addCamera(@RequestBody Camera newCamera) {

		try {
			return new ResponseEntity<Camera>(cameraService.save(newCamera), HttpStatus.CREATED);
		} catch (CameraSavingException ex) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@DeleteMapping(value = "/private/delete/{id}")
	public ResponseEntity<Camera> deleteCamera(@PathVariable("id") String id) {
		try {
			return new ResponseEntity<Camera>(cameraService.delete(id),HttpStatus.OK);
		} catch (CameraNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera Not Found", ex);
		}
	}

	// The product ID is the only required argument.
	@PutMapping(value = "/private/update/{id}")
	public ResponseEntity<Camera> updateCamera(@PathVariable String id,

			@RequestParam(required = false) String tratta,

			@RequestParam(required = false) String km,

			@RequestParam(required = false) String direzione,

			@RequestParam(required = false) Double lon, 
			
			@RequestParam(required = false) Double lat,
			
			@RequestParam(required = false) String descrizione

	) {
		try {
			return new ResponseEntity<Camera>(cameraService.update(id, tratta, km, direzione, lon, lat, descrizione),HttpStatus.OK);
		} catch (CameraNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera Not Found", ex);
		}

	}

}
