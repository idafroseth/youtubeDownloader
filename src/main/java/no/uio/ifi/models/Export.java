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
 * @version 0.25
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
		pw.print("\t\t<description>");
		for(String d : YTPage.getDescription()) pw.print(d);
		pw.println("</description>");
		pw.println("\t\t<linkedVideos>");
		for(String l : YTPage.getLinkedUrls())
			pw.println("\t\t\t<linkedVideo>" + l + "</linkedVideo");
		pw.println("\t\t</linkedVideos>");
		pw.println("\t\t<comments>");
//		for(String c : YTPage.getComments){
//			pw.println("\t\t\t<comment>");
//			pw.println("\t\t\t\t<author>" + YTPage.getAuthor() + "</author>");
//			pw.println("\t\t\t\t<body>" + YTPage.getAuthor() + "</body>");
//			if(YTPage.CommenThread.size() != 0){
//				pw.println("\t\t\t\t<commentThread>");
//				for(String ct : YTPage.getCommentThread){
//					pw.println("\t\t\t\t\t<comment>");
//					pw.println("\t\t\t\t\t\t<author>" + YTPage.getAuthor() + "</author>");
//					pw.println("\t\t\t\t\t\t<body>" + YTPage.getBody() + "</body>");
//					pw.println("\t\t\t\t\t</comment>");
//				}
//				pw.println("\t\t\t\t</commentThread>");
//			}
//			pw.println("\t\t\t</comment>");
//		}
		pw.println("\t\t</comments>");
		pw.println("\t</video>");
	}
	
	public static void closeXML(){
		pw.print("</videoList>");
		pw.close();
	}
	
	public static void toCSV() {
		Writer fw = null;
		try {
			fw = new FileWriter("res/" + fileName + "." + ExportType.CSV.name().toLowerCase());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Writer bw = new BufferedWriter(fw);
		pw = new PrintWriter(bw);
		
		pw.println("ID;title;author;length;familyFriendly;regionsAllowed;keywords;views;datePublished;genre;linkPreviewImage;likes;dislikes;description;linkedVideos");
	}
	
	public static void writeCSV(PageYouTube YTPage) {
		pw.print(YTPage.getVideoID() + ";");
		pw.print("\"" + YTPage.getTitle().replace("\"", "\"\"") + "\";");
		pw.print("\"" + YTPage.getAuthor().replace("\"", "\"\"") + "\";");
		pw.print(YTPage.getLength() + ";");
		pw.print(YTPage.getFamilyFriendly() + ";");
		pw.print(YTPage.getRegionsAllowed() + ";");
		pw.print("\"");
		for(String k : YTPage.getKeywords())
			if(YTPage.getKeywords().indexOf(k) != YTPage.getKeywords().size()-1) pw.print(k.replace("\"", "\"\"") + ";");
			else pw.print(k.replace("\"", "\"\""));
		pw.print("\";");
		pw.print(YTPage.getViews() + ";");
		pw.print(YTPage.getDatePublished() + ";");
		pw.print(YTPage.getGenre() + ";");
		pw.print(YTPage.getLinkPreviewImage() + ";");
		pw.print(YTPage.getLikes() + ";");
		pw.print(YTPage.getDislikes() + ";");
		pw.print("\"");
		for(String d : YTPage.getDescription())
			pw.print(d);
		pw.print("\";");
		pw.print("\"");
		for(String l : YTPage.getLinkedUrls())
			if(YTPage.getLinkedUrls().indexOf(l) != YTPage.getLinkedUrls().size()-1) pw.print(l + ";");
			else pw.print(l);
		pw.print("\";");
		pw.println();
	}
	
	public static void closeCSV(){
		pw.close();
	}
	
}
