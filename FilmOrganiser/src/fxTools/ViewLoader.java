/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxTools;

import javafx.fxml.*;
import javafx.stage.*;
import javafx.scene.*;
import java.io.*;


public class ViewLoader {
    public static <T> void showStage(T model, String fxml, String title, Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Controller.class.getResource(fxml), null, null,
                type -> {
                    try {
                        Controller<T> controller = (Controller<T>)type.newInstance();
                        controller.model = model;
                        controller.stage = stage;
                        return controller;
                    } catch (Exception e) { throw new RuntimeException(e); }
                });
        Parent root = loader.load();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.show();
    }
}