package group1.dashYoutube;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	public Crawler(String startUrl){
		this.startUrl = startUrl;
	}
	String startUrl;
	public static void main(String[] args) throws IOException{
		Crawler myCrawler = new Crawler("https://www.youtube.com");
		myCrawler.crawlPage("https://www.youtube.com");
	}
	
	public void crawlPage(String url){
		try {
			Document webSite = Jsoup.connect(url).get();
			Elements linkedUrls = webSite.select("a[href]");
			int count = 0;
			int randomStop = ThreadLocalRandom.current().nextInt(0, 40);
		//	System.out.println(webSite);
		//	System.out.println(linkedUrls.size());
			for(Element link : linkedUrls){	
			//	System.out.println(link);
				if(link.toString().contains("watch?v")){
					count++;
					if(count == randomStop){
					//	System.out.println(link);
						System.out.println("Crawl to: " + url + link.attr("href") + " with count " + count);
						crawlPage(startUrl + link.attr("href"));
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
