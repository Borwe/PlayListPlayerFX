package com.borwe.playlistPlayerFx.springServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.borwe.playlistPlayerFx.data.Video;
import com.borwe.playlistPlayerFx.data.repos.VideoRepo;

import io.reactivex.Single;
import lombok.ToString;

@Service
@ToString
public class VideoService {

	@Autowired
	private VideoRepo vidRepo;
	
	public Single<Video> saveVideo(Video video) {
		return Single.defer(()->Single.just(video).map(v->{
			return vidRepo.save(v);
		}));
	}
}
