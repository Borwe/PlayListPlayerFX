package com.borwe.playlistPlayerFx.data;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlayList {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/**
	 * Get name of the playlist
	 */
	@Column
	private String name;
	
	/**
	 * Hold the location of the folder containing the playlist
	 */
	@Column
	private String location;
	
	/**
	 * Access to the files inside the directory that are multimedia files
	 */
	@OneToMany
	private List<MultiMedia> markMultiMediaAccess;
	
	/**
	 * Mark time video was last played
	 */
	private Long time;
}
