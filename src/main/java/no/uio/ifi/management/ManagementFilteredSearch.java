package no.uio.ifi.management;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONObject;
import org.json.XML;
import java.io.File;
import java.util.List;
import java.util.Map;

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
 * @author Ida Marie Frøseth
 *
 */
public class ManagementFilteredSearch {
	FilteredSearch filterSearch = new FilteredSearch();
	FilteredSearchGui gui = new FilteredSearchGui(this);
	public int NUMBER_OF_VIDEOS_TO_SEARCH = 100000;
	public int NUMBER_OF_VIDEOS_RETRIVED = 0;
	int NUMBER_OF_THREADS=5;
	LinkedList<String> resultCache = new LinkedList<String>();
//	CountDownLatch latch = new CountDownLatch(0);
	int threadCount = 0;
	HashMap<String, String> availableCategories;
	HashMap<String, String> availableLanguages;
	HashMap<String, String> availableRegions;
	HashMap<String, String> availableDuration;
	HashMap<String, String> availableVideoTypes;

	File filepath;

	String videoInfo;
	
	DownloadProgressBar wait;

	LinkedList<Thread> downloadThreads = new LinkedList<Thread>();
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
		//gui.addFilterBox(availableVideoDefinitions, "Defintion:", FilteredSearch.VIDEODEFINITONFILTER);
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
		wait =new DownloadProgressBar(NUMBER_OF_VIDEOS_TO_SEARCH,"Crawling YouTube");
		search();

/*
			WaitDialog wait =new WaitDialog("Crawling YouTube");
	
			@Override
			protected Integer doInBackground() throws Exception {
				// TODO Auto-generated method stub
			 	threadCount = NUMBER_OF_THREADS;
				for(int i = 0;i<NUMBER_OF_THREADS; i++){
					(new SearchThread("SearchThread_"+i)).run();
				}
				while(NUMBER_OF_VIDEOS_RETRIVED< NUMBER_OF_VIDEOS_TO_SEARCH){
					wait.appendText(NUMBER_OF_VIDEOS_RETRIVED);
					Thread.sleep(1);
				}
				return null;
			}
			
		};
		worker.execute();
*/
	}
	
	private void search(){
		RandomVideoIdGenerator randomGenerator = new RandomVideoIdGenerator();	
	 	threadCount = NUMBER_OF_THREADS;
		for(int i = 0;i<NUMBER_OF_THREADS; i++){
			downloadThreads.add(new SearchThread("SearchThread_"+i, this));
		
		}
		for(Thread th : downloadThreads){
			th.start();
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
		for(Thread th : downloadThreads){
			th.interrupt();
		}
		System.out.println("Videos in cache " +resultCache.size());

		
		//Map<String, Video> videoInfoResult = (new VideoInfoExtracter()).getVideoContent(resultCache);
		//gui.newResult(videoInfoResult);

		Map<String, Video> videoInfoResult = null;
		switch(videoInfo){
		case "JSON":
			videoInfoResult  = (new VideoInfoExtracter()).saveJsonVideoContent(resultCache, filepath);
			break;
		case "XML":
			videoInfoResult  = (new VideoInfoExtracter()).saveXmlVideoContent(resultCache, filepath);
			break;
		case "CSV":
			videoInfoResult  = (new VideoInfoExtracter()).saveCSVVideoContent(resultCache, filepath);
			break;
		default:
			videoInfoResult  = (new VideoInfoExtracter()).getVideoMetadata(resultCache);
			break;
		}
		
		//gui.newResult(videoInfoResult);

		gui.getStatWindow().computeStatistics(videoInfoResult, filterSearch.getAvailableCategoriesReverse());		
	}

	/**
	 * This convert a video to xml format
	 * @param video one object of YouTube video
	 * @return the video in xml formats
	 */
	public String convertToXML(Video video){
		return XML.toString(new JSONObject(video));
	}
	public static void main(String[] args) {
		ManagementFilteredSearch fs = new ManagementFilteredSearch();
		//fs.preformKeyWordSearch("","",null,"hello");
	}
	
	class SearchThread extends Thread{
		ManagementFilteredSearch mng;
		public SearchThread(String s, ManagementFilteredSearch mng){
			super(s);
			this.mng = mng;
		}
		
		@Override
		public void run() {
			try {
				RandomVideoIdGenerator randomGenerator = new RandomVideoIdGenerator();
				
				
				//Here one thread shoul handle the gui and another thread should handle the search, or multiple threads. 
				
				while(NUMBER_OF_VIDEOS_RETRIVED< NUMBER_OF_VIDEOS_TO_SEARCH){
					List<SearchResult> result = filterSearch.searchBy(randomGenerator.getNextRandom());
					
					for(SearchResult res : result){
						Thread.sleep(1);
						if(!resultCache.contains(res.getId().getVideoId())){
							NUMBER_OF_VIDEOS_RETRIVED++;
							resultCache.add(res.getId().getVideoId());
							System.out.println(NUMBER_OF_VIDEOS_RETRIVED + ": "+res.getId());
						}	
					}
					wait.updateProgressBar(NUMBER_OF_VIDEOS_RETRIVED );	
				}
				wait.setVisible(false);
				mng.finishedSearch();
				
				System.out.println("thread stopped.");
				//interrupt();
				
			} catch (InterruptedException v) {
				System.out.println("Thread Interrupted");
			}
		}

	}

	
}
