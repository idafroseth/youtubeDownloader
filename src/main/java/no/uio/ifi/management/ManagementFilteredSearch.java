package no.uio.ifi.management;

import java.util.HashMap;

import no.uio.ifi.guis.FilteredSearchGui;
import no.uio.ifi.models.FilteredSearch;

public class ManagementFilteredSearch {
	FilteredSearch filterSearch = new FilteredSearch();
	FilteredSearchGui gui = new FilteredSearchGui(this);
	
	public ManagementFilteredSearch(){
		HashMap<String, String> availableCategories = (HashMap<String, String>) filterSearch.getVideoCategories();
		HashMap<String, String> availableLanguages = (HashMap<String, String>)  filterSearch.getAvailableLanguages();
		HashMap<String, String> availableRegions = (HashMap<String, String>) filterSearch.getAvailableRegions();
		gui.addFilterBox(availableCategories, "Category", FilteredSearch.CATEGORYFILTER);
		gui.addFilterBox(availableLanguages, "Language", FilteredSearch.LANGUAGEFILTER);
		gui.addFilterBox(availableRegions, "Region", FilteredSearch.REGIONFILTER);
	}
	
	public void preformSearch(){
		HashMap<Integer, String> filtersApplied = gui.getSelectedFilters();
		filterSearch.init();
		for(Integer key : filtersApplied.keySet()){
			System.out.println("AddingFilters");
			filterSearch.setFilter(key, filtersApplied.get(key) );
		}
	//	filterSearch.search("hElik");
		ManagementAllRandom mar = new ManagementAllRandom(this.filterSearch);
		
	//		mar.searchBaseOnRandomID(mar.randomUrlGenerator());
		mar.numberOfThreads(1);
//		
	}
	
	public static void main(String[] args){
		ManagementFilteredSearch fs = new ManagementFilteredSearch();
	}
}
