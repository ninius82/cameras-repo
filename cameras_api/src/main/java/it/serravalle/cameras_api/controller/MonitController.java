package it.serravalle.cameras_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import it.serravalle.cameras_api.exception.CameraNotFoundException;

/*
All requests are received from the client and sent to the service for processing. 
*/
@RestController
@RequestMapping("/")
public class MonitController {

	@GetMapping(value = "monit")
	public ResponseEntity<String> getMonit() {
		try {
			return new ResponseEntity<String>(HttpStatus.OK);

		} catch (CameraNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service is down!", ex);
		}

	}
}
