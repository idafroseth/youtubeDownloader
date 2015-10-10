package no.uio.ifi.management;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
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
	int counter;
	ManagementAllRandom mng;

	public static String FILEPATH = "/Users/Richi/Desktop/randomVideos.txt";

	HashMap<String, String> categoriesMap;

	public ManagementAllRandom() {

	}

	/*
	 * write random videolinks to disk change directory!!
	 */
	public ManagementAllRandom(String maxPower) {
		utilAPI = new UtilitiesAPI();
		utilAPI.initialiazeCategories();
		categoriesMap = utilAPI.getCategoriesMap();

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
						mng.searchBaseOnRandomID("watch?v=" + rnd);
					}
					Thread.sleep(1);

					System.out.println("thread error.");
				} catch (InterruptedException v) {
					System.out.println(v);
				}
			}
		};

		one.start();

		Thread two = new Thread() {
			public void run() {
				try {
					for (int i = 0; i < 10000000; i++) {
						String rnd = mng.randomUrlGenerator();
						mng.searchBaseOnRandomID("watch?v=" + rnd);
					}
					Thread.sleep(1);

					System.out.println("thread error.");
				} catch (InterruptedException v) {
					System.out.println(v);
				}
			}
		};

		two.start();

		Thread three = new Thread() {
			public void run() {
				try {
					for (int i = 0; i < 10000000; i++) {
						String rnd = mng.randomUrlGenerator();
						mng.searchBaseOnRandomID("watch?v=" + rnd);
					}
					Thread.sleep(1);

					System.out.println("thread error.");
				} catch (InterruptedException v) {
					System.out.println(v);
				}
			}
		};

		three.start();

		Thread four = new Thread() {
			public void run() {
				try {
					for (int i = 0; i < 10000000; i++) {
						String rnd = mng.randomUrlGenerator();
						mng.searchBaseOnRandomID("watch?v=" + rnd);
					}
					Thread.sleep(1);

					System.out.println("thread error.");
				} catch (InterruptedException v) {
					System.out.println(v);
				}
			}
		};

		four.start();

		Thread five = new Thread() {
			public void run() {
				try {
					for (int i = 0; i < 10000000; i++) {
						String rnd = mng.randomUrlGenerator();
						mng.searchBaseOnRandomID("watch?v=" + rnd);
					}
					Thread.sleep(1);

					System.out.println("thread error.");
				} catch (InterruptedException v) {
					System.out.println(v);
				}
			}
		};

		five.start();

	}

	// get all the information from the youtube API and save it to file

	public void searchBaseOnRandomID(String keyword) {

		// get SerachList from the Youtube API
		List<SearchResult> searchResults = search.getVideoLinkFromKeyWord(keyword);
		ListIterator<SearchResult> iteratorSearchResults = searchResults.listIterator();
		System.out.println("\n=============================================================");
		System.out.println("   First " + Search.NUMBER_OF_VIDEOS_RETURNED + " videos for search on " + keyword + ".");
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

				// TODO fix duration format

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

					File file = new File(FILEPATH);
					if (!file.exists()) {
						file.createNewFile();
					}
					FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write("https://www.youtube.com/watch?v=" + rId.getVideoId());
					bw.write(";");
					bw.write(singleVideoSearchList.getSnippet().getTitle());
					bw.write(";");
					bw.write(duration);
					bw.write(";");
					bw.write(views.toString());
					bw.write(";");
					bw.write(likes.toString());
					bw.write(";");
					bw.write(dislikes.toString());
					bw.write(";");
					bw.write(favourites.toString());
					bw.write(";");
					bw.write(comments.toString());
					bw.write(";");
					bw.write(categoryId);
					bw.write(";");
					bw.write(category);

					bw.write(System.lineSeparator());
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				counter++;
				System.out.println(counter);
				System.out.println();
				System.out.println("-----------------------------------");
				System.out.println();
			}
		}

	}

	public String randomUrlGenerator() {
		String alfabet = "0123456789_-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		String randomValue = "";
		for (int i = 0; i < 4; i++) {
			randomValue += alfabet.charAt(random.nextInt(alfabet.length()));
		}
		return randomValue;
	}

}
