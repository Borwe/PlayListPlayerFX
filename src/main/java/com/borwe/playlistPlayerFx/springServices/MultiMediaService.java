package com.borwe.playlistPlayerFx.springServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.borwe.playlistPlayerFx.data.MultiMedia;
import com.borwe.playlistPlayerFx.data.repos.MultiMediaRepo;

import io.reactivex.Single;
import lombok.ToString;

@Service
@ToString
public class MultiMediaService {

	@Autowired
	private MultiMediaRepo multiRepo;
	
	public Single<MultiMedia> save(MultiMedia media){
		return Single.defer(()->Single.just(media).map(m->multiRepo.save(m)));
	}
}
