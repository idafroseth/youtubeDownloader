package no.uio.ifi.models.downloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import no.uio.ifi.Auth;
import no.uio.ifi.models.search.Search;

/**
 * 
 * @author Ida Marie Frøseth and Richard Reimer
 *
 */
public class VideoInfoExtracter extends Search {
	private YouTube.Videos.List searchContent;
	BufferedWriter writer;
	String fileType;
	Map<String, Video> videoJSON = new HashMap<String, Video>();
	CommentExtractor commentExtractor = new  CommentExtractor(5L);
	/**
	 * setting the filetype
	 */
	public VideoInfoExtracter(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * Initialize the HTTP request to get the information from the datacontent
	 */
	public void initDataContent() {
		configureProperties();
		try {
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}

			}).setApplicationName("getContent").build();

			searchContent = youtube.videos()
					.list("snippet, contentDetails, player, recordingDetails,statistics,status,topicDetails");// ,
																												// fileDetails,processingDetails,suggestions");//,,,,");
			String apiKey = properties.getProperty("youtube.apikey");

			System.out.println("Configure properties");

			searchContent.setKey(apiKey);

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	

	/**
	 * 
	 * @param videoId
	 * @return
	 */
	public Video getVideoInfo(String videoId) {
		Video videoJSON = null; 
		try {
			List<Video> videoList;
	
				searchContent.setId(videoId);
				VideoListResponse listResponse = searchContent.execute();
				videoList = listResponse.getItems();
				
				for (Video v : videoList) {
					
					videoJSON = v;
				//	String jsonString = "{\"video\":" +v.toPrettyString().substring(0, v.toPrettyString().length()-1)+commentExtractor.getTopLevelComments(v.getId())+"}";
					
					String jsonString = v.toPrettyString();
					switch(fileType){
					case "JSON":
						saveMetaData( v.toPrettyString());
						break;
					case "XML":
						JSONObject jsonObject = new JSONObject(jsonString);
						String xml = XML.toString(jsonObject, "video");
						saveMetaData(xml);
						break;
					case "CSV":
						//not implemented
						break;
					default:
						//Dont save
						break;
					}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return videoJSON;
	}

//	}
	public String convertJsonToCSV(JSONObject json){
//	    JSONObject output = new JSONObject(json);
	    JSONArray jsonArray = json.getJSONArray("video");
	    String csv = CDL.toString(jsonArray);
	    System.out.println(csv);
	    return csv;
	}

	/**
	 * Creates a file in the given filepath and with the give filename
	 * 
	 * @param filePath
	 * @param filename
	 */
	public void initOutputFile(File filePath, String filename) {
		writer = null;
		try {
			File utskrift = new File(filePath + filename);
			System.out.println(utskrift);
			writer = new BufferedWriter(new FileWriter(utskrift));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that writes to the specified file. Takes a string as input, so it doesn´t matter if its json, xml or whatever
	 * @param videoInfo
	 */
	public void saveMetaData(String videoInfo) {

		try {
			writer.write(videoInfo);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
