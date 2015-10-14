package no.uio.ifi.models;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import no.uio.ifi.Auth;

public class VideoInfoExtracter extends Search{
	private YouTube.Videos.List searchContent;

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
	public void getVideoContent(LinkedList<String> videoIdsQueue){
		try {
			initDataContent();
			List<Video> videoList;
			while(!videoIdsQueue.isEmpty()){
				searchContent.setId(videoIdsQueue.removeFirst());
				VideoListResponse listResponse;
				listResponse = searchContent.execute();
				videoList = listResponse.getItems();
				for(Video v : videoList){
					System.out.println(v.getStatistics());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
