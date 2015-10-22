package no.uio.ifi.management;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONObject;
import org.json.XML;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.JPanel;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import no.uio.ifi.guis.DownloadProgressBar;
import no.uio.ifi.guis.FilteredSearchGui;
import no.uio.ifi.guis.WaitDialog;
import no.uio.ifi.models.downloader.VideoInfoExtracter;
import no.uio.ifi.models.search.FilteredSearch;
import no.uio.ifi.models.search.GeolocationSearch;
import no.uio.ifi.models.search.RandomVideoIdGenerator;
import no.uio.ifi.models.search.Search;

/**
 * Management for the filtered search Displaying a gui and handling the dialog
 * with the searchbox and crawler
 * 
 * @author Ida Marie Frøseth and Richard Reimer
 *
 */
public class ManagementFilteredSearch {
	FilteredSearch filterSearch = new FilteredSearch();
	FilteredSearchGui gui = new FilteredSearchGui(this);
	public int NUMBER_OF_VIDEOS_TO_SEARCH = 100000;
	public int NUMBER_OF_VIDEOS_RETRIVED = 0;
	int NUMBER_OF_THREADS=5;
//	ArrayList<String> resultCache = new ArrayList<String>();

	int threadCount = 0;
	HashMap<String, String> availableCategories;
	HashMap<String, String> availableLanguages;
	HashMap<String, String> availableRegions;
	HashMap<String, String> availableDuration;
	HashMap<String, String> availableVideoTypes;

	File filepath;

	String videoInfo;
	
	DownloadProgressBar wait;
	
	CountDownLatch latch;
	
//	VideoInfoExtracter infoExtracter = new VideoInfoExtracter();

	Map<String, Video> videoInfoResult;
	int count = 0;
	ThreadGroup tg = new ThreadGroup("Download");
	/**
	 * Retrieving all the filters and then display the window. 
	 */
	public ManagementFilteredSearch() {
		
		gui.initWindow();
		WaitDialog wait = new WaitDialog("Downloading available filters from YouTube");
		HashMap<String, String> availableCategories = (HashMap<String, String>) filterSearch.getVideoCategories();
		HashMap<String, String> availableLanguages = (HashMap<String, String>) filterSearch.getAvailableLanguages();
		HashMap<String, String> availableRegions = (HashMap<String, String>) filterSearch.getAvailableRegions();
		HashMap<String, String> availableDuration = (HashMap<String, String>) filterSearch.getAvailableVideoDuration();
		HashMap<String, String> availableVideoTypes = (HashMap<String, String>) filterSearch.getAvailableVideoTypes();

		HashMap<String, String> availableVideoDefinitions = (HashMap<String, String>) filterSearch.getAvailableVideoDefinition();
		
		gui.addFilterBox(availableCategories, "Category:", FilteredSearch.CATEGORYFILTER);
		gui.addFilterBox(availableLanguages, "Language:", FilteredSearch.LANGUAGEFILTER);
		gui.addFilterBox(availableRegions, "Region:", FilteredSearch.REGIONFILTER);
		gui.addFilterBox(availableDuration, "Duration:", FilteredSearch.VIDEODURATIONFILTER);
		gui.addFilterBox(availableVideoDefinitions, "Defintion:", FilteredSearch.VIDEODEFINITONFILTER);
		gui.addFilterBox(availableVideoTypes, "Type:", FilteredSearch.VIDEOTYPEFILTER);

		//ADDED
	//	HashMap<String, String> availableVideoDimension = (HashMap<String, String>) filterSearch.getAvailableVideoDimension();
	//	HashMap<String, String> availableVideoDefinition = (HashMap<String, String>) filterSearch.getAvailableVideoDefinition();
	//	HashMap<String, String> availableVideoOrder = (HashMap<String, String>) filterSearch.getAvailableVideoOrder();
		
		
	//	gui.addFilterBox(availableCategories, "Category", FilteredSearch.CATEGORYFILTER);
	//	gui.addFilterBox(availableLanguages, "Language", FilteredSearch.LANGUAGEFILTER);
	//	gui.addFilterBox(availableRegions, "Region", FilteredSearch.REGIONFILTER);
	//	gui.addFilterBox(availableDuration, "Duration", FilteredSearch.VIDEODURATIONFILTER);
	//	gui.addFilterBox(availableVideoTypes, "Video types", FilteredSearch.VIDEOTYPEFILTER);
	//	gui.addFilterBox(availableVideoDefinition, "Video definition", FilteredSearch.VIDEODEFINITIONFILTER);
	//	gui.addFilterBox(availableVideoDimension, "Video Dimension", FilteredSearch.VIDEODIMENSIONFILTER);
	//	gui.addFilterBox(availableVideoOrder, "Order by", FilteredSearch.VIDEOORDERBY);
		

		wait.setVisible(false);
		gui.pack();

	}
	
	
	/**
	 * Applying choosen filters and start the search. 
	 */
	public void preformFilteredSearch(String videoInfo,  String videoQuality, File filepath){
		videoInfoResult = new HashMap<String, Video>();
		gui.wipeStatWindow();
		
		this.filepath = filepath;
		this.videoInfo = videoInfo;
		
		
		

		
		NUMBER_OF_VIDEOS_RETRIVED = 0;
		
		HashMap<Integer, String> filtersApplied = gui.getSelectedFilters();
		filterSearch.init();
		for (Integer key : filtersApplied.keySet()) {
			System.out.println("AddingFilters");
			filterSearch.setFilter(key, filtersApplied.get(key));
		}
		wait =new DownloadProgressBar(this, NUMBER_OF_VIDEOS_TO_SEARCH,"Crawling YouTube", tg);
		search();

	}
	
	private void search() {
		RandomVideoIdGenerator randomGenerator = new RandomVideoIdGenerator();
	
	 	threadCount = NUMBER_OF_THREADS;
		for(int i = 0;i<NUMBER_OF_THREADS; i++){
			(new SearchThread(tg, "SearchThread_"+i, this)).start();
			
		}
		

	}
	
	/**
	 * This method should take a keyword and preform a search in a loop until we get the number of videos the user want. 
	 */

	public List<Video> preformKeywordSearch(String keyword){
		return (new Search()).getVideoLinkFromKeyWord(keyword);
	}
	
	public void displayresultFromKeySearch(JPanel resultcontain){
		gui.displayResult(resultcontain);
	}
	
	public List<Video> performKeywordSearchWithGeolocation(String keyword, String address, String distance){
		return (new GeolocationSearch()).searchVideoBaseOnLocation(keyword, address, distance);
	}
	
	
	/**
	 * When the thread is finished Searching the videos are saved and statistics are displayed
	 */
	public void finishedSearch(){
	
		tg.interrupt();
		//wait.setVisible(true);
		System.out.println("Compute stats");
		gui.getStatWindow().computeStatistics(videoInfoResult, filterSearch.getAvailableCategoriesReverse());		
	}

	public static void main(String[] args) {
		ManagementFilteredSearch fs = new ManagementFilteredSearch();
		//fs.preformKeyWordSearch("","",null,"hello");
	}
	
	class SearchThread extends Thread{
		ManagementFilteredSearch mng;
		VideoInfoExtracter infoExtracter;
		public SearchThread(ThreadGroup tg, String s, ManagementFilteredSearch mng){//, CountDownLatch startSignal, CountDownLatch doneSignal){
			super(tg,s);
			infoExtracter= new VideoInfoExtracter(videoInfo);
			this.mng = mng;
			infoExtracter.initDataContent();
			
			switch(videoInfo){
			case "JSON":
				infoExtracter.initOutputFile(filepath, "/videoInfo.json");
				break;
			case "XML":
				infoExtracter.initOutputFile(filepath, "/videoInfo.xml");
				break;
			case "CSV":
				infoExtracter.initOutputFile(filepath, "/videoInfo.csv");
				break;
			default:
			//	do nothing
				break;
			}
		}
		
		@Override
		public void run() {
			try {
				RandomVideoIdGenerator randomGenerator = new RandomVideoIdGenerator();
				loop:
				while(NUMBER_OF_VIDEOS_RETRIVED< NUMBER_OF_VIDEOS_TO_SEARCH){
					List<SearchResult> result = filterSearch.searchBy(randomGenerator.getNextRandom());
	
					innerLoop:
					for(SearchResult res : result){
						Thread.sleep(1);
						String videoId = res.getId().getVideoId();
						if(!videoInfoResult.containsKey(videoId)&&res.getId()!=null){
							NUMBER_OF_VIDEOS_RETRIVED++;
							System.out.println(res.getId().getVideoId());
						//	resultCache.add(res.getId().getVideoId());
							videoInfoResult.put(videoId, infoExtracter.getVideoInfo(videoId));		
						}	
					}
					wait.updateProgressBar(NUMBER_OF_VIDEOS_RETRIVED );	
//					if(NUMBER_OF_VIDEOS_RETRIVED%10 == 0){
//						System.out.println("Updating stats");
//						gui.wipeStatWindow();
//						gui.getStatWindow().computeStatistics(videoInfoResult, filterSearch.getAvailableCategoriesReverse());		
//					}
					
				}
			
				
				threadCount--;
				if(threadCount == 0 ){
					mng.finishedSearch();
					wait.setVisible(false);
				}
				
			} catch (InterruptedException v) {
				System.out.println("Thread Interrupted");
				//System.out.println(resultCache.size());
				threadCount--;
				if(threadCount == 0 ){
					mng.finishedSearch();
					wait.setVisible(false);
				}
			}

		}
	

	}

	
}
