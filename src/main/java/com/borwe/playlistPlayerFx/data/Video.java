package com.borwe.playlistPlayerFx.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Video {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/** 
	 * Video can have a type, but not a must
	 */
	@OneToOne(optional = true)
	private Type type;
	
	/**
	 * Each video is a type of multimedia
	 */
	@OneToOne(optional = true)
	private MultiMedia multiMedia;
	
	/**
	 * Hold location of the file
	 */
	@Column
	private String location;
	
	/**
	 * Mark location of the video last stopped at
	 */
	@Column
	private  Long seek;
	
	/**
	 * Mark the day/time the video was last played
	 */
	private Long time;
}
