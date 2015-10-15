package no.uio.ifi.models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

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
		pw.println("\t<videoList>");
	}

	public static void write(PageYouTube YTPage) {
		pw.println("\t\t<video>");
		pw.println("\t\t\t<ID>" + YTPage.getVideoID() + "</ID>");
		pw.println("\t\t\t<title>" + YTPage.getTitle() + "</title>");
		pw.println("\t\t\t<author>" + YTPage.getAuthor() + "</author>");
		pw.println("\t\t\t<length>" + YTPage.getLength() + "</length>");
		pw.println("\t\t\t<familyFriendly>" + YTPage.getFamilyFriendly() + "</familyFriendly>");
		pw.println("\t\t\t<regionsAllowed>" + YTPage.getRegionsAllowed() + "</regionsAllowed>");
		pw.println("\t\t\t<keywords>");
		for(String k : YTPage.getKeywords())
			pw.println("\t\t\t\t<keyword>" + k + "</keyword>");
		pw.println("\t\t\t</keywords>");
		pw.println("\t\t\t<description>" + YTPage.getDescription() + "</description>");
		pw.println("\t\t\t<comments>");
//		pw.println("\t\t\t\t<comment>" + YTPage.getDescription() + "</description>");
		pw.println("\t\t\t</comments>");
	}
	
	public static void closeXML(){
		pw.print("</videoList>");
		pw.close();
	}
}
