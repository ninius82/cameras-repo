package it.serravalle.cameras_api.service;

import java.util.List;

import it.serravalle.cameras_api.data.model.Camera;

public interface CameraService {

	    List<Camera> findAll();

	    Camera findById(String id);

	    Camera delete(String id);

	    Camera update(String  id, String tratta, String km, String direzione, Double lon, Double lat, String descrizione);

		Camera save(Camera camera);
}
