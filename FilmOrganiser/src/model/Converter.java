/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;

import controller.RenamerController;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


/**
 *
 * @author Campbell
 */

public class Converter {
	
	//Variables
    private StringProperty sourceDirectoryPath = new SimpleStringProperty();
    private StringProperty destinationDirectoryPath = new SimpleStringProperty();
    private StringProperty seasonFolderPrecursor = new SimpleStringProperty();
    private StringProperty seasonPrecursor = new SimpleStringProperty();
    private StringProperty episodePrecursor = new SimpleStringProperty();
    private File sourceDirectory;
    private File destinationDirectory;
    private TreeItem<String> currentNameTree;
    private TreeItem<String> futureNameTree;
    private LinkedList<String> oldFileNames = new LinkedList<String>();
    private LinkedList<String> newFileNames = new LinkedList<String>();
    
    
    //Getters and Setters
    public String getSourceDirectoryPath() { return sourceDirectoryPath.get(); }
	public void setSourceDirectoryPath(String sourceDirectoryPath) { this.sourceDirectoryPath.set(sourceDirectoryPath); }
	public StringProperty sourceDirectoryPathProperty(){ return sourceDirectoryPath;}
    
	public String getSeasonFolderPrecursor() { return seasonFolderPrecursor.get(); }
	public void setSeasonFolderPrecursor(String seasonFolderPrecursor) { this.seasonFolderPrecursor.set(seasonFolderPrecursor); }
	public StringProperty seasonFolderPrecursorProperty(){ return seasonFolderPrecursor;}
	
	public String getSeasonPrecursor() { return seasonPrecursor.get(); }
	public void setSeasonPrecursor(String seasonPrecursor) { this.seasonPrecursor.set(seasonPrecursor); }
	public StringProperty seasonPrecursorProperty(){ return seasonPrecursor;}
	
	public String getEpisodePrecursor() { return episodePrecursor.get(); }
	public void setEpisodePrecursor(String episodePrecursor) { this.episodePrecursor.set(episodePrecursor); }
	public StringProperty episodePrecursorProperty(){ return episodePrecursor;}
	
	public String getDestinationDirectoryPath() { return destinationDirectoryPath.get(); }
	public void setDestinationDirectoryPath(String destinationDirectoryPath) { this.destinationDirectoryPath.set(destinationDirectoryPath); }
	public StringProperty destinationDirectoryPathProperty(){ return destinationDirectoryPath;}
	
	public File getSourceDirectory(){ return sourceDirectory; }
    public void setSourceDirectory(File sourceDirectory){ this.sourceDirectory = sourceDirectory;}
    
    public File getDestinationDirectory() {	return destinationDirectory; }
	public void setDestinationDirectory(File destinationDirectory) { this.destinationDirectory = destinationDirectory; }
	
    public TreeItem<String> getCurrentNameTree() { return currentNameTree; }
	public void setCurrentNameTree(TreeItem<String> currentNameTree) { this.currentNameTree = currentNameTree; }
	
	public TreeItem<String> getFutureNameTree() { return futureNameTree; }
	public void setFutureNameTree(TreeItem<String> futureNameTree) { this.futureNameTree = futureNameTree; }
	
	public LinkedList<String> getOldFileNames(){ return oldFileNames; }
	public void setOldFileNames(LinkedList<String> oldFileNames) { this.oldFileNames = oldFileNames; }
    
	public LinkedList<String> getNewFileNames() { return newFileNames; }
	public void setNewFileNames(LinkedList<String> newFileNames) { this.newFileNames = newFileNames; }
	
	public String createNewNames(String path) throws Exception{
		String formattedDirectory = Renamer.format(path);
		int ID = Renamer.getID(Renamer.getShowName(formattedDirectory));
		String name =  Renamer.createDirectory(
				getDestinationDirectoryPath(), 
				Renamer.getFormattedShowName(ID), 
				getSeasonFolderPrecursor(), 
				getSeasonPrecursor(), 
				Renamer.getSeason(formattedDirectory), 
				getEpisodePrecursor(), 
				Renamer.getEpisode(formattedDirectory), 
				Renamer.getEpisodeName(
						ID, 
						Renamer.getSeason(formattedDirectory), 
						Renamer.getEpisode(formattedDirectory)),
				Renamer.getExtension(path));
		return name;
	}
	
	//Initiate source directory
	public void createSourceDirectory(Stage stage){
		setSourceDirectory(getDirectory(stage));
		setSourceDirectoryPath(getSourceDirectory().getAbsolutePath());
		currentNameTree = getNodesForDirectory(sourceDirectory);
		LinkedList<String> temp = removeNonVideo(oldFileNames);
		setOldFileNames(temp);
	}
	
	//Initiate destination directory
	public void createDestinationDirectory(Stage stage){
		setDestinationDirectory(getDirectory(stage));
		setDestinationDirectoryPath(getDestinationDirectory().getAbsolutePath());
	}
	
	
	//Open Directory Chooser - Return user selection
	public File getDirectory(Stage stage){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        
        if(selectedDirectory == null){
            //TODO error message
        }else{
            return selectedDirectory.getAbsoluteFile();
        }
        return null;
    }
    
    //Convert directory to Tree Item && store path names in array
    public TreeItem<String> getNodesForDirectory(File directory) {
    	TreeItem<String> tree = new TreeItem<String>(directory.getName());
        for(File f : directory.listFiles()) {
            if(f.isDirectory()) {
            	tree.getChildren().add(getNodesForDirectory(f));
            } else {
            	tree.getChildren().add(new TreeItem<String>(f.getName()));
            	oldFileNames.add(f.getAbsolutePath());
            }
        }
        return tree;
    }
    
    public LinkedList<String> removeNonVideo(LinkedList<String> directoryArray){
    	LinkedList<String> result = new LinkedList<String>();
    	for(String fileDirectory : directoryArray){
    		Path path = Paths.get(fileDirectory);
    		try{
	    		if(Files.probeContentType(path).contains("video")){
	    			result.add(fileDirectory);
	    		}
    		}catch(Exception e){
    		}		
    	}
    	return result;
	}
    
    //Move file from one directory to another
    public boolean moveFile(String oldDirectory, String newDirectory){
    	
    	String[] words = newDirectory.split("\\\\");
  	  String directory = newDirectory.replace("\\" + words[words.length-1], "");
  	  
  	  File file = new File(directory);
  	  if(!file.exists()){
  		file.mkdirs();
  	  }
  	  
  	  try{
      	   File afile =new File(oldDirectory);
      	   if(afile.renameTo(new File(newDirectory))){
      		   return true;
      	   }
      	}catch(Exception e){
      		e.printStackTrace();
      	}
  	  return false;
    }
    
    public boolean moveFiles(LinkedList<String> oldList, LinkedList<String> newList){
    	boolean result = true;
    	for(int i = 0; i < oldList.size(); i++){
    		if(newList.get(i) != null){
    			if(!moveFile(oldList.get(i), newList.get(i))){
    				result = false;
    			}
    		}
    	}
    	return result;
    }
	
    public static TreeItem<String> createTreeFromList(LinkedList<String> paths, String destinationPath){
		String[] folders = destinationPath.split("\\\\");
		TreeItem<String> tree = new TreeItem<String>(folders[folders.length - 1]);
		tree.expandedProperty().set(true);
		
				
		for(String path : paths){
			if(path != null){
			String trimmedPath = path.replace(destinationPath + "\\", "");
			String[] nodeFolders = trimmedPath.split("\\\\");
				for(int i = 0; i < nodeFolders.length; i++){
					if(i == 0){
						tree = addNode(tree, null, nodeFolders[i]);
					}else{				
						tree = addNode(tree, nodeFolders[i-1], nodeFolders[i]);
					}
				}
			}
		}
		return tree;
	}
       
        
    public static TreeItem<String> getNode(TreeItem<String> list, String newNode){
    	if(list.getValue().equals(newNode)){
    		return list;
    	}
    	TreeItem<String> result = null;
    	for(TreeItem<String> child : list.getChildren()){
    		result = getNode(child, newNode);
    		if(result != null){
    			return result;
    		}
    	}
    	return null;
    }
    
        
	public static TreeItem<String> addNode(TreeItem<String> list, String previousNode ,String newNode){
		TreeItem node = new TreeItem<String>(newNode);
		node.expandedProperty().set(true);
		
		if(getNode(list, newNode) == null){
			if(previousNode == null){				
				list.getChildren().add(node);
			}else{
				getNode(list, previousNode).getChildren().add(node);
			}
		}else{
			return list;
		}
		return list;
	}
}
