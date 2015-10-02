package no.uio.ifi.models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class Crawler {
	public Crawler(String startUrl){
		this.startUrl = startUrl;
	}
	String startUrl;
	int count = 0;
	int totalCount = 0;
	public static void main(String[] args) throws IOException{
		Crawler myCrawler = new Crawler("https://www.youtube.com");
		myCrawler.crawlPage("https://www.youtube.com/watch?v=lpdfBSb-8i8");
	}
	
	public void crawlPage(String url){
		try {
			Document webSite = Jsoup.connect(url).get();
			
			Elements metadata = webSite.select("meta[itemprop]");
			Elements linkedUrls = webSite.select("a[href]"); //.attr("watch?v");
			
			
			int randomStop = ThreadLocalRandom.current().nextInt(0, 20);
			File file = new File("output.txt");
			BufferedWriter writer= new BufferedWriter(new FileWriter(file));
		
			writer.write(webSite.toString());
			
			writer.flush();
	//		System.out.println(webSite);
			for(Element meta :  metadata){
		//		System.out.println(meta);
			}
//			for(Element link : linkedUrls){	
//				if(link.toString().contains("watch?v")){
//					count++;
//					if(count == randomStop){
//						String crawlTo = startUrl + link.attr("href");
//						crawlPage(crawlTo);
//						System.out.println("Crawling "+ crawlTo);
//					}
//				}
//			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception");
			e.printStackTrace();
			System.exit(0);
		}
	}
	private String randomUrlGenerator(){
		String alfabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		String randomValue = "";
		for(int i = 0; i<11; i++){
			randomValue +=alfabet.charAt(random.nextInt(alfabet.length()));
		}
	//	System.out.println(randomValue);
		return randomValue;
	}
}
