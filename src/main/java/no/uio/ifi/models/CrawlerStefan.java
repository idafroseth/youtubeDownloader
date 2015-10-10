package no.uio.ifi.models;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author Stefan Leicht
 * @version 0.11
 *
 * 
 */
public class CrawlerStefan {
	private String startUrl;
	private Writer fw = null;
	private long startTime;
	private long endTime;
	private static final int numberVideosToCrawl = 2000;
	private ArrayList<String> arr;
	private Map<String, Integer> genres = new HashMap<String, Integer>();
	private Map<String, Integer> authors = new HashMap<String, Integer>();
	private Map<String, Integer> dates = new HashMap<String, Integer>();
	
	public CrawlerStefan(String startUrl) {
		this.startUrl = startUrl;
		arr = new ArrayList<String>();
	}

	public static void main(String[] args){
		CrawlerStefan myCrawler = new CrawlerStefan("https://www.youtube.com");
		myCrawler.crawl();
	}

	public void crawl() {
		Document webSite = null;
		startTime = System.nanoTime();
		try {
			webSite = Jsoup.connect(startUrl).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fw = new FileWriter("res/YouTubeLinks.txt");
		} catch (IOException e) {
			System.err.println("Could not create file");
		}
		Elements linkedUrls = webSite.select("a[href]");
		for (Element link : linkedUrls) {
			if (link.toString().contains("watch?v")) {
				if (!arr.contains(startUrl + link.attr("href"))) {
					arr.add(startUrl + link.attr("href"));
					try {
						fw.write(startUrl + link.attr("href"));
						fw.append(System.getProperty("line.separator")); // e.g.// "\n"
					} catch (IOException e) {
						System.err.println("Could not write into file");
					}
				}
			}
		}
		crawlLinks();
	}

	private void crawlLinks() {
		for (int i = 0; i < numberVideosToCrawl; i++) {
			System.out.println(i + ". " + arr.get(i));
			crawlPage(arr.get(i));
		}
		endTime = System.nanoTime();
		try {
			fw.append(System.getProperty("line.separator"));
			fw.write("Number of different YouTube Videos in this list: " + arr.size());
			fw.append(System.getProperty("line.separator"));
			fw.write("It contains " + genres.size() + " different genres and how often they occure:");
			fw.append(System.getProperty("line.separator"));
//			for(String s : genres.keySet()){
//				fw.write( s + "=" + genres.get(s));
//				fw.append(System.getProperty("line.separator"));
//			}
			fw.write("It contains " + authors.size() + " different authors and how often they occure:");
			fw.append(System.getProperty("line.separator"));
//			for(String s : authors.keySet()){
//				fw.write( s + "=" + authors.get(s));
//				fw.append(System.getProperty("line.separator"));
//			}
			fw.write("It contains " + dates.size() + " different publishing dates and how often they occure:");
			fw.append(System.getProperty("line.separator"));
//			for(String s : dates.keySet()){
//				fw.write( s + "=" + dates.get(s));
//				fw.append(System.getProperty("line.separator"));
//			}
			fw.write("Time needed (in seconds) to crawl: " + ((endTime - startTime) / Math.pow(10, 9)));
			fw.append(System.getProperty("line.separator"));
			fw.write("Number of crawled Videos: " + numberVideosToCrawl);
			fw.close();
		} catch (IOException e) {
			System.out.println("close problem");
			e.printStackTrace();
		}finally{
			try {
				fw.close();
			} catch (IOException e) {
				System.out.println("problem with close");
				e.printStackTrace();
			}
		}
	}

	private void crawlPage(String url) {
	  	Document webSite = null;
		boolean connectionError = false;
		do{
			try {
				if(connectionError) Thread.sleep(50);
				webSite = Jsoup.connect(url).get();
				connectionError = false;
			} catch (IOException e) {
				connectionError = true;
				System.out.println("Connecting problem to " + url);
			} catch (InterruptedException e) {
				connectionError = false;
				System.out.println("Thread sleep problem");
			}
		}while(connectionError);
			
//		List<String> linkedVideos = getLinkedVideos(webSite.select("a[href]"));
		Elements linkedUrls = webSite.select("a[href]");
		Elements keywords = webSite.select("meta[property=og:video:tag]");
		String title = webSite.select("meta[itemprop=name]").attr("content");
		String videoId = webSite.select("meta[itemprop=videoId]").attr("content");
		boolean familyFriendly = (webSite.select("meta[itemprop=isFamilyFriendly]").attr("content").equals(true) ? true : false);
		String regionsAllowed = webSite.select("meta[itemprop=regionsAllowed]").attr("content");
		String views = placeDotInNumber(webSite.select("meta[itemprop=interactionCount]").attr("content"));
		String datePublished = webSite.select("meta[itemprop=datePublished]").attr("content");
		String genre = webSite.select("meta[itemprop=genre]").attr("content");
		String linkPreviewImage = webSite.select("meta[property=og:image]").attr("content");
		Elements likesRAW = webSite.select("button[class=yt-uix-button yt-uix-button-size-default yt-uix-button-opacity yt-uix-button-has-icon no-icon-markup like-button-renderer-like-button like-button-renderer-like-button-unclicked yt-uix-clickcard-target   yt-uix-tooltip]");
		String likes = "";
		if(!likesRAW.isEmpty())
			likes = likesRAW.get(0).childNode(0).childNode(0).toString();
		//dislikes need -1
		Elements dislikesRAW = webSite.select("button[class=yt-uix-button yt-uix-button-size-default yt-uix-button-opacity yt-uix-button-has-icon no-icon-markup like-button-renderer-dislike-button like-button-renderer-dislike-button-clicked yt-uix-button-toggled  hid yt-uix-tooltip]");
		String dislikes = "";
		if(!dislikesRAW.isEmpty())
			dislikes = disLikes(dislikesRAW.get(0).childNode(0).childNode(0).toString());
		List<String> description = getDescription(webSite.select("p[id=eow-description]"));
		String author = null;
		try{
		author = findPattern("author\":\".*?\",", webSite.body().toString());
		author = author.substring(9, author.length()-2);
		}catch(IllegalStateException e){
			author = findPattern("<a href=\"[[/user/]|[/channel/]].*?alt=\".*?\"", webSite.body().toString());
			author = findPattern("alt=\".*?\"", author);
			author = author.substring(5, author.length()-1);
		}
		//lengthSeconds -1
//		String length = convertLength(findPattern("length_seconds\":\".*?\",", webSite.body().toString()));
		String length = convertLength(webSite.select("meta[itemprop=duration]").attr("content"));
		
		if(genres.containsKey(genre)){
			genres.put(genre, genres.get(genre) + 1);
		}else{
			genres.put(genre, 1);
		}
		if(authors.containsKey(author)){
			authors.put(author, authors.get(author) + 1);
		}else{
			authors.put(author, 1);
		}
		if(dates.containsKey(datePublished)){
			dates.put(datePublished, dates.get(datePublished) + 1);
		}else{
			dates.put(datePublished, 1);
		}
		
		for (Element link : linkedUrls) {
			if (link.toString().contains("watch?v")) {
				try {
					if(link.attr("href").contains("http")){
						if(link.attr("href").contains("https")){
							if (!arr.contains(link.attr("href"))) {
								arr.add(link.attr("href"));
								fw.write(link.attr("href"));
								fw.append(System.getProperty("line.separator")); // e.g. // "\n"
							}
						}else{
							String linkNew = new StringBuffer(link.attr("href")).insert(4, "s").toString();
							if (!arr.contains(linkNew)) {
								arr.add(linkNew);
								fw.write(linkNew);
								fw.append(System.getProperty("line.separator")); // e.g. // "\n"
							}
						}
					}else if(!arr.contains(startUrl + link.attr("href"))) {
						arr.add(startUrl + link.attr("href"));
						fw.write(startUrl + link.attr("href"));
						fw.append(System.getProperty("line.separator")); // e.g. // "\n"
					}
				} catch (IOException e) {
					System.err.println("Could not write");
				}
			}
		}
	}
	
	private List<String> getLinkedVideos(Elements linkedUrls){
		List<String> linkedVideos = new ArrayList<String>();
		
		for (Element link : linkedUrls) {
			if (link.toString().contains("watch?v")) {
				if(link.attr("href").contains("http")){
					if(link.attr("href").contains("https")){
						if (!arr.contains(link.attr("href"))) {
							arr.add(link.attr("href"));
							linkedVideos.add(link.attr("href"));
						}
					}else{
						String linkNew = new StringBuffer(link.attr("href")).insert(4, "s").toString();
						if (!arr.contains(linkNew)) {
							arr.add(linkNew);
							linkedVideos.add(link.attr(linkNew));
						}
					}
				}else if(!arr.contains(startUrl + link.attr("href"))) {
					arr.add(startUrl + link.attr("href"));
					linkedVideos.add(startUrl + link.attr("href"));
				}
			}
		}
		return linkedVideos;
	}
	
	private List<String> getDescription(Elements descriptionRAW){
		List<String> des = new ArrayList<String>();
		String s = "";
		
		for(int i = 0; i < descriptionRAW.get(0).childNodes().size(); i++){
			//to get only text in the description
			if(!descriptionRAW.get(0).childNode(i).hasAttr("href")){
				if(descriptionRAW.get(0).childNode(i).toString().equals("<br />")){
					des.add(s);
					s = new String("");
				}else if(!descriptionRAW.get(0).childNode(i).toString().matches("\\p{Blank}+")){
					s += descriptionRAW.get(0).childNode(i).toString();
				}
			//to get the hyperLinks in the description
			}else{
				//to get the linked time in the video
				if(descriptionRAW.get(0).childNode(i).attr("href").equals("#")){
					s += descriptionRAW.get(0).childNode(i).childNode(0).toString();
				}else{
					s += descriptionRAW.get(0).childNode(i).attr("href");
				}
			}
		}
		if(!s.equals("")) des.add(s);
		
		return des;
	}
	
	private String findPattern(String pattern, CharSequence string){
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(string);
		m.find();
		return m.group();
	}
	
	private String convertLength(String lengthRAW){
		int seconds = Integer.parseInt(findPattern("\\d+", findPattern("M\\d+", lengthRAW))) - 1;
		int minutes = Integer.parseInt(findPattern("\\d+", lengthRAW));
		String time = "";
		
		if(minutes/60 != 0){
			time = time + minutes/60 + ":";
			if(minutes%60 > 9){
				time = time + minutes%60 + ":";
			}else{
				time = time + "0" + minutes%60 + ":";
			}
		}else{
			time = time + minutes%60 + ":";
		}
		if(seconds%60 > 9){
			time = time + seconds%60;
		}else{
			time = time + "0" + seconds%60;
		}
		return time;
	}
	
	private String disLikes(String disLikesOld){
		String[] disArr = disLikesOld.split("\\.");
		
		String disLikesNew = "";
		for(String s : disArr){
			disLikesNew += s;
		}
		
		int d = Integer.parseInt(disLikesNew);
		if(d > 0) d-=1;
		disLikesNew = Integer.toString(d);
		
		return placeDotInNumber(disLikesNew);
	}
	
	private String placeDotInNumber(String number){
		StringBuffer sb = new StringBuffer(number);
		int j = 0;
		for(int i = number.length(); i > 0; i--){
			if(j%3 == 0 && i != number.length()){
				sb.insert(i, ".");
			}
			j++;
		}
		return sb.toString();
	}
	
}
