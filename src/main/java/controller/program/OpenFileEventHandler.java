package controller.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import controller.language.LanguageController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.FileChooser;

public class OpenFileEventHandler implements EventHandler <ActionEvent> {

	private final ProgramController controller;
	private final LanguageController lang;
	
	public OpenFileEventHandler(ProgramController controller) {
		this.controller = controller;
		this.lang = this.controller.getStage().getLanguageController();
	}
	
	@Override
	public void handle(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(this.lang.getRes().getString("of_title"));
		chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Java-Datei", "*.java"));
		chooser.setInitialDirectory(controller.getStage().getDir());
		File newFile = chooser.showOpenDialog(controller.getStage());
		if(newFile != null) {
			try (BufferedReader reader = new BufferedReader(new FileReader(newFile))) {

		        String line = reader.readLine();
		        String fname = newFile.getName();
		        //remove file extension
		        int pos = fname.lastIndexOf(".");
		        if (pos > 0) {
		            fname = fname.substring(0, pos);
		        }
	        	if(!controller.isProgramOpen(fname)) {
	        		 if(!line.matches("import models.*; import annotations.*; public class "+fname+" extends Hero \\{ public(.*)")) {
	        			Alert alert = new Alert(AlertType.INFORMATION, this.lang.getRes().getString("of_alert"), ButtonType.OK);
	 	    			alert.showAndWait();
	 	    			controller.startNewStage(fname);
	        		 } else {
	        			 //Success!
	        			 String content = new String(Files.readAllBytes(Paths.get(newFile.getPath())));
	        			 content = content.substring(content.indexOf('\n')+1, content.lastIndexOf('\n'));
	        			 //Open in existing stage or start a new one?
	        			 Alert alert = new Alert(AlertType.CONFIRMATION);
	        			 alert.setTitle(this.lang.getRes().getString("of_title"));
	        			 alert.setHeaderText(this.lang.getRes().getString("of_header"));

	        			 ButtonType buttonTypeOne = new ButtonType(this.lang.getRes().getString("of_current"));
	        			 ButtonType buttonTypeTwo = new ButtonType(this.lang.getRes().getString("of_new"));
	        			 ButtonType buttonTypeCancel = new ButtonType(this.lang.getRes().getString("of_cancel"), ButtonData.CANCEL_CLOSE);

	        			 alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
	        			 Optional<ButtonType> result = alert.showAndWait();
	        			 if (result.get() == buttonTypeOne){
	        				 controller.openProgramOnCurrentStage(fname, content);
	        			 } else if (result.get() == buttonTypeTwo) {
	        				 controller.startNewStage(fname, content);
	        			 } else {
	        			     // Cancel?
	        			 }
	        		 }
	        	} else {
	        		Alert alert = new Alert(AlertType.INFORMATION, this.lang.getRes().getString("of_opened"), ButtonType.OK);
	    			alert.showAndWait();
	        	}
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
}
