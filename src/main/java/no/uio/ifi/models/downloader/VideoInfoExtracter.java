package no.uio.ifi.models.downloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

public class VideoInfoExtracter extends Search{
	private YouTube.Videos.List searchContent;
	private CommentThreadListResponse videoCommentsListResponse;
	 BufferedWriter writer;

	public VideoInfoExtracter(){
		
	}
	public void initDataContent() {
		configureProperties();
		try {
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}

			}).setApplicationName("getContent").build();
			
			
			searchContent = youtube.videos().list("snippet, contentDetails, player, recordingDetails,statistics,status,topicDetails");//, fileDetails,processingDetails,suggestions");//,,,,");
			String apiKey = properties.getProperty("youtube.apikey");
		
			System.out.println("Configure properties");
			
			searchContent.setKey(apiKey);
			
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}
	public	Map<String, Video> getVideoMetadata(LinkedList<String> videoIdsQueue){
		Map<String, Video> videoJSON = new HashMap<String, Video>();
		try {
			initDataContent();
			List<Video> videoList;
			while(!videoIdsQueue.isEmpty()){
				searchContent.setId(videoIdsQueue.removeFirst());
				VideoListResponse listResponse;
				listResponse = searchContent.execute();
				videoList = listResponse.getItems();
				for(Video v : videoList){
					videoJSON.put(v.getId(), v);
					System.out.println(v.getId());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return videoJSON;
	}
	
	public	Map<String, Video> saveJsonVideoContent(LinkedList<String> videoIdsQueue, File path){
	
		Map<String, Video> videoJSON = new HashMap<String, Video>();
		try {
			initDataContent();
			List<Video> videoList;
			initOutputFile(path, "/videoJSONInfo.txt");
			while(!videoIdsQueue.isEmpty()){
				searchContent.setId(videoIdsQueue.removeFirst());
				VideoListResponse listResponse;
				listResponse = searchContent.execute();
				videoList = listResponse.getItems();
				for(Video v : videoList){
					System.out.println(v.getId());
					videoJSON.put(v.getId(), v);
					saveMetaData("{\n \"video\" :" + v.toPrettyString()+"\n}");

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return videoJSON;
		
	}
	public	Map<String, Video> saveXmlVideoContent(LinkedList<String> videoIdsQueue, File path){
		Map<String, Video> videoJSON = new HashMap<String, Video>();
		try {
			initDataContent();
			List<Video> videoList;
			initOutputFile(path, "/videoXMLInfo.txt");
			while(!videoIdsQueue.isEmpty()){
				searchContent.setId(videoIdsQueue.removeFirst());
				VideoListResponse listResponse;
				listResponse = searchContent.execute();
				videoList = listResponse.getItems();
				for(Video v : videoList){
				//	System.out.println(v.getSnippet().getTitle());//.getSnippet() +""+  v.getStatistics() + "" + v.getContentDetails() + "" + v.getStatus());
					System.out.println(v.getId());
					String videoJson = v.toPrettyString();//"{\"video\":" + v.toPrettyString() + "}";
					videoJSON.put(v.getId(), v);
					JSONObject json  = new JSONObject( videoJson );  
					String xml = XML.toString(json, "video");
					System.out.println("xml: " + xml);
					saveMetaData(xml);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return videoJSON;
	}

	public void initOutputFile(File filePath, String filename){
		   writer = null;
	        try {
	            File utskrift  = new File(filePath + filename);
	            System.out.println(utskrift);
	            writer = new BufferedWriter(new FileWriter(utskrift));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
	public void saveMetaData( String videoInfo){

            try {
				writer.write(videoInfo);
				writer.newLine();
		        writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	//TODO handle no comments error
	public List<CommentThread> getTopLevelComments(String videoId) throws IOException {

		List<CommentThread> commentsList = null;
		Properties properties = new Properties();
		try {
			InputStream in = Search.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
			properties.load(in);

		} catch (IOException e) {
			System.err.println(
					"There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause() + " : " + e.getMessage());
			System.exit(1);
		}

		// This object is used to make YouTube Data API requests.
		youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
			public void initialize(HttpRequest request) throws IOException {
			}
		}).setApplicationName("YT-downloader").build();

		String apiKey = properties.getProperty("youtube.apikey");

		// Call the YouTube Data API's commentThreads.list method to
		// retrieve video comment threads.

		YouTube.CommentThreads.List listcommentThreadRequest = null;

		listcommentThreadRequest = youtube.commentThreads().list("items/snippet").setVideoId(videoId);
		listcommentThreadRequest.setTextFormat("plainText");

		listcommentThreadRequest.setKey(apiKey);

		CommentThreadListResponse videoCommentsListResponse = null;
		videoCommentsListResponse = listcommentThreadRequest.execute();

		commentsList = videoCommentsListResponse.getItems();
		for(CommentThread cm : commentsList){
			System.out.println(cm);
		}

		return commentsList;

	}
}
