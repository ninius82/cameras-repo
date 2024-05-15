package it.serravalle.cameras_api.data.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TCAMERAS")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Camera implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	@Column(unique = false, nullable = true)
	private String tratta;
	@Column(unique = false, nullable = true)
	private String km;
	@Column(unique = false, nullable = true)
	private String direzione;
	@Column(unique = false, nullable = true)
	private Double lon;
	@Column(unique = false, nullable = true)
	private Double lat;
	@Column(unique = false, nullable = true)
	private String descrizione;
		
}
