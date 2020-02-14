package com.borwe.playlistPlayerFx.data.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.borwe.playlistPlayerFx.data.Video;

public interface VideoRepo extends CrudRepository<Video, Long>{

}
