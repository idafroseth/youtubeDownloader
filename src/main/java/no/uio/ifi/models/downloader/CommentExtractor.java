package no.uio.ifi.models.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.CommentThreads;
import com.google.api.services.youtube.YouTube.Comments;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentListResponse;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoCategory;
import com.google.api.services.youtube.model.VideoCategoryListResponse;
import com.google.api.services.youtube.model.VideoListResponse;




import no.uio.ifi.Auth;
import no.uio.ifi.models.UtilitiesAPI;
import no.uio.ifi.models.search.Search;

public class CommentExtractor extends Search{
	private YouTube youtube;
	UtilitiesAPI categoryUT;
	private YouTube.CommentThreads.List listcommentThreadRequest = null;
	private YouTube.Comments.List listCommentsRequest = null;
	String json = "{\"comments\":";
	Long numComm;
	Long counter;
	
	public CommentExtractor(Long numberOfComments) {
		configureProperties();
		numComm = numberOfComments;
		initCommentThread(numberOfComments);
		initComment();
		
	}
	public void initCommentThread(Long numberOfComments){
	
		try {
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}

			}).setApplicationName("CommentExtractor").build();
			
			listcommentThreadRequest = youtube.commentThreads().list("snippet");//"id");
			listcommentThreadRequest.setFields("items");
			String apiKey = properties.getProperty("youtube.apikey");
			listcommentThreadRequest.setMaxResults(numberOfComments);
			listcommentThreadRequest.setKey(apiKey);
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void initComment(){
		
		try {
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}

			}).setApplicationName("CommentExtractor").build();
			
			listCommentsRequest = youtube.comments().list("snippet");
		//	listCommentsRequest.setFields("items(i");
			String apiKey = properties.getProperty("youtube.apikey");
			listCommentsRequest.setFields("items(snippet)");
			listCommentsRequest.setKey(apiKey);
		//	
			
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}
	//TODO handle no comments error
	public String getTopLevelComments(String videoId) throws IOException {
		json = ",\"comments\":"+ "{\"comment\":[";
		List<CommentThread> commentsList = null;
		listcommentThreadRequest.setVideoId(videoId);
		counter = 0L;
		CommentThreadListResponse videoCommentsListResponse;
		try{
			
			
			videoCommentsListResponse = listcommentThreadRequest.execute();
			commentsList = videoCommentsListResponse.getItems();
			if( commentsList.size() == 0){
				return "";//json += "]}}";
			}
			
			for(CommentThread cm : commentsList){
				
//				System.out.println(cm.getId());
			
				getComment(cm.getId());
			}
		}catch(GoogleJsonResponseException e){
			System.out.println("*******Comments disabled*********");
			return "";
			
		}
	
		

		json += "]}}";
		System.out.println(json);
		return json;

	}
	public String getComment(String commentId) throws IOException {

		List<Comment> commentsList = null;
		
		listCommentsRequest.setId(commentId);
		
		CommentListResponse commentResponse;
		try{
		
			commentResponse = listCommentsRequest.execute();
			commentsList = commentResponse.getItems();
			
			for(Comment cm : commentsList){
				json +=  cm.getSnippet().toPrettyString();
				counter++;
				if(counter <numComm ){
					json+=",";
				}
			}
			
		}catch (GoogleJsonResponseException e) {
			System.out.println("********Comments not accessible**********");
			return "";
		}

		return json;

	}
	public String topLevelComments(String videoId){
		json = ",\"comments\":"+ "{\"comment\":[";
		   try {
			//listcommentThreadRequest.list("snippet");
			listcommentThreadRequest.setVideoId(videoId);
			CommentThreadListResponse videoCommentsListResponse = listcommentThreadRequest.execute();
			
			  List<CommentThread> videoComments = videoCommentsListResponse.getItems();
			  int counter = 0;
			  int size = videoComments.size();
			  for (CommentThread videoComment : videoComments) {
                  CommentSnippet snippet = videoComment.getSnippet().getTopLevelComment().getSnippet();
                  System.out.println(snippet);
                  System.out.println("  - Author: " + snippet.getAuthorDisplayName());
                  System.out.println("  - Comment: " + snippet.getTextDisplay());
                  System.out
                          .println("\n-------------------------------------------------------------\n");
                  counter++;
                  json += videoComment.getSnippet().toPrettyString();
                  System.out.println(size);
                  System.out.println(counter);
                  if(counter<size){
                	  json +=",";
                  }
                  
              }
			json += "]}}";
			System.out.println(json);
			return json;
		   } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		   return json;
	}
	public static void main(String[] args){
		CommentExtractor uapi = new CommentExtractor(5L);
		try {
			uapi.getTopLevelComments("jeYhb8h47CE");
			uapi.topLevelComments("jeYhb8h47CE");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOEX");
			e.printStackTrace();
		}
	}

}



