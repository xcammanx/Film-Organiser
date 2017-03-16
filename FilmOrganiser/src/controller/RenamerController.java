package controller;

import fxTools.Controller;
import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.*;

import java.net.URL;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;



public class RenamerController extends Controller<Converter> {
	

    private OperationThread thread;
        
	@FXML VBox rootVBX;
    @FXML ChoiceBox filmTypeCB;
    @FXML TextField sourceFolderTF;
    @FXML TextField destinationFolderTF;
    @FXML TextField seasonTF;
    @FXML TextField episodeTF;
    @FXML TextField seasonFolderTF;
    @FXML Button sourceBrowseBTN;
    @FXML Button destinationBrowseBTN;
    @FXML Button previewBTN;
    @FXML Button organiseBTN;
    @FXML CheckBox seasonNumberCBX;
    @FXML CheckBox episodeNumberCBX;
    @FXML CheckBox seasonFolderNumberCBX;
    @FXML TreeView<String> currentNameTV;
    @FXML TreeView<String> futureNameTV;
    @FXML Label messageLBL;
    @FXML ProgressBar renamingPB;
    
    public final Converter getConverter() { return model; }
    public final Stage getStage() { return stage; }
    
    @FXML private void handleSourceBrowse(ActionEvent event) throws Exception{
        getConverter().createSourceDirectory(stage);
        currentNameTV.setRoot(getConverter().getCurrentNameTree());
    }
    
    @FXML private void handleDestinationBrowse(ActionEvent event) throws Exception{
        getConverter().createDestinationDirectory(stage); 
    }
    
    @FXML private void preview(ActionEvent event) throws Exception{
    	organiseBTN.disableProperty().set(false);
    	createNewNames();
    }
    
    @FXML private void moveFiles(ActionEvent event) throws Exception{
    	System.out.println(getConverter().moveFiles(getConverter().getOldFileNames(), getConverter().getNewFileNames()));
    }
    
    @FXML public void initialize() { 
    	organiseBTN.disableProperty().set(true);
    	seasonTF.textProperty().bindBidirectional(getConverter().seasonPrecursorProperty());
    	episodeTF.textProperty().bindBidirectional(getConverter().episodePrecursorProperty());
    	seasonFolderTF.textProperty().bindBidirectional(getConverter().seasonFolderPrecursorProperty());
    	
        sourceFolderTF.textProperty().bind(getConverter().sourceDirectoryPathProperty());
        destinationFolderTF.textProperty().bind(getConverter().destinationDirectoryPathProperty());
        
        seasonTF.disableProperty().bind(seasonNumberCBX.selectedProperty().not());
        episodeTF.disableProperty().bind(episodeNumberCBX.selectedProperty().not());
        seasonFolderTF.disableProperty().bind(seasonFolderNumberCBX.selectedProperty().not());
        
        renamingPB.prefWidthProperty().bind(rootVBX.widthProperty().subtract(20));
    } 
    
    public void handle(WindowEvent we) {
    	if (thread != null) {
    		thread.interrupt();
    	}
    }
    
    public synchronized void createNewNames() {
        if (thread == null) {
            thread = new OperationThread();
            thread.setDaemon(true);
            thread.start();
            
        }
    }
    
	private class OperationThread extends Thread {
		int i;
		
        @Override public void run() {
        	LinkedList<String> temp = getConverter().removeNonVideo(getConverter().getOldFileNames());
    		LinkedList<String> newName = new LinkedList<String>();
    		for(i = 0; i < temp.size(); i++){
                try {
                	Thread.sleep(300);
                	System.out.println(temp.get(i));
            		newName.add(getConverter().createNewNames(temp.get(i)));
                	Platform.runLater(() -> {
                        futureNameTV.setRoot(getConverter().createTreeFromList(newName, getConverter().getDestinationDirectoryPath()));
                        renamingPB.setProgress((double)i/(temp.size()-1));
                        messageLBL.setText(temp.get(i));
                    });
                }
                catch (Exception e) {
                    // Happens when requested to shut down
                }
            }
    		getConverter().setNewFileNames(newName);
    		System.out.println(getConverter().getNewFileNames());
    		interrupt();
        }
    }
    
}
