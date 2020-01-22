package com.borwe.playlistPlayerFx;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.borwe.playlistPlayerFx.springConfigs.MainConfig;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Application extends javafx.application.Application{

	private static ApplicationContext context;
	
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
		
		primaryStage.show();
	}
}
