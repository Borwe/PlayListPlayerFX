package com.borwe.playlistPlayerFx.springServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.borwe.playlistPlayerFx.data.PlayList;
import com.borwe.playlistPlayerFx.data.repos.PlayListRepo;

import io.reactivex.Observable;
import lombok.ToString;

@Service
@ToString
public class PlayListService {

	@Autowired
	PlayListRepo playListRepo;
	
	public Observable<PlayList> getPlayLists(){
		return Observable.fromIterable(playListRepo.findAll());
	}
	
	public io.reactivex.Single<Boolean> thereIsPlayList(){
		return getPlayLists().count().map(count->{
			return (count>0)?false:true;
		});
	}
}
