package model;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Renamer {
	
	public static String format(String directory){
		if(directory == ""){
			return null;
		}
		return directory.replaceAll("\\.", " ").toLowerCase();
	}
	
	public static String capitalize(String word){
		if(word == ""){
			return null;
		}
		String[] words = getWords(word);
		String capitalized = "";
		for(int i = 0; i < words.length; i++){
			if(i != words.length - 1){
				capitalized += words[i].substring(0, 1).toUpperCase() + words[i].substring(1) + " ";
			}else{
				capitalized += words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
			}
		}
		
		return capitalized;
	}
	
	public static String getFileName(String directory){
		String[] name = directory.split("\\\\");
		return name[name.length-1];
	}
	
	public static String[] getWords(String name){
		return name.split("\\s+");
	}
	
	public static int getSeason(String directory){
		String fileName = getFileName(directory);
		String[] words = getWords(fileName);
		for(String word : words){
			if(word.matches(".*s\\d+e\\d+.*")){
				word = word.substring(word.indexOf("s") + 1);
				word = word.substring(0, word.indexOf("e"));
				return Integer.parseInt(word);
			}
		}
		
		return -1;
	}
	
	public static String getExtension(String directory){
		String[] words = directory.split("\\.");
		return words[words.length-1];
	}
	
	public static int getEpisode(String directory){
		String fileName = getFileName(directory);
		String[] words = getWords(fileName);
		for(String word : words){
			if(word.matches(".*s\\d+e\\d+.*")){
				word = word.substring(word.indexOf("e") + 1);
				return Integer.parseInt(word);
			}
		}
		return -1;
	}
	
	public static String getShowName(String directory){
		String fileName = getFileName(directory);
		String[] words = getWords(fileName);
		String showName = "";
		for(int i = 0; i < words.length; i++){
			if(words[i].matches(".*s\\d+e\\d+.*")){
				for(int j = 0; j != i; j++){
					if(j != i-1){
						showName += words[j] + " ";
					}else{
						showName += words[j];
					}
				}
				return showName;
			}
		}
		return null;
	}
	
	public static String createDirectory(String destinationDirectory, String showName,
										String seasonFolderPrecursor, 
										String seasonPrecursor, int season,
										String episodePrecursor, int episode, String episodeName,
										String extension){
		
		if(showName == null){
			return null;
		}
		
		String name = destinationDirectory + "\\" + showName + "\\";
		
		if(seasonFolderPrecursor != null){
			name += seasonFolderPrecursor + season + "\\";
		}
		if(seasonPrecursor != null){
			name += seasonPrecursor + season;
		}
		
		if(episodePrecursor != null){
			name += episodePrecursor + episode + " - ";
		}
		
		name += episodeName + "." + extension;
		return name;
			
	}
	
	//Get ID (TVMaze) - Return highest from fuzzy search
	public static int getID(String name) throws Exception{
		
		if(name == null){
			return -1;
		}
		
		// Build a URL
	    String fuzzySearch = "http://api.tvmaze.com/search/shows?q=";
	    fuzzySearch += URLEncoder.encode(name, "UTF-8");
	    URL url = new URL(fuzzySearch);
	 
	    // Read from the URL
	    try{
	    	URLConnection conn = url.openConnection();
	    	conn.connect();
	    }catch(Exception e){	    	
	    	return -1;
    	}
	    
	    Scanner scanner = new Scanner(url.openStream());
	    String JSONstr = new String();
	    while (scanner.hasNext())
	    	JSONstr += scanner.nextLine();
	    scanner.close();
		
	    //Build JSON Object & return show ID
	    JSONArray array = new JSONArray(JSONstr);
	    if(array.length() <= 0 )
	    	return -1;
	    JSONObject result = array.getJSONObject(0);
	    JSONObject show = result.getJSONObject("show");
	    return show.getInt("id");
	    		
	}
		
	//Get Episode name from ID array (TVMaze)
	public static String getEpisodeName(int id, int season, int episode) throws Exception {
		
		if(id == -1)
			return "No Episode Information";
		
		// Build a URL
	    String fuzzySearch = "http://api.tvmaze.com/shows/";
	    fuzzySearch += URLEncoder.encode(id + "/episodes", "UTF-8");
	    URL url = new URL(fuzzySearch);
	    
	    // Read from the URL	       
	    Scanner scanner = new Scanner(url.openStream());
	    String JSONstr = new String();
	    while (scanner.hasNext())
	    	JSONstr += scanner.nextLine();
	    scanner.close();
	    
	    //Iterate episode list & return name
	    JSONArray array = new JSONArray(JSONstr);
	    for(int n = 0; n < array.length(); n++)
	    {
	        JSONObject episodes = array.getJSONObject(n);
	    	if(episodes.getInt("season") == season && episodes.getInt("number") == episode)
	    		return episodes.getString("name");
	    }
	    return "Episode doesn't exist";
		
	}
	
	//Get Show name from ID  (TVMaze)
	public static String getFormattedShowName(int id) throws Exception {
		
		if(id == -1)
			return null;
		
		// Build a URL
	    String episodeLookup = "http://api.tvmaze.com/shows/";
	    episodeLookup += URLEncoder.encode(id + "", "UTF-8");
	    URL url = new URL(episodeLookup);
	    
	    // Read from the URL	       
	    Scanner scanner = new Scanner(url.openStream());
	    String JSONstr = new String();
	    while (scanner.hasNext())
	    	JSONstr += scanner.nextLine();
	    scanner.close();
	    
	    //Iterate episode list & return name
	    JSONObject object = new JSONObject(JSONstr);
	    return object.getString("name");
	    
		
	}
	
	
}
