package no.uio.ifi.management;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Toolkit;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.ResourceId;
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
		
//			public void run(){
//				while(NUMBER_OF_VIDEOS_TO_SERACH>NUMBER_OF_VIDEOS_RETRIVED){
//					
//					System.out.println(NUMBER_OF_VIDEOS_RETRIVED);
//	
////					try {
////						Thread.sleep(1);
////					} catch (InterruptedException e1) {
////						// TODO Auto-generated catch block
////						e1.printStackTrace();
////					}
//				}
//			}
//		});
		//NUMBER_OF_THREADS = 3;
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
		(new VideoInfoExtracter()).getVideoContent(resultCache);
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
