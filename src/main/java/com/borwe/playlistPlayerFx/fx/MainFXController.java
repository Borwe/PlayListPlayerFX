package com.borwe.playlistPlayerFx.fx;

import java.net.URL;
import java.util.ResourceBundle;

import com.borwe.playlistPlayerFx.Application;
import com.borwe.playlistPlayerFx.HelpView;
import com.borwe.playlistPlayerFx.Observers.DoNothingObserver;
import com.borwe.playlistPlayerFx.fx.functions.FileOpenViewer;
import com.borwe.playlistPlayerFx.fx.functions.GenerateHelperView;
import com.borwe.playlistPlayerFx.fx.functions.GenerateTypesView;
import com.borwe.playlistPlayerFx.springServices.PlayListService;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import lombok.Getter;
import lombok.Setter;

public class MainFXController implements Initializable{
	
	@FXML
	MenuBar menuBar;

	@FXML
	MenuItem menu_exit;
	
	@FXML
	MenuItem menu_help;
	
	@FXML
    @Getter
	private static Label loadBarText;
	
	@FXML 
    @Getter
	private static ProgressBar loadBar;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	public void showHelpDocs(ActionEvent event) throws Exception {
        new GenerateHelperView<ActionEvent>().accept(event);
	}
	
	public void showTypesView(ActionEvent event) {
		new GenerateTypesView<ActionEvent>().accept(event);
	}

    public void selectFile(ActionEvent event){
        new FileOpenViewer().accept(Application.getMainWindow());
    }
	
	//close the all application
	public void closeAppliction() {
		System.exit(0);
	}
}
