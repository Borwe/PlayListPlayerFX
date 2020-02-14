package com.borwe.playlistPlayerFx.fx;

import java.util.concurrent.Callable;

import com.borwe.playlistPlayerFx.Application;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.internal.operators.observable.ObservableRefCount;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Used for doing things on the FX thread, instead of some
 * random thread
 * @author brian
 *
 */
@ToString
public class FXCompletableGenerator {
	
	@Getter @Setter
	private static ProgressBar loadingBar;
	
	@Getter @Setter
	private static Label loadingLabel;

	/**
	 * Used to run an FX runnable inside a the UI thread asyncornously.
	 * on the RX Computation scheduler
	 * @param runnable
	 * @return
	 */
	public static <T> Completable generateCompletable(String loadingText,Observable<T> observable) {
		return Completable.defer(()->
		//enable the loading string
		FXCompletableGenerator.setupLoading(loadingText)
		//do something with the passed in observable
		.map(val->{
			observable.subscribe();
			return val;
		})
		//finally update the UI side of things
		.map(val->{
			FXCompletableGenerator.stopLoading().subscribe();
			return val;
		}).toCompletable()).subscribeOn(Schedulers.computation());
	}

    public static void doOnUI(FXRunnable runnable){
    	try {
    		
			Platform.runLater(runnable);
    	}catch(Exception ex) {
    		
    	}
    }

    private static Single<Object> setupLoading(String textLoad){
        return Single.defer(()->Single.create(emitter->{
        	//get parent access to the main UI
        	Parent parent=Application.getParent();
        	//wait for the UI loading elements to load before going ahead
        	while(parent==null && parent.lookup("#loadBar")==null && parent.lookup("#loadBarText")==null) {
        		Thread.currentThread().sleep(300);
				parent=Application.getParent();
				if(parent==null) {
					System.out.println("TRYING TO GET parent");
				}
				if(parent.lookup("#loadBar")==null) {
					System.out.println("LOADBAR NULL STILL");
				}
				if(parent.lookup("#loadBarText")==null) {
					System.out.println("LOADBARTEXT NULL STILL");
				}
        	}
        	
        	loadingLabel=(Label) parent.lookup("#loadBarText");
        	loadingBar=(ProgressBar) parent.lookup("#loadBar");
        	
			FXRunnable runnable=()->{
				loadingLabel.setText(textLoad);
				loadingBar.setVisible(true);
				loadingBar.setMinWidth(100);
			};
			doOnUI(runnable);
			emitter.onSuccess(true);
		})).observeOn(Schedulers.computation()).subscribeOn(Schedulers.computation());
    }

    private static Single<Object> stopLoading(){
        return Single.defer(()->Single.create(emitter->{
            FXRunnable runnable=()->{
                loadingLabel.setText("Complete");
                loadingBar.setVisible(false);
                loadingBar.setMinWidth(0);
            };
            doOnUI(runnable);
            emitter.onSuccess(true);
        })).observeOn(Schedulers.computation()).subscribeOn(Schedulers.computation());
    }
} 
