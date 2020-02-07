package com.borwe.playlistPlayerFx.fx;

import java.net.URL;
import java.util.ResourceBundle;

import com.borwe.playlistPlayerFx.Application;
import com.borwe.playlistPlayerFx.springServices.TypeService;

import io.reactivex.Single;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class TypesViewController implements Initializable{

	private boolean loaded=false;
	
	private Single<ListView<String>> fillInListViewObservable;
	
	@FXML
	ListView<String> typesList;
	
	@FXML
	Button exitTypesView;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loaded=true;
		
		fillInListViewObservable=Application.getApplicationContext().getBean(TypeService.class)
			.getAlltypes().map(type->{
				System.out.println("TYPE: "+type);
				return type.getType();
			}).toList().map(list->{
				while(typesList==null) {
					Thread.sleep(500);
				}
				typesList.getItems().addAll(list);
				return typesList;
			});
		
		fillInListViewObservable.subscribe();
	}

	public void exitTypes(ActionEvent event) {
		((Stage)exitTypesView.getScene().getWindow()).close();
	}
}
