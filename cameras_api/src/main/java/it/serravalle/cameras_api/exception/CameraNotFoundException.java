package it.serravalle.cameras_api.exception;

public class CameraNotFoundException extends RuntimeException {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CameraNotFoundException(String id) {
		    super("Could not find camera " + id);
		  }
}
