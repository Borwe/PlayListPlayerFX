package com.borwe.playlistPlayerFx.fx;

import java.net.URL;
import java.util.ResourceBundle;

import com.borwe.playlistPlayerFx.Application;
import com.borwe.playlistPlayerFx.data.Type;
import com.borwe.playlistPlayerFx.springServices.TypeService;

import io.reactivex.Observable;
import io.reactivex.Single;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class TypesViewController implements Initializable{

	private boolean loaded=false;
	
	private Single<ListView<String>> fillInListViewObservable;
	
	@FXML
	ListView<String> typesList;
	
	@FXML
	Button exitTypesView;
	
	@FXML
	Button removeType;
	
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
				typesList.refresh();
				return typesList;
			});
				
		fillInListViewObservable.map(typeList->{
				//set listener to enable disable removeType button
				var removeEnabler=new EventHandler<Event>() {

					@Override
					public void handle(Event arg0) {
						// TODO Auto-generated method stub
						var selection=typesList.getSelectionModel();
						if(selection.isEmpty()==true) {
							removeType.setDisable(true);
						}else {
							removeType.setDisable(false);
						}
					}
				};
				typesList.setOnMouseClicked(removeEnabler);
				typesList.setOnKeyPressed(removeEnabler);
				return typesList;
		}).subscribe();
	}

	public void exitTypes(ActionEvent event) {
		((Stage)exitTypesView.getScene().getWindow()).close();
	}
	
	public void removeTypeItem(ActionEvent event) {
		//get the type which was selected
		var getType=Single.create(emitter->{
			emitter.onSuccess(typesList.getSelectionModel().getSelectedItem());
		}).map(item->{
			return new Type(0L, (String)item);
		});
		Application.getApplicationContext().getBean(TypeService.class)
			.removeTypes(getType).subscribe(success->{
				if(success==true) {
					//clear list
					typesList.getItems().clear();
					fillInListViewObservable.subscribe();
				}
			},error->{
				System.err.println(error.getMessage());
				System.err.println(error.getCause());
			});
	}
	
	public void addType(ActionEvent event) {
		TextInputDialog textInput=new TextInputDialog();
		textInput.setTitle("Enter Multimedia Type Extention");
		textInput.showAndWait();
		
		String typeInput=textInput.getResult();
		
		//check if user gave any valid input
		var validInput=Single.just(typeInput).map(input->{
			if(input!=null&& input.isEmpty()==false) {
				return true;
			}
			return false;
		});
		validInput.subscribe(success->{
			if(success==false) {
				Dialog<Void> dialog=new Dialog<Void>();
				ButtonType okButton=new ButtonType("OK", ButtonData.OK_DONE);
				dialog.getDialogPane().getButtonTypes().add(okButton);
				dialog.getDialogPane().setHeaderText("Please enter valid type");
				dialog.getDialogPane().setContentText("The value given was not valid");
				dialog.setTitle("Warning");
				dialog.showAndWait();
			}
		});
		
		validInput.toObservable()
		.filter(success->success)	
		.flatMap(valid->{
			if(valid==true) {
				//create a new type of given input
				Type type=Application.getApplicationContext().getBean(Type.class);
				type.setType(typeInput);
				return Application.getApplicationContext().getBean(TypeService.class).addType(type).toObservable();
			}
			return null;
		}).firstOrError().subscribe(success->{
			if(success!=null && success==false) {
				//prompt user than type already existed
				Dialog<Void> dialog=new Dialog<Void>();
				ButtonType okButton=new ButtonType("OK", ButtonData.OK_DONE);
				dialog.setTitle("Type already exists");
				dialog.setHeaderText("Data type exists");
				dialog.setContentText("The data type given, is already listed");
				dialog.getDialogPane().getButtonTypes()
					.add(okButton);
				dialog.showAndWait();
			}else {
				//clear the typesList, and then repopulate it
				typesList.getItems().clear();
				fillInListViewObservable.subscribe();
			}
		},error->{
			System.err.println("ERROR: "+error.getMessage());
		});
	}
}
