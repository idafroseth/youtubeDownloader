package no.uio.ifi.management;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
//import com.google.api.services.youtube.model.Thumbnail;
import no.uio.ifi.models.Search;
import no.uio.ifi.guis.YTDashGUI;

import java.util.*;

public class ManagementAll {
	YTDashGUI view;
	Search search;
	
	public static void main(String[] args) {
		ManagementAll mng = new ManagementAll();
		mng.search = new Search(mng);
		mng.view = new YTDashGUI(mng);
		
	}
	
	public void searchBaseOnKeyWord(String keyword){
		List<SearchResult> searchResults = search.getVideoLinkFromKeyWord(keyword);
		ListIterator<SearchResult> iteratorSearchResults = searchResults.listIterator();
        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + Search.NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + keyword + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                //Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
               // System.out.println(singleVideo);
                System.out.println("https://www.youtube.com/watch?v=" + rId.getVideoId());
                System.out.println(singleVideo.getSnippet().getTitle());
                
                view.videoQueryList_Text.append("https://www.youtube.com/watch?v=" + rId.getVideoId());
                view.videoQueryList_Text.append("\n");
                view.videoQueryList_Text.append(singleVideo.getSnippet().getTitle());
                view.videoQueryList_Text.append("\n");
            }
        }
	}
	
	
		
}
