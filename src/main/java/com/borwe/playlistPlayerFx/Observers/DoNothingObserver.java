package com.borwe.playlistPlayerFx.Observers;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class DoNothingObserver<T> implements Observer<T>{@Override
	public void onSubscribe(Disposable d) {
		log.debug("Subscribing");
	}

	@Override
	public void onNext(T t) {
        System.out.println("OnNext: "+t);
		log.debug("OnNext: "+t);
	}

	@Override
	public void onError(Throwable e) {
		log.debug("OnError: "+e.getMessage());
	}

	@Override
	public void onComplete() {
		log.debug("Completed yeah!!! ");
	}

}
