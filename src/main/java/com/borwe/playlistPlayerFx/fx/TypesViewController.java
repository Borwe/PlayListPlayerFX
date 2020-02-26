package com.borwe.playlistPlayerFx.fx;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.borwe.playlistPlayerFx.Application;
import com.borwe.playlistPlayerFx.data.Type;
import com.borwe.playlistPlayerFx.springServices.TypeService;
import com.borwe.playlistPlayerFx.thirdobjects.MuttableString;

import io.reactivex.MaybeSource;
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
		
		fillInListViewObservable=Single.defer(()->Application.getApplicationContext().getBean(TypeService.class)
			.getAlltypes().map(type->{
				System.out.println("TYPE: "+type);
				return type.getType();
			}).toList().map(list->{
							
				System.out.println("UPDATE UI");
				FXRunnable updateUIWithTypes=()->{
					
					while(typesList==null) {
						try {
							System.out.println("HEY WHAT THE FUCK?");
							Thread.currentThread().sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					typesList.getItems().addAll(list);
					typesList.refresh();
				};
					
				FXCompletableGenerator.doOnUI(updateUIWithTypes);
				
				return typesList;
			}));
				
		var enableTypeRemovalButton=fillInListViewObservable.map(typeList->{
				//set listener to enable disable removeType button
				var removeEnabler=new EventHandler<Event>() {

					@Override
					public void handle(Event arg0) {
						// TODO Auto-generated method stub
						var selection=typesList.getSelectionModel();
						if(selection.isEmpty()==true) {
							FXRunnable disable=()->removeType.setDisable(true);
							FXCompletableGenerator.doOnUI(disable);
						}else {
							FXRunnable enable=()->removeType.setDisable(false);
							FXCompletableGenerator.doOnUI(enable);
						}
					}
				};
				typesList.setOnMouseClicked(removeEnabler);
				typesList.setOnKeyPressed(removeEnabler);
				return typesList;
		}).toObservable();
		
		FXCompletableGenerator.generateCompletable("Enable/Disable Button", enableTypeRemovalButton)
		.subscribe();
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
		var removeTypeObserver=Application.getApplicationContext().getBean(TypeService.class)
			.removeTypes(getType).filter(success->{
				if(success==true) {
					FXRunnable runnable=()->{
						//clear list
						typesList.getItems().clear();
						//disable remove button
						removeType.setDisable(true);
					};
					FXCompletableGenerator.doOnUI(runnable);
					fillInListViewObservable.subscribe();
				}
				return success;
			});
		FXCompletableGenerator.generateCompletable("Remvoing element", removeTypeObserver.toObservable())
			.subscribe();
	}
	
	public void addType(ActionEvent event) {
		MuttableString typeInput=Application.getApplicationContext().getBean(MuttableString.class);
		
		Single<Boolean> addingTYpeObservable=Single.just(typeInput).map(input->{
            ReentrantLock lock=new ReentrantLock(true);
			FXRunnable getVals=()->{
				TextInputDialog textInput=new TextInputDialog();
				textInput.setTitle("Enter Multimedia Type Extention");
				textInput.setContentText("Enter value, eg MP3 for mp3");
				textInput.showAndWait();
                String result=textInput.getResult();
                System.out.println("result: "+result);
				//since rxjava creates new objects
                typeInput.setString(result);
                System.out.println("TYPE INPUT: "+typeInput);
			};

			
			FXCompletableGenerator.doOnUI(getVals,lock);
			//now wait for lock before going ahead
            lock.lock();
            typeInput.strip();
            System.out.println("VALUE: "+typeInput);
			return typeInput;
		}).map(input->{
            System.out.println("VALUE: "+input);
			if(input!=null&& input.isEmpty()==false) {
				return true;
			}
			return false;
		}).map(success->{
            ReentrantLock lock=new ReentrantLock(true);
			if(success==false) {
				//give warning
				FXRunnable runnable=()->{
					Dialog<Void> dialog=new Dialog<Void>();
					ButtonType okButton=new ButtonType("OK", ButtonData.OK_DONE);
					dialog.getDialogPane().getButtonTypes().add(okButton);
					dialog.getDialogPane().setHeaderText("Please enter valid type");
					dialog.getDialogPane().setContentText("The value given was not valid");
					dialog.setTitle("Warning");
					dialog.showAndWait();
				};
				FXCompletableGenerator.doOnUI(runnable,lock);
			}
			//wait until warning has been displayed then go ahead, if it was displayed,
			//otherwise just go on ahead
            lock.lock();
			return success;
		}).filter(success->success).flatMap(valid->{
			//create a new type of given input
			Type type=Application.getApplicationContext().getBean(Type.class);
			type.setType(typeInput.getString());
			return Application.getApplicationContext().getBean(TypeService.class).addType(type).toMaybe();
		}).toSingle().map(success->{
			if(success!=null && success==false) {
				//prompt user than type already existed
				FXRunnable runnable=()->{
					Dialog<Void> dialog=new Dialog<Void>();
					ButtonType okButton=new ButtonType("OK", ButtonData.OK_DONE);
					dialog.setTitle("Type already exists");
					dialog.setHeaderText("Data type exists");
					dialog.setContentText("The data type given, is already listed");
					dialog.getDialogPane().getButtonTypes()
						.add(okButton);
					dialog.showAndWait();
				};
				FXCompletableGenerator.doOnUI(runnable);
			}else {
				//clear the typesList, and then repopulate it
				typesList.getItems().clear();
				fillInListViewObservable.subscribe();
			}
			return success;
		});
		
		FXCompletableGenerator
			.generateCompletable("Adding type", addingTYpeObservable)
			.subscribe(System.out::println,System.err::println);
	}
}
