/**
 * 
 */
package it.serravalle.cameras.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 */
@Data
@NoArgsConstructor
public class Camera implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String tratta;

	private String km;

	private String direzione;

	private Double lon;

	private Double lat;

	private String descrizione;

}
