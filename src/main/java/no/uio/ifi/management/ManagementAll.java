package no.uio.ifi.management;

import java.util.List;
import java.util.ListIterator;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import no.uio.ifi.guis.MainGUI;
import no.uio.ifi.models.search.GeolocationSearch;
import no.uio.ifi.models.search.Search;

public class ManagementAll {
	MainGUI view;
	Search search;
	
	
	public static void main(String[] args) {
		ManagementAll mng = new ManagementAll();
		mng.search = new Search(mng);
		mng.view = new MainGUI(mng);
		
	}
	
	public ListIterator<SearchResult> searchBaseOnKeyWord(String keyword){
		List<SearchResult> searchResults = search.getVideoLinkFromKeyWord(keyword);
		ListIterator<SearchResult> iteratorSearchResults = searchResults.listIterator();
		return iteratorSearchResults;
	}
	
	
	public void crawler(String keyword){
		
	}
	
	public ListIterator<Video> searchOnGeolocation(String keyword, String city, String distance){
		System.out.println("Keyword: "+keyword +" City:"+city+" distance "+ distance);
		GeolocationSearch s = new GeolocationSearch();
		List<Video> listvideo = s.searchVideoBaseOnLocation(keyword, city, distance);
		ListIterator<Video> list = listvideo.listIterator();        
        return list;
	}
		
}
