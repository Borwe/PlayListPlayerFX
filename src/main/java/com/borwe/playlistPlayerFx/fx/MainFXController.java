package com.borwe.playlistPlayerFx.fx;

import java.net.URL;
import java.util.ResourceBundle;

import com.borwe.playlistPlayerFx.Application;
import com.borwe.playlistPlayerFx.HelpView;
import com.borwe.playlistPlayerFx.Observers.DoNothingObserver;
import com.borwe.playlistPlayerFx.springServices.PlayListService;

import io.reactivex.schedulers.Schedulers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainFXController implements Initializable{

	@FXML
	MenuItem menu_exit;
	
	@FXML
	MenuItem menu_help;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		var playlistService=Application.getApplicationContext().getBean(PlayListService.class);
		playlistService.thereIsPlayList().map(val->{
			if(val==false) {
				Thread.sleep(1000);
				generateHelpDocsView();
			}
			return val;
		}).observeOn(Schedulers.computation())
			.subscribe(val->{
				System.out.println("SHIT:   "+val);
			},err->{
				System.err.println("ERROR: "+err);
			});
	}
	
	private void generateHelpDocsView() throws Exception {
		
		Stage stage=new Stage();
		stage.initOwner(Application.getMainWindow());
		stage.initModality(Modality.APPLICATION_MODAL);
		HelpView help=new HelpView();
		help.start(stage);
		System.out.println("SHIT!!!");
	}
	
	public void showHelpDocs(ActionEvent event) throws Exception {
		generateHelpDocsView();
	}
	
	//close the all application
	public void closeAppliction() {
		System.exit(0);
	}
}
