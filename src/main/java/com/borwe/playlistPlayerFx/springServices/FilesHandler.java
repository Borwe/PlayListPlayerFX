package com.borwe.playlistPlayerFx.springServices;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

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
		return Observable.fromArray(location).map(loc->{
			return new File(loc);
		})
		.filter(file->(file.exists())?true:false)
		.flatMap(file->{
			Callable<ObservableSource<File>> recursive;
			recursive=()->{
				
				if(file.isDirectory()==false && file.isFile()==true) {
					//if file then so return it
					return (emit->{
						emit.onNext(file);
						emit.onComplete();
					});
				}else if(file.isDirectory()==false){
					//we reach here if the file is a directory;
					//open the folder
					return (emit->{
						Observable.fromArray(file.listFiles()).map(f->getFilesInDirectory(f.getAbsolutePath()))
						.forEach(observables->{
							observables.forEach(f->{
								emit.onNext(f);
							});
						});
						emit.onComplete();
					});
				}else {
					return (emit->{
						emit.onComplete();
					});
				}
			};
			//use executer to execute this
			ExecutorService service=Executors.newSingleThreadExecutor();
			var fut=service.submit(recursive);
			return fut.get();
		});
	}
}
