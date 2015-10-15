package no.uio.ifi.models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Export of crawled YouTube pages in different formats. 
 * 
 * @author Stefan Leicht
 * @version 0.2
 *
 */
public class Export {
	private static final String fileName = "Videos";
	private static PrintWriter pw;
	
	public enum ExportType{
		XML,
		CSV,
		JASON
	}
	
//	static{
//		Writer fw = null;
//		try {
//			fw = new FileWriter(fileName + "." + ExportType.XML.name().toLowerCase());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		Writer bw = new BufferedWriter(fw);
//		pw = new PrintWriter(bw);
//		
//		pw.println("<?xml version=\"1.0\"?>");
//		pw.println("\t<videoList>");
//	}
	
	public static void toXML() {
		Writer fw = null;
		try {
			fw = new FileWriter("res/" + fileName + "." + ExportType.XML.name().toLowerCase());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Writer bw = new BufferedWriter(fw);
		pw = new PrintWriter(bw);
		
		pw.println("<?xml version=\"1.0\"?>");
		pw.println("<videoList>");
	}

	public static void writeXML(PageYouTube YTPage) {
		pw.println("\t<video>");
		pw.println("\t\t<ID>" + YTPage.getVideoID() + "</ID>");
		pw.println("\t\t<title>" + YTPage.getTitle() + "</title>");
		pw.println("\t\t<author>" + YTPage.getAuthor() + "</author>");
		pw.println("\t\t<length>" + YTPage.getLength() + "</length>");
		pw.println("\t\t<familyFriendly>" + YTPage.getFamilyFriendly() + "</familyFriendly>");
		pw.println("\t\t<regionsAllowed>" + YTPage.getRegionsAllowed() + "</regionsAllowed>");
		pw.println("\t\t<keywords>");
		for(String k : YTPage.getKeywords())
			pw.println("\t\t\t<keyword>" + k + "</keyword>");
		pw.println("\t\t</keywords>");
		pw.println("\t\t<views>" + YTPage.getViews() + "</views>");
		pw.println("\t\t<datePublished>" + YTPage.getDatePublished() + "</datePublished>");
		pw.println("\t\t<genre>" + YTPage.getGenre() + "</genre>");
		pw.println("\t\t<linkPreviewImage>" + YTPage.getLinkPreviewImage() + "</linkPreviewImage>");
		pw.println("\t\t<likes>" + YTPage.getLikes() + "</likes>");
		pw.println("\t\t<dislikes>" + YTPage.getDislikes() + "</dislikes>");
		pw.println("\t\t<description>" + YTPage.getDescription() + "</description>");
		pw.println("\t\t<linkedVideos>");
		for(String l : YTPage.getLinkedUrls())
			pw.println("\t\t\t<linkedVideo>" + l + "</linkedVideo");
		pw.println("\t\t</linkedVideos>");
		pw.println("\t\t<comments>");
//		pw.println("\t\t\t<comment>" + YTPage.getDescription() + "</description>");
		pw.println("\t\t</comments>");
		pw.println("\t</video>");
	}
	
	public static void closeXML(){
		pw.print("</videoList>");
		pw.close();
	}
	
}
