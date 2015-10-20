package no.uio.ifi.models.search;

import java.io.IOException;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import no.uio.ifi.Auth;

public class KeyWordSearch extends Search{

	private YouTube.Search.List search;

	/**
	 * Setting up a HTTP connection with youtube and setting the properties for
	 * the API
	 */
	public void init() {
		configureProperties();
		try {
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}

			}).setApplicationName("SERACH").build();
			
			
			search = youtube.search().list("id");//, recordingDetails, contentDetails, statistics");
			search.setFields("items(id/videoId)");
			String apiKey = properties.getProperty("youtube.apikey");
			search.setType("video");
			search.setKey(apiKey);
			
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}
	public List<SearchResult> searchBy(String keyWord){
		List<SearchResult> searchResultList = null;
		try{
			search.setQ(keyWord);
	
			search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
	
			// Call the API and print results.
			SearchListResponse searchResponse = search.execute();
			searchResultList = searchResponse.getItems();
	
			return searchResultList;
	
		} catch (GoogleJsonResponseException e) {
			System.err.println(
					"There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	
		return null;
	}
}
