package it.serravalle.cameras_api.service;

import java.util.List;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.serravalle.cameras_api.data.model.Camera;
import it.serravalle.cameras_api.data.repository.CameraRepository;
import it.serravalle.cameras_api.exception.CameraNotFoundException;
import it.serravalle.cameras_api.exception.CameraSavingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CameraServiceImpl implements CameraService {

	@Autowired
	private CameraRepository cameraRepository;

	@Transactional(readOnly = true)
	@Override
	public List<Camera> findAll() {
		log.info("Retrieving all cameras");
		return cameraRepository.findAll();
	}

	@Transactional(readOnly = true)
	@Override
	public Camera findById(String id) {
		log.info("Retrieving camera {}", id);
		return cameraRepository.findById(id)
				.orElseThrow(() -> {
					log.error("Camera {} not found", id);
					return new CameraNotFoundException(id);
				});
	}

	@Override
	@Transactional
	public Camera save(Camera camera) {
		log.info("Adding camera {} to the database", camera.getId());
		try {
			return cameraRepository.save(camera);
		} catch (Exception e) {
			log.error("Could not save the camera in the database: {}", e.getMessage());
			throw new CameraSavingException();
		}
	}

	@Override
	@Transactional
	public Camera delete(String id) {
		log.info("Deleting camera {}", id);
		Camera camera = findById(id);
		cameraRepository.delete(camera);
		return camera;
	}

	@Override
	@Transactional
	public Camera update(String id, String tratta, String km, String direzione, Double lon, Double lat, String descrizione) {
		log.info("Updating camera {}", id);

		Camera camera = findById(id);

		updateIfChanged(tratta, camera.getTratta(), camera::setTratta);
		updateIfChanged(km, camera.getKm(), camera::setKm);
		updateIfChanged(direzione, camera.getDirezione(), camera::setDirezione);
		updateIfChanged(descrizione, camera.getDescrizione(), camera::setDescrizione);

		if (isValidCoordinate(lon)) {
			camera.setLon(lon);
		}
		if (isValidCoordinate(lat)) {
			camera.setLat(lat);
		}

		cameraRepository.save(camera);
		log.info("Camera {} correctly updated", id);
		return camera;
	}

	private void updateIfChanged(String newValue, String currentValue, java.util.function.Consumer<String> setter) {
		if (hasText(newValue) && !Objects.equals(newValue, currentValue)) {
			setter.accept(newValue);
		}
	}

	private boolean hasText(String value) {
		return value != null && !value.isEmpty();
	}

	private boolean isValidCoordinate(Double value) {
		return value != null && value > 0;
	}

}
