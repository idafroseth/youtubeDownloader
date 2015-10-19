package no.uio.ifi.models.search;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.I18nRegions;
import com.google.api.services.youtube.YouTube.Videos;
import com.google.api.services.youtube.model.GuideCategory;
import com.google.api.services.youtube.model.GuideCategoryListResponse;
import com.google.api.services.youtube.model.I18nLanguage;
import com.google.api.services.youtube.model.I18nLanguageListResponse;
import com.google.api.services.youtube.model.I18nRegion;
import com.google.api.services.youtube.model.I18nRegionListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.VideoCategory;
import com.google.api.services.youtube.model.VideoCategoryListResponse;

import no.uio.ifi.Auth;
import no.uio.ifi.guis.FilteredSearchGui;

/**
 * 
 * @author Ida Marie Frøseth and Richard Reimer
 *
 */
public class FilteredSearch extends Search{

	public static final int REGIONFILTER = 1;
	public static final int CATEGORYFILTER = 2;
	public static final int LANGUAGEFILTER = 3;
	public static final int GUIDECATEGORYFILTER = 4;
	public static final int TIMEFILTER = 5;
	public static final int VIDEODURATIONFILTER = 6;
	public static final int VIDEOTYPEFILTER = 7;
	public static final int GEOFILTER = 8;
	public static final int NUMBERTOSEARCHFILTER = 9;

	private Map<String, String> availableCategories = new HashMap<String, String>();
	private Map<String, String> availableLanguages = new HashMap<String, String>();
	private Map<String, String> availableGuideCategories = new HashMap<String, String>();
	private Map<String, String> availableRegions = new HashMap<String, String>();
	private Map<String, String> availableDurations = new HashMap<String, String>();
	private Map<String, String> availableVideoTypes = new HashMap<String, String>();

	
	
	private Map<String, String> availableCategoriesReverse =  new HashMap<String, String>();

	private YouTube.Search.List search;

	//public static final long NUMBER_OF_VIDEOS_RETURNED = 25;
	
	/**
	 * 
	 * @return all the available categories in youtube
	 */
	public Map<String, String> getVideoCategories() {
		configureProperties();
		availableCategories = new HashMap<String, String>();
		availableCategoriesReverse = new HashMap<String, String>();
		try {
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}
			}).setApplicationName("Get categories").build();

			YouTube.VideoCategories.List categoriesSearch = youtube.videoCategories().list("id,snippet");

			// {{ https://cloud.google.com/console }}
			String apiKey = properties.getProperty("youtube.apikey");
			categoriesSearch.setKey(apiKey);
			categoriesSearch.setRegionCode("US");
			VideoCategoryListResponse searchResponse = categoriesSearch.execute();
			List<VideoCategory> categories = searchResponse.getItems();
			for (VideoCategory vc : categories) {
				availableCategories.put(vc.getSnippet().getTitle(), vc.getId());
				availableCategoriesReverse.put(vc.getId(),vc.getSnippet().getTitle());
				System.out.println("Adding to language: (" + vc.getSnippet().getTitle() + ", " + vc.getId() + ")");
			}

		} catch (GoogleJsonResponseException e) {
			System.err.println(
					"There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return availableCategories;
	}
	public Map<String,String> getAvailableCategoriesReverse(){
		if(availableCategoriesReverse.size() < 1){
			availableCategoriesReverse = getVideoCategories();
		}
		return this.availableCategoriesReverse;
	}

	/**
	 * 
	 * @return all the available langues from youtube
	 */
	public Map<String, String> getAvailableLanguages() {
		availableLanguages = new HashMap<String, String>();
		configureProperties();
		try {
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}
			}).setApplicationName("Get languages").build();

			YouTube.I18nLanguages.List languageSearch = youtube.i18nLanguages().list("id,snippet");

			// {{ https://cloud.google.com/console }}
			String apiKey = properties.getProperty("youtube.apikey");
			languageSearch.setKey(apiKey);
			I18nLanguageListResponse searchResponse = languageSearch.execute();
			List<I18nLanguage> categories = searchResponse.getItems();
			for (I18nLanguage lc : categories) {
				availableLanguages.put(lc.getSnippet().getName(), lc.getId());
				System.out.println("Adding to language: (" + (lc.getSnippet().getName()) + ", " + lc.getId() + ")");
			}

		} catch (GoogleJsonResponseException e) {
			System.err.println(
					"There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return availableLanguages;
	}

	/**
	 * 
	 * @return all the available regions on YouTube
	 */
	public Map<String, String> getAvailableRegions() {
		availableRegions = new HashMap<String, String>();
		configureProperties();
		try {
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}
			}).setApplicationName("Get regions").build();

			YouTube.I18nRegions.List regionsSearch = youtube.i18nRegions().list("id,snippet");

			// {{ https://cloud.google.com/console }}
			String apiKey = properties.getProperty("youtube.apikey");
			regionsSearch.setKey(apiKey);
			I18nRegionListResponse searchResponse = regionsSearch.execute();
			List<I18nRegion> regions = searchResponse.getItems();
			for (I18nRegion region : regions) {
				availableRegions.put(region.getSnippet().getName(), region.getId());
				System.out
						.println("Adding to region: (" + (region.getSnippet().getName()) + ", " + region.getId() + ")");
			}

		} catch (GoogleJsonResponseException e) {
			System.err.println(
					"There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return availableRegions;
	}

	/**
	 * A guideCategory resource identifies a category that YouTube
	 * algorithmically assigns based on a channel's content or other indicators,
	 * such as the channel's popularity. The list is similar to video
	 * categories, with the difference being that a video's uploader can assign
	 * a video category but only YouTube can assign a channel category.
	 * 
	 * @return
	 */
	public Map<String, String> getAvailableGuideCategories() {
		configureProperties();
		availableGuideCategories = new HashMap<String, String>();
		try {
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}
			}).setApplicationName("Get GuideCategories").build();

			YouTube.GuideCategories.List guideCategorySearch = youtube.guideCategories().list("id,snippet");

			// {{ https://cloud.google.com/console }}
			String apiKey = properties.getProperty("youtube.apikey");
			guideCategorySearch.setKey(apiKey);
			guideCategorySearch.setRegionCode("US");
			guideCategorySearch.setRegionCode("NO");
			guideCategorySearch.setRegionCode("DE");
			GuideCategoryListResponse searchResponse = guideCategorySearch.execute();
			List<GuideCategory> gCategories = searchResponse.getItems();
			for (GuideCategory category : gCategories) {
				availableGuideCategories.put(category.getSnippet().getTitle(), category.getId());
				System.out.println("Adding to guideCategories: (" + (category.getSnippet().getTitle()) + ", "
						+ category.getId() + ")");
			}

		} catch (GoogleJsonResponseException e) {
			System.err.println(
					"There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return availableGuideCategories;
	}

	/**
	 * Making a map of all the available durations on YT
	 * 
	 * @return the available duration on YT
	 */
	public Map<String, String> getAvailableVideoDuration() {
		if (availableDurations.size() < 1) {
			availableDurations.put("Any", "any");
			availableDurations.put("More than 20min", "long");
			availableDurations.put("4 to 20 min ", "medium");
			availableDurations.put("Less then 4 min", "short");
		}
		return availableDurations;
	}

	/**
	 * Making a map of all the available types on YT
	 * 
	 * @return available types
	 */
	public Map<String, String> getAvailableVideoTypes() {
		if (availableVideoTypes.size() < 1) {
			availableVideoTypes.put("Any", "any");
			availableVideoTypes.put("Episode of shows", "episode");
			availableVideoTypes.put("Movie ", "movie");
		}
		return availableVideoTypes;
	}

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

	/**
	 * Preforms a search for videos with a string as input
	 * @param randomInput
	 * @return
	 */
	public List<SearchResult> searchBy(String randomInput){
		try {
			List<SearchResult> searchResultList = null;

			search.setQ(randomInput);

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
	
	

	/**
	 * Applying filters to the serachBy method, must be run before the search
	 * @param filterType must reflect one of the FilterSearch.FILTERTYPES
	 * @param id 
	 */
	public void setFilter(int filterType, String id) {

		System.out.println("Filter type is:" + filterType);
		switch (filterType) {

		// You can only set one category filter
		case CATEGORYFILTER:
			search.setVideoCategoryId(availableCategories.get(id));
			System.out.println(id);
			break;
		case GUIDECATEGORYFILTER:
			// I could not find a parameter to set this
			// search.set
			break;
		case LANGUAGEFILTER:
			search.setRelevanceLanguage(availableLanguages.get(id));
			break;
		// Search in a specified country but only one
		case REGIONFILTER:
			search.setRegionCode(availableRegions.get(id));
			// search.setLocation(id);
			break;
		case TIMEFILTER:
			String[] period = id.split("\\|");
			System.out.println(period[0]);
			System.out.println(period[1]);
			DateTime start = new DateTime(period[0]+"T00:00:00Z");
			DateTime end = new DateTime(period[1]+"T00:00:00Z");
			search.setPublishedBefore(end);
			search.setPublishedAfter(start);
			break;
		case VIDEODURATIONFILTER:
			search.setVideoDuration(availableDurations.get(id));
			break;
		case VIDEOTYPEFILTER:
			search.setVideoType(availableVideoTypes.get(id));
			break;
		}
	}
}
