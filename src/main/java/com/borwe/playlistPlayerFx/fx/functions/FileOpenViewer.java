package com.borwe.playlistPlayerFx.fx.functions;

import java.io.File;
import java.util.function.Consumer;

import com.borwe.playlistPlayerFx.Application;
import com.borwe.playlistPlayerFx.springServices.FilesHandler;
import com.borwe.playlistPlayerFx.springServices.TypeService;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
            .getAlltypes().map(type->{
                return type.getType();
            }).toList();

        //open file chooser and listen for a returning
        fileChooser.setTitle("Select a folder containing files to play");
        File file=fileChooser.showDialog(t);

        //check that file exists, otherwise user didn't select any
        if(file!=null && file.exists() && file.isDirectory()){
            //continue here to parse files in the folder
            Application.getApplicationContext().getBean(FilesHandler.class)
                .getFilesInDirectory(file.getAbsolutePath())
                .forEach(f->{
                    System.out.println("FILE : "+f.getAbsolutePath());
                });
        }else{
            //meaning user didn't select anything here
            System.out.println("Wow, you didn't select shit!'");
        }
    }
}
