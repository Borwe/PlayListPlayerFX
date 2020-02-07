package com.borwe.playlistPlayerFx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelpView {
	
	public void start(Stage primaryStage) throws Exception {
		
		Parent parent=FXMLLoader.load(this.getClass().getResource("/fxml/helpDisplay.fxml"));
		
		Scene scene=new Scene(parent);
		primaryStage.setScene(scene);
		
		//show the help section
		primaryStage.showAndWait();
	}

}
