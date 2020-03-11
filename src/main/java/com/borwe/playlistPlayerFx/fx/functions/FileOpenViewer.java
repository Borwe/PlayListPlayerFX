package com.borwe.playlistPlayerFx.fx.functions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import com.borwe.playlistPlayerFx.Application;
import com.borwe.playlistPlayerFx.data.MultiMedia;
import com.borwe.playlistPlayerFx.data.PlayList;
import com.borwe.playlistPlayerFx.data.Video;
import com.borwe.playlistPlayerFx.fx.FXCompletableGenerator;
import com.borwe.playlistPlayerFx.fx.FXRunnable;
import com.borwe.playlistPlayerFx.springServices.FilesHandler;
import com.borwe.playlistPlayerFx.springServices.MultiMediaService;
import com.borwe.playlistPlayerFx.springServices.PlayListService;
import com.borwe.playlistPlayerFx.springServices.TypeService;
import com.borwe.playlistPlayerFx.springServices.VideoService;
import com.borwe.playlistPlayerFx.thirdobjects.MuttableString;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Window;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class FileOpenViewer<T extends Window> implements Consumer<T>{

    /**
     * Open a file chooser:
     * - When user clicks exit/cancel exit it.
     * - When user clicks select folder, go ahead
     * - Check if the folder has any files:
     *   - If empty exit, prompting the user of the error
     * - Check if the files in the folder are of a specific Types
     *   - If none, close window, show prompt informing user to add types
     * - If so, then create Multimedia and associated Video types putting
     *   video types into Playlist, then saving them all recursivly as you put
     *   each.
     * - And then populate the UI with this information for playlist
     *   and Multimedia section after exiting this dialog
     */
	@Override
	public void accept(T window) {
		
		getFolderSelected(window).subscribeOn(Schedulers.computation())
			//filter to make on selected valid folders
			.filter(file->{
				if(file==null || file.exists()==false) {
					System.out.println("You didn't select a file");
					return false;
				}
				
				//if empty return false, but also display warning that reloops
				boolean empty=Application.getApplicationContext().getBean(FilesHandler.class)
					.getFilesInDirectory(file.getAbsolutePath()).isEmpty().blockingGet();
				if(empty==true) {
					FXRunnable runner=()->{
						
						System.out.println("SHIT COME ON\n\n\n");
						ButtonType okButton=new ButtonType("OK");
						Dialog<Void> noFilesDialog=new Dialog<Void>();
						noFilesDialog.getDialogPane().getButtonTypes().add(okButton);
						noFilesDialog.setTitle("Error");
						noFilesDialog.getDialogPane().setHeaderText("Sorry");
						noFilesDialog.getDialogPane().setContentText("Please select another folder witch isn't empty");
						noFilesDialog.initModality(Modality.APPLICATION_MODAL);
						noFilesDialog.showAndWait();
						accept(window);
					};
					FXCompletableGenerator.doOnUI(runner);
					return false;
				}
				return true;
			}).flatMapSingle(folder->{
            	//meaning that files actually exist, then go ahead and filter only
            	//files that have a specific extention ending that the user has
            	//listed for support
				return Application.getApplicationContext().getBean(FilesHandler.class)
					.getFilesInDirectory(folder.getAbsolutePath())
					.filter(
						//check if has extention or not
						file->Application.getApplicationContext()
							.getBean(TypeService.class)
							.checkIfFileContainsUserType(file).blockingGet()
					)
					.flatMapSingle(file->{
						//we reach here if files exist that have an extention
						//now map the files to MultiMedia objects, but first create 
						//Video objects that are linked to MultiMedia Objects
						//then afterwards return the MultiMedia objects
						MultiMedia media=Application.getApplicationContext().getBean(MultiMedia.class);
						Video video=Application.getApplicationContext().getBean(Video.class);
						media.setName(file.getName());

	            		//setup the video
	            		video.setMultiMedia(media);
	            		video.setLocation(file.getAbsolutePath());
	            		video.setSeek(0L);
	            		video.setTime(-1L);
	            		video.setType(Application.getApplicationContext()
	            				.getBean(TypeService.class).getTypeFromFile(file).blockingGet());
	            		
	            		//save the video
	            		Application.getApplicationContext().getBean(MultiMediaService.class)
	            			.save(video.getMultiMedia()).subscribe(System.out::println,System.err::println);
	            		
	            		return Application.getApplicationContext().getBean(VideoService.class)
	            				.saveVideo(video);
					}).toList().map(vids->{
	            		//map video to playlist
	            		PlayList playlist=Application.getApplicationContext().getBean(PlayList.class);
	            		playlist.setLocation(new File(vids.get(0).getLocation()).getParentFile().getAbsolutePath());
	            		playlist.setTime(System.currentTimeMillis());
	            		System.out.println("DEVELOPING SHIT");
	            		
	            		List<MultiMedia> medias=new ArrayList<MultiMedia>();
	            		playlist.setMarkMultiMediaAccess(medias);
	            		
	            		vids.forEach(v->{
	            			playlist.getMarkMultiMediaAccess().add(v.getMultiMedia());
	            		});
	            		return playlist;
					});
			}).observeOn(Schedulers.io())
			.subscribe(playlist->{
				//prompt user to give playlist a name if playlist not null
				promptForPlaylistName(playlist);
			},error->{
				System.err.println("ERROR: "+error.getCause());
				System.err.println("ERROR SUBSCRIBE: "+error.getLocalizedMessage());
			   });
           
    }
	
	/**
	 * Used for saving playlist with a name, will keep prompting user untill they
	 * select a playlist that is valid
	 * @param playlist
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void promptForPlaylistName(PlayList playlist) throws InterruptedException, ExecutionException {
	
			if(playlist!=null) {
				ReentrantLock lock=new ReentrantLock();
				MuttableString result=new MuttableString();
				
				FXRunnable runnable=()->{
					TextInputDialog dialog=new TextInputDialog();
					dialog.setHeaderText("Input");
					dialog.setContentText("Enter name of the playlist");
					dialog.initModality(Modality.APPLICATION_MODAL);
					dialog.showAndWait();
					result.setString(dialog.getResult());
				};
				
				FXCompletableGenerator.doOnUI(runnable, lock);
				
				//wait for user to select something before proceeding here
				lock.lock();
				//check that it's not null, and that it exists, then set the value of playlist 
				//and save it asyncronously
				if(!result.isNull() && result.isEmpty()==false) {
					playlist.setName(result.getString());
					Application.getApplicationContext().getBean(PlayListService.class)
						.savePlayList(playlist).subscribeOn(Schedulers.io()).subscribe(pl->{
							System.out.println("DONE SAVING PLAY LIST");
						});
				}
			}
	}
	
	private Single<File> getFolderSelected(T window){
		return Single.defer(()->Single.just(window)
			.map(w->{
				ReentrantLock lock=new ReentrantLock(true);
				
				DirectoryChooser fileChooser=new DirectoryChooser();
				MuteableFile file=new MuteableFile(null);
				FXRunnable showFileUI=()->{
				fileChooser.setTitle("Select a folder containing files to play");
				
				file.setFile(fileChooser.showDialog(window));
				};
				FXCompletableGenerator.doOnUI(showFileUI, lock);
				
				lock.lock();
				
				//open file chooser and listen for a returning
				return file.getFile();
			}
		));
	}
	
	/**
	 * 
	 * @author BRIAN
	 * Used for dynamically setting a file object
	 * , good for when using FileChooser/DirectoryChooser.showDialog() in fx
	 */
	@AllArgsConstructor
	@ToString
	private class MuteableFile{
		@Getter @Setter
		private File file;
	}
}
