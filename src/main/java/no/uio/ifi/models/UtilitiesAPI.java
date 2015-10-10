package no.uio.ifi.models;

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
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.VideoCategory;
import com.google.api.services.youtube.model.VideoCategoryListResponse;

import no.uio.ifi.Auth;

public class UtilitiesAPI {

	public static final String PROPERTIES_FILENAME = "youtube.properties";
	HashMap<String, String> categoriesMap;
	private static YouTube youtube;
	UtilitiesAPI categoryUT;

	public UtilitiesAPI() {

	}

	public void initialiazeCategories() {

		categoryUT = new UtilitiesAPI();
		categoriesMap = new HashMap<String, String>();

		List<VideoCategory> categoryList = categoryUT.getCategoryList("US");
		Iterator<VideoCategory> iteratorCategoryResults = categoryList.iterator();
		if (!iteratorCategoryResults.hasNext()) {
			System.out.println(" There aren't any results for your query.");
		}

		while (iteratorCategoryResults.hasNext()) {

			VideoCategory singleVideo = iteratorCategoryResults.next();

			// System.out.println(singleVideo);
			System.out.println(singleVideo.getId());
			System.out.println(singleVideo.getSnippet().getTitle());

			categoriesMap.put(singleVideo.getId(), singleVideo.getSnippet().getTitle());
		}
	}

	public List<VideoCategory> getCategoryList(String regionCode) {
		List<VideoCategory> categoryList = null;
		Properties properties = new Properties();
		try {
			InputStream in = Search.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
			properties.load(in);

		} catch (IOException e) {
			System.err.println(
					"There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause() + " : " + e.getMessage());
			System.exit(1);
		}

		try {
			// This object is used to make YouTube Data API requests. The last
			// argument is required, but since we don't need anything
			// initialized when the HttpRequest is initialized, we override
			// the interface and provide a no-op function.
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
				@Override
				public void initialize(HttpRequest request) throws IOException {
				}
			}).setApplicationName("youtube-cmdline-search-sample").build();

			// Set your developer key from the Google Developers Console for
			// non-authenticated requests. See:
			// https://console.developers.google.com/
			String apiKey = properties.getProperty("youtube.apikey");

			// Call the YouTube Data API's youtube.videos.list method to
			// retrieve the resources that represent the specified videos.

			YouTube.VideoCategories.List videoCategoryList = youtube.videoCategories().list("snippet")
					.setRegionCode(regionCode);

			// Set your developer key
			videoCategoryList.setKey(apiKey);

			VideoCategoryListResponse listResponse = videoCategoryList.execute();

			categoryList = listResponse.getItems();
			System.out.println(categoryList);

		} catch (GoogleJsonResponseException e) {
			System.err.println(
					"There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return categoryList;
	}

	public HashMap<String, String> getCategoriesMap() {
		return categoriesMap;
	}

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
		}).setApplicationName("youtube-cmdline-search-sample").build();

		String apiKey = properties.getProperty("youtube.apikey");

		// Call the YouTube Data API's commentThreads.list method to
		// retrieve video comment threads.

		YouTube.CommentThreads.List listcommentThreadRequest = null;

		listcommentThreadRequest = youtube.commentThreads().list("snippet").setVideoId(videoId);

		listcommentThreadRequest.setKey(apiKey);

		CommentThreadListResponse videoCommentsListResponse = null;
		videoCommentsListResponse = listcommentThreadRequest.setTextFormat("plainText").execute();

		commentsList = videoCommentsListResponse.getItems();

		return commentsList;

	}

}