package com.borwe.playlistPlayerFx;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.borwe.playlistPlayerFx.fx.FXCompletableGenerator;
import com.borwe.playlistPlayerFx.fx.FXRunnable;
import com.borwe.playlistPlayerFx.fx.MainFXController;
import com.borwe.playlistPlayerFx.fx.functions.GenerateHelperView;
import com.borwe.playlistPlayerFx.springConfigs.MainConfig;
import com.borwe.playlistPlayerFx.springServices.PlayListService;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;

public class Application extends javafx.application.Application{

	private static ApplicationContext context;
	
	private static Window parentWindow;
	
	private static String[] args;
	
	@Getter
	private static Parent parent;
	
	public static void main(String[] args) {
		Application.args=args;
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("PlayListPlayerFX - A Media Playlist player");
		
		//load from mainDisplay as view
		parent=FXMLLoader.load(this.getClass().getResource("/fxml/mainDisplay.fxml"));
		Scene scene=new Scene(parent);
		
		primaryStage.setScene(scene);
		
		parentWindow=primaryStage.getScene().getWindow();
		
		
		
		/*
		 * once shown now go ahead and start display help page if no playlists
		 * otherwise go ahead and populate the views with playlists
		 * Also, generate access to spring boot context to be used later
		 */
		primaryStage.setOnShown(event->{
			
			
			//the spring boot background observable to be used
			var springBootBackground=Observable.just(true)
					.map(val->{
						
						Application.context= SpringApplication.run(MainConfig.class, Application.args);
						//enable the menus after we reach here
						FXRunnable runnable=()->{
							Parent parent=Application.getParent();
							MenuBar menuBar=(MenuBar) parent.lookup("#menuBar");
							//set it to enabled
							menuBar.setDisable(false);
						};
						FXCompletableGenerator.doOnUI(runnable);
						return true;
					}).flatMap(cont->{
						//for getting playlists
						return Application.getApplicationContext()
								.getBean(PlayListService.class).thereIsPlayList().map(val->{
							if(val==false) {
								Thread.sleep(1000);
								new GenerateHelperView<Void>().accept(null);
							}
							return val;
						}).toObservable();
					})
					.observeOn(Schedulers.computation());
			FXCompletableGenerator.generateCompletable("Starting Up", springBootBackground)
				.subscribe(val-> {
					// TODO Auto-generated method stub
					System.out.println("COMPLETED THIS BITCH");
				});
			
			
			
		});

		primaryStage.show();
	}

	
	public static ApplicationContext getApplicationContext() {
		if(context==null) {
			throw new NullPointerException("Sorry, no context loaded, wait for application to fully load");
		}
		return context;
	}
	
	public static Window getMainWindow() {
		return parentWindow;
	}
}
