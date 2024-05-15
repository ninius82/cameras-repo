package it.serravalle.cameras_api.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.serravalle.cameras_api.data.model.Camera;

public interface CameraRepository extends JpaRepository<Camera, String> { 

	 
}
