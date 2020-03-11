package com.borwe.playlistPlayerFx;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TypesView {
	
	public void start(Stage primaryStage) throws IOException {
		
		//load the fxml file
		Parent parent=FXMLLoader.load(this.getClass().getResource("/fxml/typesDisplay.fxml"));
		
		Scene scene=new Scene(parent);
		primaryStage.setScene(scene);
		primaryStage.setMinWidth(300);
		primaryStage.setMinHeight(300);
		primaryStage.setMaxWidth(350);
		primaryStage.setMaxHeight(350);

		primaryStage.showAndWait();
	}
}
