package com.borwe.playlistPlayerFx.fx.functions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.borwe.playlistPlayerFx.Application;
import com.borwe.playlistPlayerFx.Observers.DoNothingObserver;
import com.borwe.playlistPlayerFx.data.MultiMedia;
import com.borwe.playlistPlayerFx.data.PlayList;
import com.borwe.playlistPlayerFx.data.Video;
import com.borwe.playlistPlayerFx.springServices.FilesHandler;
import com.borwe.playlistPlayerFx.springServices.MultiMediaService;
import com.borwe.playlistPlayerFx.springServices.PlayListService;
import com.borwe.playlistPlayerFx.springServices.TypeService;
import com.borwe.playlistPlayerFx.springServices.VideoService;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.SingleSource;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Window;

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
	public void accept(T t) {
		DirectoryChooser fileChooser=new DirectoryChooser();
        //get types to be allowed into file chooser
        var typesListObserver= Application.getApplicationContext().getBean(TypeService.class)
            .getAlltypes().map(type->type.getType().toLowerCase());

        //open file chooser and listen for a returning
        fileChooser.setTitle("Select a folder containing files to play");
        File file=fileChooser.showDialog(t);

        //check that file exists, otherwise user didn't select any
        if(file!=null && file.exists() && file.isDirectory()){
            //continue here to parse files in the folder
            var filesObserver=Application.getApplicationContext().getBean(FilesHandler.class)
                .getFilesInDirectory(file.getAbsolutePath());
            
            //check if files is empty, if so stop and don't go ahead, show error message
            if(filesObserver.isEmpty().blockingGet()) {
            	ButtonType okButton=new ButtonType("OK");
            	Dialog<Void> noFilesDialog=new Dialog<Void>();
            	noFilesDialog.getDialogPane().getButtonTypes().add(okButton);
            	noFilesDialog.setTitle("Error");
            	noFilesDialog.getDialogPane().setHeaderText("Sorry");
            	noFilesDialog.getDialogPane().setContentText("Please select another folder with actual files");
            	noFilesDialog.initModality(Modality.APPLICATION_MODAL);
            	noFilesDialog.showAndWait();
            	accept(t);
            	return;
            }else {
            	//meaning that files actually exist, then go ahead and filter only
            	//files that have a specific extention ending that the user has
            	//listed for support
            	filesObserver.filter(f->{
            		try {
            			
						String extention=f.getName().toLowerCase().replace(".", " ").split(" ")[1];
						if(typesListObserver.contains(extention).blockingGet()) {
							return true;
						}else {
							return false;
						}
            		}catch (Exception e) {
            			e.printStackTrace();
            			return false;
					}
            	})
            	.flatMap(f->{
            		//now map the files to MultiMedia objects, but first create 
            		//Video objects that are linked to MultiMedia Objects
            		//then afterwards return the MultiMedia objects
            		MultiMedia media=Application.getApplicationContext().getBean(MultiMedia.class);
            		Video video=Application.getApplicationContext().getBean(Video.class);
            		media.setName(f.getName());

            		
            		//setup the video
            		video.setMultiMedia(media);
            		video.setLocation(f.getAbsolutePath());
            		video.setSeek(0L);
            		video.setTime(-1L);
            		/*var typ=*/Application.getApplicationContext()
            				.getBean(TypeService.class).getAlltypes()
            				.filter(type->{

            			
            			try {
            				
							String extention=f.getName().toLowerCase().replace(".", " ").split(" ")[1];
							if(type.getType().equals(extention)) {
								return true;
							}
            			}catch(Exception ex) {
            				ex.printStackTrace();
            				return false;
            			}
						return false;
            		}).toList().subscribe(typs->{
						video.setType(typs.get(0));
            		},error->System.err.println("ERRORTYPE: "+error.getCause()+"\n"+error.getMessage()));
            		
            		//now save the video
            		
            		Application.getApplicationContext().getBean(MultiMediaService.class)
            			.save(video.getMultiMedia()).subscribe(System.out::println,err->{
            				System.err.println("ERROR: "+err.getCause()+"\n"+err.getMessage());
            			});
            		return Application.getApplicationContext().getBean(VideoService.class)
            				.saveVideo(video).toObservable();
            	}).doOnError(error->System.err.println("FUCKING: "+error.getLocalizedMessage())).toList().map(vids->{
            				
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
            	}).doOnError(error->System.err.println("SHITTYERROR: "+error.getLocalizedMessage()))
            	.observeOn(Schedulers.io()).subscribe(playlist->{
            		//prompt user to give playlist a name if playlist not null
            		if(playlist!=null) {
            			Platform.runLater(()->{
							TextInputDialog dialog=new TextInputDialog();
							dialog.setHeaderText("Input");
							dialog.setContentText("Enter name of the playlist");
							dialog.initModality(Modality.APPLICATION_MODAL);
							dialog.showAndWait();
							
							String result=dialog.getResult();
							//check that it's not null, and that it exists, then set the value of playlist 
							//and save it asyncronously
							if(result!=null && result.isEmpty()==false) {
								Application.getApplicationContext().getBean(PlayListService.class)
									.savePlayList(playlist).subscribeOn(Schedulers.io()).subscribe(pl->{
										System.out.println("DONE SAVING PLAY LIST");
									});
							}
            			});
            		}else {
            			//some shit went wrong
            			System.out.println("WHAT THE FUCK? COME ON!!!!!!!!!!!!!");
            		}
            	},error->{
            		System.err.println("ERROR: "+error.getCause());
            		System.err.println("ERROR SUBSCRIBE: "+error.getLocalizedMessage());
            	});
            }
        }else{
            //meaning user didn't select anything here
            System.out.println("Wow, you didn't select shit!'");
        }
    }
}
