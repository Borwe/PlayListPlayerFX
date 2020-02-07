package com.borwe.playlistPlayerFx.fx.functions;

import java.io.IOException;

import com.borwe.playlistPlayerFx.Application;
import com.borwe.playlistPlayerFx.TypesView;

import io.reactivex.functions.Consumer;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GenerateTypesView<T> implements Consumer<T> {

	/**
	 * Creates a TypesView to be viewed by anybody
	 */
	@Override
	public void accept(T arg0) {
		
		Stage stage=new Stage();
		stage.initOwner(Application.getMainWindow());
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Showing Supported Types");
		
		TypesView typesView=new TypesView();
		try {
			typesView.start(stage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
