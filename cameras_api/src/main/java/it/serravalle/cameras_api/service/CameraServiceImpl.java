package it.serravalle.cameras_api.service;

import java.util.List;

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
		try {
			return cameraRepository.findById(id).get();
		} catch (Exception e) {
			log.error("Camera {} not found", id);
			throw new CameraNotFoundException(id);
			// TODO: handle exception
		}

	}

	@Override
	@Transactional
	public Camera save(Camera camera) {
		log.info("Adding camera {} to the database", camera.getId());
		try {
			return cameraRepository.save(camera);
		} catch (Exception e) {
			log.error("Could not save the camera in the database");
			throw new CameraSavingException();
			// TODO: handle exception
		}

	}

	/*
	 * To update the value a property: - validate that the new value is not null nor
	 * empty. - validate that the new value is not the same as the old value to be
	 * replaced. - If the values are the same, skip the operation.
	 */
	@Override
	@Transactional
	public Camera delete(String id) {
		log.info("Deleting camera {}", id);
		try {
			Camera camera = findById(id);
			cameraRepository.delete(camera);
			return camera;
		} catch (Exception e) {
			log.error("Camera {} not found", id);
			throw new CameraNotFoundException(id);
			// TODO: handle exception
		}
	}

	@Override
	@Transactional
	public Camera update(String id, String tratta, String km, String direzione, Double lon, Double lat, String descrizione) {
		log.info("Updateing camera {}", id);
		Camera camera = null;

		try {
			
			camera = findById(id);
			
			boolean emptyTratta = tratta == null || tratta.length() < 1;
			
			boolean emptyKm = km == null || km.length() < 1;
			
			boolean emptyDirezione = direzione == null || direzione.length() < 1;
			
			boolean validLon = lon != null && (lon.compareTo((double) 0) > 0);
			
			boolean validLat = lat != null && (lat.compareTo((double) 0) > 0);
			
			boolean emptyDescrizione = descrizione == null || descrizione.length() < 1;
			

			if (!emptyTratta && !camera.getTratta().equals(tratta)) {

				camera.setTratta(tratta);

			}

			if (!emptyKm && !camera.getKm().equals(km)) {
				camera.setKm(km);
			}

			if (!emptyDirezione && !camera.getDirezione().equals(direzione)) {
				camera.setDirezione(direzione);
			}
			
			if (!emptyDescrizione && !camera.getDescrizione().equals(descrizione)) {
				camera.setDescrizione(descrizione);
			}
			
			if (validLon) {
				camera.setLon(lon);
			}

			if (validLat) {
				camera.setLat(lat);
			}
			
			cameraRepository.save(camera);
			log.info("Camera {} correctly updated", id);

		} catch (Exception e) {
			throw new CameraNotFoundException(id);
			// TODO: handle exception
		}

		return camera;

	}

}
