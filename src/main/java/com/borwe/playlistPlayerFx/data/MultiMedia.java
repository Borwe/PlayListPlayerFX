package com.borwe.playlistPlayerFx.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author brian
 *	For handling video types, names/ etc for direct playlist access
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MultiMedia {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/**
	 * Mark name of the multimedia file
	 */
	@Column
	private String name;
}
