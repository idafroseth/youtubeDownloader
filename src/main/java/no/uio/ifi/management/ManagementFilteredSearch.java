package no.uio.ifi.management;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONObject;
import org.json.XML;

import java.awt.Toolkit;

import java.util.List;
import java.util.Map;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import no.uio.ifi.guis.DownloadProgressBar;
import no.uio.ifi.guis.FilteredSearchGui;
import no.uio.ifi.guis.Statistics;
import no.uio.ifi.guis.WaitDialog;
import no.uio.ifi.models.DeadEndException;
import no.uio.ifi.models.FilteredSearch;
import no.uio.ifi.models.RandomVideoIdGenerator;
import no.uio.ifi.models.VideoInfoExtracter;

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
	public int NUMBER_OF_VIDEOS_TO_SERACH = 10;
	public int NUMBER_OF_VIDEOS_RETRIVED = 0;
	int NUMBER_OF_THREADS=2;
	LinkedList<String> resultCache = new LinkedList<String>();
//	CountDownLatch latch = new CountDownLatch(0);
	int threadCount = 0;
	HashMap<String, String> availableCategories;
	HashMap<String, String> availableLanguages;
	HashMap<String, String> availableRegions;
	HashMap<String, String> availableDuration;
	HashMap<String, String> availableVideoTypes;
	/**
	 * Counting the number of updates of the chache queue, if there isn´t recieved a new
	 * video in the last 100 search, it will terminate and give a feedback to the user.  
	 */
	
	int deadEndValue = 100;
	int deadEndCount = 0;
	/**
	 * Retrieving all the filters and then display the window. 
	 */
	public ManagementFilteredSearch() {
		
		gui.initWindow();
		WaitDialog wait = new WaitDialog();
		HashMap<String, String> availableCategories = (HashMap<String, String>) filterSearch.getVideoCategories();
		HashMap<String, String> availableLanguages = (HashMap<String, String>) filterSearch.getAvailableLanguages();
		HashMap<String, String> availableRegions = (HashMap<String, String>) filterSearch.getAvailableRegions();
		HashMap<String, String> availableDuration = (HashMap<String, String>) filterSearch.getAvailableVideoDuration();
		HashMap<String, String> availableVideoTypes = (HashMap<String, String>) filterSearch.getAvailableVideoTypes();
		
		gui.addFilterBox(availableCategories, "Category", FilteredSearch.CATEGORYFILTER);
		gui.addFilterBox(availableLanguages, "Language", FilteredSearch.LANGUAGEFILTER);
		gui.addFilterBox(availableRegions, "Region", FilteredSearch.REGIONFILTER);
		gui.addFilterBox(availableDuration, "Duration", FilteredSearch.VIDEODURATIONFILTER);
		gui.addFilterBox(availableVideoTypes, "Video types", FilteredSearch.VIDEOTYPEFILTER);
		wait.setVisible(false);
		gui.pack();
	}
	
	
	/**
	 * Applying choosing filter and start the search. 
	 */
	public void preformSearch() {
//		Thread downloadBar = new DownloadProgressBar(NUMBER_OF_VIDEOS_TO_SERACH, "Crawling YouTube");
//		downloadBar.run();
//		(new BarThread("Crawler")).run();
		
		HashMap<Integer, String> filtersApplied = gui.getSelectedFilters();
		filterSearch.init();
		for (Integer key : filtersApplied.keySet()) {
			System.out.println("AddingFilters");
			filterSearch.setFilter(key, filtersApplied.get(key));
		}
		
//		new Thread(new Runnable(){
			
		DownloadProgressBar dpb = new DownloadProgressBar(NUMBER_OF_VIDEOS_TO_SERACH, "Crawling YouTube");
		
		threadCount = NUMBER_OF_THREADS;
//		latch = new CountDownLatch(NUMBER_OF_THREADS);
		for(int i = 0;i<NUMBER_OF_THREADS; i++){
			(new SearchThread("SearchThread_"+i,dpb)).run();
		}
//		try {
////			latch.await();
//			
//		} catch (InterruptedException e) {	
//			// TODO Auto-generated catch block
//			System.out.println("Somthing happend when waiting for thread");
//			e.printStackTrace();
//		}
		
	}
	public void finishedSearch(){
		System.out.println("Videos in cache " +resultCache.size());
		Map<String, Video> videoInfoResult = (new VideoInfoExtracter()).getVideoContent(resultCache);
		gui.newResult(videoInfoResult);
		getStatistics(videoInfoResult);		
	}
	public void getStatistics(Map<String, Video> videoInfoResult){
		//Likes values 
		HashMap<String, BigInteger> likesStat = new HashMap<String, BigInteger>(2);
		likesStat.put("Likes", new BigInteger("0"));
		likesStat.put("Dislikes", new BigInteger("0"));
		int likesVideos = 0;
		
		Map<String, Integer> categoryStats = new HashMap<String, Integer>();
		Map<String, Integer> yearStats = new HashMap<String, Integer>();
		
		HashMap<String, BigInteger> countStat = new HashMap<String, BigInteger>(2);
		countStat.put("Views", new BigInteger("0"));
		//countStat.put("Favorite", new BigInteger("0"));
		//countStat.put("Comments", new BigInteger("0"));
		BigInteger viewCount = new BigInteger("0");
		BigInteger favouritesCount = new BigInteger("0");
		BigInteger commentsCount = new BigInteger("0");
		int countStatVideos = 0;
		
		
		for(Video video : videoInfoResult.values()){
			//Like statistics
			if(video.getStatistics()!=null && video.getStatistics().getLikeCount()!= null){
				likesStat.replace("Likes", likesStat.get("Likes").add(video.getStatistics().getLikeCount()));
				likesStat.replace("Dislikes", likesStat.get("Dislikes").add(video.getStatistics().getDislikeCount()));
				likesVideos++;
			}
			if(video.getStatistics()!=null){
				if(video.getStatistics().getViewCount() != null){
					
					countStat.replace("Views", countStat.get("Views").add(video.getStatistics().getViewCount()));
				}if(video.getStatistics().getFavoriteCount() != null){
		//		countStat.replace("Favorite", countStat.get("Favorite").add(video.getStatistics().getFavoriteCount()));
				}if(video.getStatistics().getCommentCount() != null){
		// 			countStat.replace("Comments", countStat.get("Comments").add(video.getStatistics().getCommentCount()));
					countStatVideos++;
				}
			}
			
			
			//Category statistics
			String category =  filterSearch.getAvailableCategoriesReverse().get(video.getSnippet().getCategoryId());
			if(categoryStats.containsKey(category)){
				categoryStats.replace(category,categoryStats.get(category)+1);
			}else{
				categoryStats.put(category,1);
			}
			
			//Year statistics
			SimpleDateFormat df = new SimpleDateFormat("yyyy");
			String year =df.format(new Date(video.getSnippet().getPublishedAt().getValue()));
			if(yearStats.containsKey(year)){
				//We must update
				yearStats.replace(year,yearStats.get(year)+1);
			}else{
				yearStats.put(year,1);
			}
		
			
			video.getStatistics().getLikeCount();
		}
		Statistics stat = gui.getStatWindow();
		stat.addBarChart(likesStat, "Likes", likesVideos);
		stat.addBarChart(countStat, "Count", countStatVideos);
		stat.addBarChart(categoryStats, "Categories");
		stat.addBarChart(yearStats, "Years");

	}
	/**
	 * This convert a video to xml format
	 * @param video one object of YouTube video
	 * @return the video in xml formats
	 */
	public String convertToXML(Video video){
		return XML.toString(new JSONObject(video));
	}
	public void deadEnd(){
		System.out.println("Dead END Videos in cache " +resultCache.size());
	}
	public int getDeadEndValue(){
		return this.deadEndValue;
	}
	public void setDeadEndValue(int newDeadEndValue){
		this.deadEndValue = newDeadEndValue;
	}
	public static void main(String[] args) {
		ManagementFilteredSearch fs = new ManagementFilteredSearch();
	}
	
	class SearchThread extends Thread {

		DownloadProgressBar progressBar;
		//ManagementAllRandom mng;
		public SearchThread(String s, DownloadProgressBar progressBar){//, ManagementAllRandom mng) {
			super(s);
			this.progressBar = progressBar;
			//this.mng = mng;
		}
		
		@Override
		public void run() {
			try {
				RandomVideoIdGenerator randomGenerator = new RandomVideoIdGenerator();
				
				
				//Here one thread shoul handle the gui and another thread should handle the search, or multiple threads. 
				
				NUMBER_OF_VIDEOS_RETRIVED = 0;
				while(NUMBER_OF_VIDEOS_RETRIVED< NUMBER_OF_VIDEOS_TO_SERACH){
					List<SearchResult> result = filterSearch.searchBy(randomGenerator.getNextRandom());
					System.out.println(result.size());
					if(deadEndCount > deadEndValue){
						System.out.println("DEAD END");
						throw new DeadEndException();	
					}
					if( result.size() == 0){
						deadEndCount++;
						continue;
					}
					
					loop:
					for(SearchResult res : result){
						if(resultCache.contains(res.getId().getVideoId())){
							deadEndCount++;
							continue loop;
						}
						deadEndCount = 0;
						System.out.print(NUMBER_OF_VIDEOS_RETRIVED + ": ");
						System.out.println(res.getId());
						System.out.println(res);
						NUMBER_OF_VIDEOS_RETRIVED++;
						resultCache.add(res.getId().getVideoId());
						
					}
					progressBar.updateProgressBar(NUMBER_OF_VIDEOS_RETRIVED);
				}
				
		  		Thread.sleep(1);
		  	//	latch.countDown();
				System.out.println("thread stopped.");
				
				System.out.println("threadCount: " + threadCount);
				if(threadCount==NUMBER_OF_THREADS){
					threadCount--;
					finishedSearch();
				}
				threadCount--;
				System.out.println();	
				this.interrupt();
			} catch (InterruptedException v) {
				System.out.println("Thread error");
				System.out.println(v);
			} catch (DeadEndException e) {
				//Should change this
//				latch.countDown();
				new DownloadProgressBar(0, "DEAD END EXCEPTION" );
				threadCount--;
				if(threadCount<NUMBER_OF_THREADS){
					deadEnd();
				}
				e.printStackTrace();
				this.interrupt();
			}
		}
	}
	class BarThread implements Runnable {
		//ManagementAllRandom mng;
		DownloadProgressBar updateProgressBar = new DownloadProgressBar(NUMBER_OF_VIDEOS_TO_SERACH,"Crawling");
		public BarThread(String s){//, ManagementAllRandom mng) {
	//		super(s);
			//this.mng = mng;
		}
		@Override
		public void run() {
			try {
				while(NUMBER_OF_VIDEOS_RETRIVED < NUMBER_OF_VIDEOS_TO_SERACH ){
					updateProgressBar.updateProgressBar(NUMBER_OF_VIDEOS_RETRIVED);
				}
				Thread.sleep(1);
				System.out.println("thread stopped.");
				} catch (InterruptedException v) {
					System.out.println("Thread error");
					System.out.println(v);
				}
		}
	}
	
}
