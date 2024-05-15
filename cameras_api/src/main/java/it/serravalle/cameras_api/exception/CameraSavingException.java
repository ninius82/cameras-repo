package it.serravalle.cameras_api.exception;

public class CameraSavingException extends RuntimeException {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	public CameraSavingException() {
		super("Could not save the camera in the database, check for errors in data");
	}
}
