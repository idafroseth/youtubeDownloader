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
	private CommentThreadListResponse videoCommentsListResponse;
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
					System.out.println("{\"video\":" +v.toPrettyString().substring(0, v.toPrettyString().length()-1)+commentExtractor.getTopLevelComments(v.getId())+"}");
					videoJSON = v;
					
					switch(fileType){
					case "JSON":
						saveMetaData( v.toPrettyString());
						break;
					case "XML":
						String videoJson = v.toPrettyString();
						JSONObject json = new JSONObject(videoJson);
						String xml = XML.toString(json, "video");
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
	
	/**
	 * Retrive the video information from youtube and saves it in a file named videoInfo.xml at a specified path.
	 * @param videoIdsQueue a queue of videoIds to retrieve
	 * @param path the folder to save the metadata
	 * @return a Map of <VideoId, YouTube.Video> 
	 */
//	public Map<String, Video> saveXmlVideoContent(ArrayList<String> videoIdsQueue, File path) {
//		Map<String, Video> videoJSON = new HashMap<String, Video>();
//		try {
//			initDataContent();
//			List<Video> videoList;
//			initOutputFile(path, "/videoInfo.xml");
//			for (String videoId : videoIdsQueue) {
//				searchContent.setId(videoId);
//				VideoListResponse listResponse;
//				listResponse = searchContent.execute();
//				videoList = listResponse.getItems();
//				for (Video v : videoList) {
//					// System.out.println(v.getSnippet().getTitle());//.getSnippet()
//					// +""+ v.getStatistics() + "" + v.getContentDetails() + ""
//					// + v.getStatus());
//					System.out.println(v.getId());
//					String videoJson = v.toPrettyString();// "{\"video\":" +
//															// v.toPrettyString()
//															// + "}";
//					videoJSON.put(v.getId(), v);
//					JSONObject json = new JSONObject(videoJson);
//					String xml = XML.toString(json, "video");
//					System.out.println("xml: " + xml);
//					saveMetaData(xml);
//				}
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return videoJSON;
//	}
//	public Map<String, Video> saveCSVVideoContent(ArrayList<String> videoIdsQueue, File path) {
//
//		Map<String, Video> videoJSON = new HashMap<String, Video>();
//		try {
//			initDataContent();
//			List<Video> videoList;
//			initOutputFile(path, "/videoInfo.csv");
//			for (String videoId : videoIdsQueue) {
//				searchContent.setId(videoId);
//				VideoListResponse listResponse;
//				listResponse = searchContent.execute();
//				videoList = listResponse.getItems();
//				int i =0;
//				System.out.println("VideoInfoExtractor number of videos in the list " + videoList.size());
//				for (Video v : videoList) {
//					System.out.println("This response contained: " + i++);
//					String csv = convertJsonToCSV( new JSONObject("{\"video\":[" + v.toString() + "]}"));
//					System.out.println(v.getId());
//					videoJSON.put(v.getId(), v);
//					saveMetaData(csv);
//
//				}
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return videoJSON;
//
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

	/**
	 * Method that returns a list for the top level comments for a given videoId
	 * @param videoId
	 * @return a list of comment threads for this video
	 * @throws IOException
	 */
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
		for (CommentThread cm : commentsList) {
			System.out.println(cm);
		}

		return commentsList;

	}
}
