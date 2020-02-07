package com.borwe.playlistPlayerFx.Observerables;


import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class OnFXLoadedObservable<T>{

	private Observable<T> observable;
	
	public Observable<T> generateObervable(ObservableOnSubscribe<T> obsEmitter){
		observable=Observable.create(obsEmitter);
		return observable;
	}
}
