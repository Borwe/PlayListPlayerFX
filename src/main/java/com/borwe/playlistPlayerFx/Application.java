package com.borwe.playlistPlayerFx;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.borwe.playlistPlayerFx.fx.MainFXController;
import com.borwe.playlistPlayerFx.springConfigs.MainConfig;
import com.borwe.playlistPlayerFx.springServices.PlayListService;

import io.reactivex.schedulers.Schedulers;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Application extends javafx.application.Application{

	private static ApplicationContext context;
	
	private static Window parentWindow;
	
	public static void main(String[] args) {
		context= SpringApplication.run(MainConfig.class, args);
		launch(args);
		System.out.println("YOLO BITCHES");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("PlayListPlayerFX - A Media Playlist player");
		
		//load from mainDisplay as view
		Parent parent=FXMLLoader.load(this.getClass().getResource("/fxml/mainDisplay.fxml"));
		Scene scene=new Scene(parent);
		
		primaryStage.setScene(scene);
		
		parentWindow=primaryStage.getScene().getWindow();
		
		
		
		/*
		 * once shown now go ahead and start display help page if no playlists
		 * otherwise go ahead and populate the views with playlists
		 */
		primaryStage.setOnShown(event->{
			var playlistService=Application.getApplicationContext().getBean(PlayListService.class);
			System.out.println("FUCKING WHAT THE HELL?");
			playlistService.thereIsPlayList().map(val->{
				if(val==false) {
					Thread.sleep(1000);
					MainFXController.generateHelpDocsView();
				}
				return val;
			}).observeOn(Schedulers.computation())
				.subscribe(val->{
					System.out.println("SHIT:   "+val);
				},err->{
					System.err.println("ERROR: "+err);
				});
		});

		primaryStage.show();
	}

	
	public static ApplicationContext getApplicationContext() {
		return context;
	}
	
	public static Window getMainWindow() {
		return parentWindow;
	}
}
