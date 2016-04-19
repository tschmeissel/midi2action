package de.midi2action;

import javafx.application.Application;
import javafx.stage.Stage;

import org.apache.log4j.Logger;

import de.midi2action.view.GUI;

public class MainFX extends Application {
	private final static Logger LOGGER = Logger.getLogger(MainFX.class);
	
	@Override
	public void start(Stage primaryStage) {
		GUI.INSTANCE.build(primaryStage);
		
		LOGGER.debug("GUI build done");
	}

	public static void main(String[] args) {
		launch(args);
	}
}
