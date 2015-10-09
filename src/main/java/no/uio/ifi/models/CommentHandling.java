package no.uio.ifi.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.CommentThreadSnippet;

import no.uio.ifi.Auth;

/**
 * Retrieving the top-level comments for a video via "commentThreads.list"
 * method. 
 * 
 * based on sample by
 * @author Ibrahim Ulukaya
 */
public class CommentHandling {

	/**
	 * Define a global instance of a YouTube object, which will be used to make
	 * YouTube Data API requests.
	 */
	private static YouTube youtube;
	public static final String PROPERTIES_FILENAME = "youtube.properties";

	/**
	 * List, reply to comment threads; list, update, moderate, mark and delete
	 * replies.
	 *
	 * @param args
	 *            command line args (not used).
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

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

		// Prompt the user for the ID of a video to comment on.
		// Retrieve the video ID that the user is commenting to.
		String videoId = null;

		videoId = getVideoId();

		String apiKey = properties.getProperty("youtube.apikey");

		// Call the YouTube Data API's commentThreads.list method to
		// retrieve video comment threads.

		YouTube.CommentThreads.List listcommentThreadRequest = null;

		listcommentThreadRequest = youtube.commentThreads().list("snippet").setVideoId(videoId);

		listcommentThreadRequest.setKey(apiKey);

		CommentThreadListResponse videoCommentsListResponse = null;
		videoCommentsListResponse = listcommentThreadRequest.setTextFormat("plainText").execute();

		commentsList = videoCommentsListResponse.getItems();

		if (commentsList.isEmpty()) {
			System.out.println("Can't get video comments.");
		} else {
			// Print information from the API response.

			System.out.println("\n================== Returned Video Comments ==================\n");
			for (CommentThread videoComment : commentsList) {
				CommentSnippet snippet = videoComment.getSnippet().getTopLevelComment().getSnippet();
				System.out.println("  - Author: " + snippet.getAuthorDisplayName());
				System.out.println("  - Comment: " + snippet.getTextDisplay());
				System.out.println("\n-------------------------------------------------------------\n");
			}
		}
	}

	/*
	 * Prompt the user to enter a video ID. Then return the ID.
	 */
	private static String getVideoId() throws IOException {

		String videoId = "";

		System.out.print("Please enter a video id: ");
		BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
		videoId = bReader.readLine();

		return videoId;
	}

}
