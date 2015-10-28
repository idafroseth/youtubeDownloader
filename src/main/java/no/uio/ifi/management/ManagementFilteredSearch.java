package no.uio.ifi.management;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.JPanel;

import org.json.JSONObject;
import org.json.XML;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;

import no.uio.ifi.guis.DownloadProgressBar;
import no.uio.ifi.guis.FilteredSearchGui;
import no.uio.ifi.guis.Statistics;
import no.uio.ifi.guis.WaitDialog;
import no.uio.ifi.models.Export;
import no.uio.ifi.models.PageYouTube;
import no.uio.ifi.models.Export.ExportType;
import no.uio.ifi.models.downloader.CommentDensity;
import no.uio.ifi.models.downloader.CommentExtractor;
import no.uio.ifi.models.downloader.DownloadLinkExtractor;
import no.uio.ifi.models.downloader.VideoInfoExtracter;
import no.uio.ifi.models.search.CrawlerStefan;
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
	int NUMBER_OF_THREADS = 5;

	DownloadLinkExtractor dlExtractor;

	// ArrayList<String> resultCache = new ArrayList<String>();

	int threadCount = 0;
	HashMap<String, String> availableCategories;
	HashMap<String, String> availableLanguages;
	HashMap<String, String> availableRegions;
	HashMap<String, String> availableDuration;
	HashMap<String, String> availableVideoTypes;

	File filepath;
	long startTime;
	public String videoInfo;
	public String videoFormat;

	DownloadProgressBar wait;

	CountDownLatch latch;
	ArrayList<SearchResult> resultCache = new ArrayList<SearchResult>();
	ArrayList<Video> videoCache = new ArrayList<Video>();

	Map<String, Video> videoInfoResult;
	Map<String, PageYouTube> videoJsoupInfoResult;
	
	int count = 0;
	ThreadGroup tg = new ThreadGroup("Download");

	/**
	 * Retrieving all the filters and then display the window.
	 */
	public ManagementFilteredSearch() {

		gui.initWindow();

		// =====================================================================
		gui.mainResultPanel = new JPanel(new BorderLayout());
		if (gui.resultPanel != null)
			gui.contentPane.remove(gui.resultPanel);
		gui.contentPane.add(gui.mainResultPanel, "RESULT");
		gui.resultPanel = gui.mainResultPanel;

		WaitDialog wait = new WaitDialog("Downloading available filters from YouTube");
		HashMap<String, String> availableCategories = (HashMap<String, String>) filterSearch.getVideoCategories();
		HashMap<String, String> availableLanguages = (HashMap<String, String>) filterSearch.getAvailableLanguages();
		HashMap<String, String> availableRegions = (HashMap<String, String>) filterSearch.getAvailableRegions();
		HashMap<String, String> availableDuration = (HashMap<String, String>) filterSearch.getAvailableVideoDuration();
		HashMap<String, String> availableVideoTypes = (HashMap<String, String>) filterSearch.getAvailableVideoTypes();

		HashMap<String, String> availableVideoDefinitions = (HashMap<String, String>) filterSearch
				.getAvailableVideoDefinition();

		gui.addFilterBox(availableCategories, "Category:", FilteredSearch.CATEGORYFILTER);
		gui.addFilterBox(availableLanguages, "Language:", FilteredSearch.LANGUAGEFILTER);
		gui.addFilterBox(availableRegions, "Region:", FilteredSearch.REGIONFILTER);
		gui.addFilterBox(availableDuration, "Duration:", FilteredSearch.VIDEODURATIONFILTER);
		gui.addFilterBox(availableVideoDefinitions, "Defintion:", FilteredSearch.VIDEODEFINITONFILTER);
		gui.addFilterBox(availableVideoTypes, "Type:", FilteredSearch.VIDEOTYPEFILTER);
		wait.setVisible(false);
		gui.pack();

	}

	/**
	 * Applying chosen filters and start the search.
	 */
	public void preformFilteredSearch(String videoInfo, String videoQuality, File filepath, boolean apiSearchCheckbox) {
		startTime = System.currentTimeMillis();
		videoInfoResult = new HashMap<String, Video>();
		gui.wipeStatWindow();

		this.filepath = filepath;
		this.videoInfo = videoInfo;
		this.videoFormat = videoQuality;

		dlExtractor = new DownloadLinkExtractor(filepath == null ? null : filepath.getAbsolutePath());

		NUMBER_OF_VIDEOS_RETRIVED = 0;

		
		if(gui.isApiSearch()){
			apiSearch();
		}
		else{
			jsoupSearch();
		}
	}

	public void apiSearch(){
		HashMap<Integer, String> filtersApplied = gui.getSelectedFilters();
		filterSearch.init();
		for (Integer key : filtersApplied.keySet()) {
			System.out.println("AddingFilters");
			filterSearch.setFilter(key, filtersApplied.get(key));
		}
		wait = new DownloadProgressBar(this, NUMBER_OF_VIDEOS_TO_SEARCH, "Crawling YouTube", tg);
		
		if(gui.getKeyWordText().length() == 0 || gui.getKeyWordText() ==null) {
			System.out.println("Using 5 threads");
			setSearchThreadNumber(5);
		}
		else {
			setSearchThreadNumber(1);
		}
		threadCount = NUMBER_OF_THREADS;
		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			(new ApiSearchThread(tg, "SearchThread_" + i, this)).start();
		}
	}
	public void jsoupSearch(){
		wait = new DownloadProgressBar(this, NUMBER_OF_VIDEOS_TO_SEARCH, "Crawling YouTube", tg);
		(new JsoupSearchThread(tg, "SearchThread_" + 1, this)).start();
	}

	public List<Video> preformKeywordSearch(String keyword) {
		return (new Search()).getVideoLinkFromKeyWord(keyword);
	}

	public void displayresultFromKeySearch(JPanel resultcontain) {
		gui.displayResult(resultcontain);
	}

	public List<Video> performKeywordSearchWithGeolocation(String keyword, String address, String distance) {
		return (new GeolocationSearch()).searchVideoBaseOnLocation(keyword, address, distance);
	}

	/**
	 * When the thread is finished Searching the videos are saved and statistics
	 * are displayed
	 */
	public void finishedSearch() {
		tg.interrupt();
		System.out.println("Comment density is: " + CommentDensity.getVideoCount()/NUMBER_OF_VIDEOS_RETRIVED);
		// wait.setVisible(true);
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Estimated time is :" + estimatedTime / 1000 + " sec");
		gui.getStatWindow().computeStatistics(NUMBER_OF_VIDEOS_RETRIVED, videoInfoResult, filterSearch.getAvailableCategoriesReverse());
		gui.drawStatistics();
		gui.resultPartInGUI(videoCache);

	}
	public void finishedJsoupSearch(){

//		Statistics stat = new Statistics();
//		stat.addBarChart(myCrawler.getGenres(), "Generes");
//		stat.addBarChart(myCrawler.getYears(), "Years");
		gui.getStatWindow().computeStatistics(NUMBER_OF_VIDEOS_RETRIVED, videoJsoupInfoResult);
		gui.drawStatistics();
		wait.setVisible(false);
	}
	

	public static void main(String[] args) {
		ManagementFilteredSearch fs = new ManagementFilteredSearch();
		// fs.preformKeyWordSearch("","",null,"hello");
	}

	class ApiSearchThread extends Thread {
		ManagementFilteredSearch mng;
		VideoInfoExtracter infoExtracter;

		public ApiSearchThread(ThreadGroup tg, String s, ManagementFilteredSearch mng) {
			super(tg, s);
			infoExtracter = new VideoInfoExtracter(videoInfo);
			this.mng = mng;
			infoExtracter.initDataContent();

			switch (videoInfo) {
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
				// do nothing
				break;
			}
		}
		
		public void run() {
			try {
				RandomVideoIdGenerator randomGenerator = new RandomVideoIdGenerator();
				List<SearchResult> result;
				NUMBER_OF_VIDEOS_RETRIVED=0;

				loop: 
				while (NUMBER_OF_VIDEOS_RETRIVED < NUMBER_OF_VIDEOS_TO_SEARCH) {

					if (gui.getKeyWordText().length() != 0) {
						System.out.println("KEYWORDSEARCH");
						result = filterSearch.searchBy(gui.getKeyWordText());
					}
					else {

						String rnd = randomGenerator.getNextRandom();
						result = filterSearch.searchBy("" + "\"" + "watch?v=" + rnd + "\"");
						System.out.println("RANDOM SEARCH watch?v=" + rnd);
					}

					innerLoop: for (SearchResult res : result) {
						Thread.sleep(1);
						String videoId = res.getId().getVideoId();
					
						if (!videoInfoResult.containsKey(videoId) && res.getId() != null) {
							NUMBER_OF_VIDEOS_RETRIVED++;
							System.out.println(res.getId().getVideoId());
							Video v = infoExtracter.getVideoInfo(res, videoFormat, filepath, dlExtractor, true);
							videoInfoResult.put(videoId, v);
							videoCache.add(v);
							resultCache.add(res);	
						}
					}
					wait.updateProgressBar(NUMBER_OF_VIDEOS_RETRIVED);
					Thread.sleep(1);

				}

				threadCount--;
				if (threadCount == 0) {
					if(videoInfo.equals("JSON")){
						infoExtracter.closeJSONOutputFile();
					}
					mng.finishedSearch();
					wait.setVisible(false);
				}

			} catch (InterruptedException v) {
				System.out.println("Thread Interrupted");
				// System.out.println(resultCache.size());
				threadCount--;
				if (threadCount == 0) {
					if(videoInfo.equals("JSON")){
						infoExtracter.closeJSONOutputFile();
					}
					dlExtractor.stopCall();
					mng.finishedSearch();
					wait.setVisible(false);
				}
			}

		}

	}
		
	class JsoupSearchThread extends Thread {
		//gui.getJsoupGui().getStartPage();
		CrawlerStefan myCrawler;

		CommentExtractor commentExtractor = new  CommentExtractor(5L);
		ManagementFilteredSearch mng;
//			VideoInfoExtracter infoExtracter;

		public JsoupSearchThread(ThreadGroup tg, String s, ManagementFilteredSearch mng) {// ,
			super(tg, s);			
			
			
			this.mng = mng;
			myCrawler = new CrawlerStefan("https://www.youtube.com", mng);
		}

		@Override
		public void run() {
			try {
				switch (videoInfo) {
				case "JSON":
					Export.init(filepath, "/videoInfo.json");
					break;
				case "XML":
					Export.init(filepath, "/videoInfo.xml");
					System.out.println("init.xml");
					break;
				case "CSV":
					Export.init(filepath, "/videoInfo.csv");
					System.out.println("init.csv");
					break;
				default:
					// do nothing
					break;
				}

				//CrawlerStefan myCrawler = new CrawlerStefan("https://www.youtube.com", null);
				
				int count = 0;
				PageYouTube video = null;// = myCrawler.crawlPage("https://www.youtube.com");
				LinkedList<String> queue = new LinkedList<String>();
				queue.add("https://www.youtube.com");
				loop:
				while(count < 100){
				
						video = myCrawler.crawlPage(queue.removeFirst());
			
						if(video == null){
							System.out.println("video is null");
							continue loop;
						}
						int i = 0;
						List<String> links = video.getLinkedUrls();
						if(links != null){
							for(String url : links){
							
								if(!queue.contains(url)){
									queue.add(url);
								}
							}
						}
						
						
						//To extract the comments uncomment this:
//						String comments = commentExtractor.getTopLevelComments(video.getVideoID());
//						System.out.println(comments);
//						if(comments.length()>2){
//							JSONObject jsonObject = new JSONObject(comments.substring(1));
//							String xml = XML.toString(jsonObject, "video");
//						}
//						saveMetaData(xml);
						
						count++;
						switch (videoInfo) {
						case "JSON":
//							Export.writeJSON(video);
							break;
						case "XML":
							Export.writeXML(video);
							break;
						case "CSV":
							Export.writeCSV(video);
							break;
						default:
							// do nothing
							break;
						}
						
						//Since the dl expects a YTSearchResult we have to put the result in a YouTube.SearchResult object
						SearchResult res = new SearchResult();
						SearchResultSnippet srs = new SearchResultSnippet();
						srs.setTitle(video.getTitle());
						//add thumbnail
						Thumbnail tumb = new Thumbnail();
						tumb.setUrl(video.getLinkPreviewImage());
						ThumbnailDetails td = new ThumbnailDetails();
						td.setDefault(tumb);
						srs.setThumbnails(td);
						
						res.setSnippet(srs);
						ResourceId ri = new ResourceId();
						ri.setVideoId(video.getVideoID());
						res.setId(ri);
						
						
						dlExtractor.extract(res);
						
//						myCrawler.writeToFile(video, ExportType.XML);
//						System.out.println("Getting next values");
						
						NUMBER_OF_VIDEOS_RETRIVED++;
						wait.updateProgressBar(NUMBER_OF_VIDEOS_RETRIVED);
						Thread.sleep(1);
						
				}
				if(videoInfo=="XML"){
					Export.closeXML();
				}else{
					Export.closeCSV();
				}
				videoJsoupInfoResult = myCrawler.getCrawledPages();
				
				System.out.println("Finished CRAWLNG + " + videoJsoupInfoResult.size());
				mng.finishedJsoupSearch();
				wait.setVisible(false);
				

			} catch (InterruptedException v) {
				if(videoInfo=="XML"){
					Export.closeXML();
				}else{
					Export.closeCSV();
				}
				System.out.println("Thread Interrupted");
				Export.closeXML();
				videoJsoupInfoResult = myCrawler.getCrawledPages();
				System.out.println("Thread Interrupted with " + videoJsoupInfoResult.size() + " videos");

				mng.finishedJsoupSearch();
			}

		}

		
	}
	
	public void setSearchThreadNumber(int number) {
		NUMBER_OF_THREADS = number;
	}

}
