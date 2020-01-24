package com.borwe.playlistPlayerFx.fx;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class MainFXController {

	@FXML
	MenuItem menu_exit;
	
	@FXML
	public void initialize() {
	}
	
	//close the all application
	public void closeAppliction() {
		System.exit(0);
	}
}
