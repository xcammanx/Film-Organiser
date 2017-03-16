/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campbellsfilmorganiser;

import fxTools.ViewLoader;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Converter;

/**
 *
 * @author Campbell
 */
public class CampbellsFilmOrganiser extends Application {
	public static void main(String[] args) { launch(args); 	}
	
	 @Override
    public void start(Stage stage) throws Exception {
		Converter converter = new Converter();
        ViewLoader.showStage(converter, "/view/renamer.fxml","Campbell's Film Organiser", stage);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	      public void handle(WindowEvent we) {
	          System.out.println("Stage is closing");
	      }
      });
    }
}
    
