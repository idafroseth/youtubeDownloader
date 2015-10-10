package no.uio.ifi.management;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import no.uio.ifi.guis.YTDashGUI;
import no.uio.ifi.models.Search;
import no.uio.ifi.models.UtilitiesAPI;

public class ManagementAllRandom {
	YTDashGUI view;
	UtilitiesAPI utilAPI;
	Search search;
	Writer writer;
	Writer writer2;
	Writer writer3;
	Writer writer4;
	int counter;

	HashMap<String, String> categoriesMap;

	public ManagementAllRandom() {

	}

	/*
	 * write random videolinks to disk change directory!!
	 */
	public ManagementAllRandom(String maxPower) {
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("/Users/Richi/Desktop/randomVideos.txt"), StandardCharsets.UTF_8));

			utilAPI = new UtilitiesAPI();
			utilAPI.initialiazeCategories();
			categoriesMap = utilAPI.getCategoriesMap();

			// writer2 = new BufferedWriter(new OutputStreamWriter(new
			// FileOutputStream("/Users/Richi/Desktop/randomVideos2.txt"),
			// StandardCharsets.UTF_8));
			// writer3 = new BufferedWriter(new OutputStreamWriter(new
			// FileOutputStream("/Users/Richi/Desktop/randomVideos3.txt"),
			// StandardCharsets.UTF_8));
			// writer4 = new BufferedWriter(new OutputStreamWriter(new
			// FileOutputStream("/Users/Richi/Desktop/randomVideos4.txt"),
			// StandardCharsets.UTF_8));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// ManagementAll mng = new ManagementAll();

		ManagementAllRandom mng = new ManagementAllRandom("maxPower");
		mng.search = new Search(mng);

		// mng.view = new YTDashGUI(mng);

		Thread one = new Thread() {
			public void run() {
				try {
					for (int i = 0; i < 10000000; i++) {
						String rnd = mng.randomUrlGenerator();
						mng.searchBaseOnRandomID("watch?v=" + rnd, 1);
					}
					Thread.sleep(1);

					System.out.println("thread error.");
				} catch (InterruptedException v) {
					System.out.println(v);
				}
			}
		};

		one.start();

		// Thread two = new Thread() {
		// public void run() {
		// try {
		// for (int i = 0; i < 10000000; i++) {
		// String rnd = mng.randomUrlGenerator();
		// mng.searchBaseOnRandomID("watch?v=" + rnd, 2);
		// }
		// Thread.sleep(1);
		//
		// System.out.println("thread error.");
		// } catch (InterruptedException v) {
		// System.out.println(v);
		// }
		// }
		// };
		//
		// two.start();
		//
		// Thread three = new Thread() {
		// public void run() {
		// try {
		// for (int i = 0; i < 10000000; i++) {
		// String rnd = mng.randomUrlGenerator();
		// mng.searchBaseOnRandomID("watch?v=" + rnd, 3);
		// }
		// Thread.sleep(1);
		//
		// System.out.println("thread error.");
		// } catch (InterruptedException v) {
		// System.out.println(v);
		// }
		// }
		// };
		//
		// three.start();
		//
		// Thread four = new Thread() {
		// public void run() {
		// try {
		// for (int i = 0; i < 10000000; i++) {
		// String rnd = mng.randomUrlGenerator();
		// mng.searchBaseOnRandomID("watch?v=" + rnd, 4);
		// }
		// Thread.sleep(1);
		//
		// System.out.println("thread error.");
		// } catch (InterruptedException v) {
		// System.out.println(v);
		// }
		// }
		// };
		//
		// four.start();

	}

	public void searchBaseOnKeyWord(String keyword) {
		List<SearchResult> searchResults = search.getVideoLinkFromKeyWord(keyword);
		ListIterator<SearchResult> iteratorSearchResults = searchResults.listIterator();
		System.out.println("\n=============================================================");
		System.out
				.println("   First " + Search.NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + keyword + "\".");
		System.out.println("=============================================================\n");

		if (!iteratorSearchResults.hasNext()) {
			System.out.println(" There aren't any results for your query.");
		}

		while (iteratorSearchResults.hasNext()) {

			SearchResult singleVideo = iteratorSearchResults.next();
			ResourceId rId = singleVideo.getId();

			// Confirm that the result represents a video. Otherwise, the
			// item will not contain a video ID.
			if (rId.getKind().equals("youtube#video")) {
				// Thumbnail thumbnail =
				// singleVideo.getSnippet().getThumbnails().getDefault();
				// System.out.println(singleVideo);
				System.out.println("https://www.youtube.com/watch?v=" + rId.getVideoId());
				System.out.println(singleVideo.getSnippet().getTitle());

				view.videoQueryList_Text.append("https://www.youtube.com/watch?v=" + rId.getVideoId());
				view.videoQueryList_Text.append("\n");
				view.videoQueryList_Text.append(singleVideo.getSnippet().getTitle());
				view.videoQueryList_Text.append("\n");
			}
		}
	}

	public void searchBaseOnRandomID(String keyword, int threadNumber) {

		if (threadNumber == 1) {
			
			// get SerachList from the Youtube API
			List<SearchResult> searchResults = search.getVideoLinkFromKeyWord(keyword);
			ListIterator<SearchResult> iteratorSearchResults = searchResults.listIterator();
			System.out.println("\n=============================================================");
			System.out
					.println("   First " + Search.NUMBER_OF_VIDEOS_RETURNED + " videos for search on " + keyword + ".");
			System.out.println("=============================================================\n");

			if (!iteratorSearchResults.hasNext()) {
				System.out.println(" There aren't any results for your query.");
			}

			String duration = null;
			BigInteger likes = null;
			BigInteger favourites = null;
			BigInteger views = null;
			BigInteger dislikes = null;
			BigInteger comments = null;
			Video singleVideoList = null;
			String categoryId = null;
			String category = null;

			while (iteratorSearchResults.hasNext()) {

				SearchResult singleVideoSearchList = iteratorSearchResults.next();
				ResourceId rId = singleVideoSearchList.getId();

				
				
				// get Comments from the Youtube API
				
				
				try {
					List<CommentThread> commentsList = utilAPI.getTopLevelComments(rId.getVideoId());

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
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// get VideoList from the Youtube API
				
				List<Video> videoList = search.getVideoList(rId.getVideoId());
				Iterator<Video> iteratorVideoResults = videoList.iterator();
				if (!iteratorVideoResults.hasNext()) {
					System.out.println(" There aren't any results for your query.");
				}

				while (iteratorVideoResults.hasNext()) {

					singleVideoList = iteratorVideoResults.next();

					duration = singleVideoList.getContentDetails().getDuration();
					views = singleVideoList.getStatistics().getViewCount();
					likes = singleVideoList.getStatistics().getLikeCount();
					favourites = singleVideoList.getStatistics().getFavoriteCount();
					dislikes = singleVideoList.getStatistics().getDislikeCount();
					comments = singleVideoList.getStatistics().getCommentCount();
					categoryId = singleVideoList.getSnippet().getCategoryId();
					category = categoriesMap.get(categoryId);

				}

				// Confirm that the result represents a video. Otherwise, the
				// item will not contain a video ID.
				if (rId.getKind().equals("youtube#video")) {
					// Thumbnail thumbnail =
					// singleVideoSearch.getSnippet().getThumbnails().getDefault();

					System.out.println(singleVideoSearchList);
					System.out.println(singleVideoList);
					// System.out.println("https://www.youtube.com/watch?v=" +
					// rId.getVideoId());
					System.out.println(singleVideoSearchList.getSnippet().getTitle());
					System.out.println("Duration of the video:" + duration);
					System.out.println("Views:" + views);
					System.out.println("Likes:" + likes);
					System.out.println("Disklikes:" + dislikes);
					System.out.println("Favourites:" + favourites);
					System.out.println("Comments:" + comments);
					System.out.println("Category ID:" + categoryId);
					System.out.println("Category: " + category);
					try {

						writer.write("https://www.youtube.com/watch?v=" + rId.getVideoId());
						writer.write(singleVideoSearchList.getSnippet().getTitle());
						writer.write(System.lineSeparator());
						counter++;
						System.out.println(counter);
						System.out.println();
						System.out.println("-----------------------------------");
						System.out.println();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		// else if (threadNumber == 2) {
		// List<SearchResult> searchResults =
		// search.getVideoLinkFromKeyWord(keyword);
		// ListIterator<SearchResult> iteratorSearchResults =
		// searchResults.listIterator();
		// System.out.println("\n=============================================================");
		// System.out.println(
		// " First " + Search.NUMBER_OF_VIDEOS_RETURNED + " videos for search on
		// \"" + keyword + "\".");
		// System.out.println("=============================================================\n");
		//
		// if (!iteratorSearchResults.hasNext()) {
		// System.out.println(" There aren't any results for your query.");
		// }
		//
		// while (iteratorSearchResults.hasNext()) {
		//
		// SearchResult singleVideo = iteratorSearchResults.next();
		// ResourceId rId = singleVideo.getId();
		//
		// // Confirm that the result represents a video. Otherwise, the
		// // item will not contain a video ID.
		// if (rId.getKind().equals("youtube#video")) {
		// // Thumbnail thumbnail =
		// // singleVideo.getSnippet().getThumbnails().getDefault();
		// // System.out.println(singleVideo);
		// System.out.println("https://www.youtube.com/watch?v=" +
		// rId.getVideoId());
		// System.out.println(singleVideo.getSnippet().getTitle());
		//
		// try {
		//
		// writer2.write("https://www.youtube.com/watch?v=" + rId.getVideoId());
		// writer2.write(singleVideo.getSnippet().getTitle());
		// writer2.write(System.lineSeparator());
		// counter++;
		// System.out.println(counter);
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// }
		// } else if (threadNumber == 3) {
		// List<SearchResult> searchResults =
		// search.getVideoLinkFromKeyWord(keyword);
		// ListIterator<SearchResult> iteratorSearchResults =
		// searchResults.listIterator();
		// System.out.println("\n=============================================================");
		// System.out.println(
		// " First " + Search.NUMBER_OF_VIDEOS_RETURNED + " videos for search on
		// \"" + keyword + "\".");
		// System.out.println("=============================================================\n");
		//
		// if (!iteratorSearchResults.hasNext()) {
		// System.out.println(" There aren't any results for your query.");
		// }
		//
		// while (iteratorSearchResults.hasNext()) {
		//
		// SearchResult singleVideo = iteratorSearchResults.next();
		// ResourceId rId = singleVideo.getId();
		//
		// // Confirm that the result represents a video. Otherwise, the
		// // item will not contain a video ID.
		// if (rId.getKind().equals("youtube#video")) {
		// // Thumbnail thumbnail =
		// // singleVideo.getSnippet().getThumbnails().getDefault();
		// // System.out.println(singleVideo);
		// System.out.println("https://www.youtube.com/watch?v=" +
		// rId.getVideoId());
		// System.out.println(singleVideo.getSnippet().getTitle());
		//
		// try {
		//
		// writer3.write("https://www.youtube.com/watch?v=" + rId.getVideoId());
		// writer3.write(singleVideo.getSnippet().getTitle());
		// writer3.write(System.lineSeparator());
		// counter++;
		// System.out.println(counter);
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// }
		//
		// }
		//
		// else if (threadNumber == 4) {
		// List<SearchResult> searchResults =
		// search.getVideoLinkFromKeyWord(keyword);
		// ListIterator<SearchResult> iteratorSearchResults =
		// searchResults.listIterator();
		// System.out.println("\n=============================================================");
		// System.out.println(
		// " First " + Search.NUMBER_OF_VIDEOS_RETURNED + " videos for search on
		// \"" + keyword + "\".");
		// System.out.println("=============================================================\n");
		//
		// if (!iteratorSearchResults.hasNext()) {
		// System.out.println(" There aren't any results for your query.");
		// }
		//
		// while (iteratorSearchResults.hasNext()) {
		//
		// SearchResult singleVideo = iteratorSearchResults.next();
		// ResourceId rId = singleVideo.getId();
		//
		// // Confirm that the result represents a video. Otherwise, the
		// // item will not contain a video ID.
		// if (rId.getKind().equals("youtube#video")) {
		// // Thumbnail thumbnail =
		// // singleVideo.getSnippet().getThumbnails().getDefault();
		// // System.out.println(singleVideo);
		// System.out.println("https://www.youtube.com/watch?v=" +
		// rId.getVideoId());
		// System.out.println(singleVideo.getSnippet().getTitle());
		//
		// try {
		//
		// writer4.write("https://www.youtube.com/watch?v=" + rId.getVideoId());
		// writer4.write(singleVideo.getSnippet().getTitle());
		// writer4.write(System.lineSeparator());
		// counter++;
		// System.out.println(counter);
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// }
		//
		// }

	}

	private String randomUrlGenerator() {
		String alfabet = "0123456789_-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		String randomValue = "";
		for (int i = 0; i < 4; i++) {
			randomValue += alfabet.charAt(random.nextInt(alfabet.length()));
		}
		return randomValue;
	}

}
