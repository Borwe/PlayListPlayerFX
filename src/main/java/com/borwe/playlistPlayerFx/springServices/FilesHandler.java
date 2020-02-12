package com.borwe.playlistPlayerFx.springServices;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.schedulers.Schedulers;

@Service
public class FilesHandler {

	/**
	 * 
	 * @param location
	 * @return Observable<File> to show files in current passed <i>location</i>
			 * Check if location is a folder, 
			 * if not and is of MediaTypes stored in db, then emit it, else skip it and exit.
			 * otherwise if folder:
			 * 	get all files in folder, 
			 * 		if file is not a folder, emit it,if no files just exit this function
			 * 		else recall loop with folder passed as location keep going back 
	 */
	public Observable<File> getFilesInDirectory(String location){
        return Observable.create(emitter->{
            //check that file exists, if so continue, otherwise end it here
            File file=new File(location);
            if(file.exists()){
                fileEmmitting(emitter,file);
            }
            
            emitter.onComplete();
        });
	}

    
    /**
     * Check that file exists:
     *  -If So, then check if it's a folder:
     *      -If so then get it's children
     *      -Recursivly call this same function on it's children
     *  -If so, but not folder:
     *      -Emit files using emmitter
     *  -If not, then do nothing
     */
    private void fileEmmitting(Emitter emitter,File file){
        if(file!=null && file.exists()==true){
            //check if folder
            if(file.isDirectory()==true){
               //get children
                Observable.fromArray(file.listFiles()).subscribeOn(Schedulers.io())
                    .blockingSubscribe(f->{
                        if(f.isDirectory()){
                            fileEmmitting(emitter,f);
                        }else if(f!=null && f.isFile()){
                            emitter.onNext(f);    
                        }
                    });
            }else if(file.isFile()){
                emitter.onNext(file);
            }
        }
    }
}
