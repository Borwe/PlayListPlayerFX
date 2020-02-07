package com.borwe.playlistPlayerFx.fx.functions;


import com.borwe.playlistPlayerFx.Application;
import com.borwe.playlistPlayerFx.HelpView;

import io.reactivex.functions.Consumer;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class GenerateHelperView<T> implements Consumer<T>{

	@Override
	public void accept(T t) {
		Stage stage=new Stage();
		stage.initOwner(Application.getMainWindow());
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Docs");
		HelpView help=new HelpView();
		try {
			help.start(stage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
