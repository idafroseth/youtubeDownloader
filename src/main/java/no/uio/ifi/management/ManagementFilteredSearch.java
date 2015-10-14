package no.uio.ifi.management;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Toolkit;

import no.uio.ifi.guis.FilteredSearchGui;
import no.uio.ifi.guis.WaitDialog;
import no.uio.ifi.models.FilteredSearch;

/**
 * Management for the filtered search Displaying a gui and handling the dialog
 * with the searchbox and crawler
 * 
 * @author Ida Marie Frøseth
 *
 */
public class ManagementFilteredSearch {
	FilteredSearch filterSearch = new FilteredSearch();
	FilteredSearchGui gui = new FilteredSearchGui(this);

	/**
	 * Retrieving all the filters and then display the window. 
	 */
	public ManagementFilteredSearch() {
		
		gui.initWindow();
		WaitDialog wait = new WaitDialog();
		HashMap<String, String> availableCategories = (HashMap<String, String>) filterSearch.getVideoCategories();
		HashMap<String, String> availableLanguages = (HashMap<String, String>) filterSearch.getAvailableLanguages();
		HashMap<String, String> availableRegions = (HashMap<String, String>) filterSearch.getAvailableRegions();
		HashMap<String, String> availableDuration = (HashMap<String, String>) filterSearch.getAvailableVideoDuration();
		HashMap<String, String> availableVideoTypes = (HashMap<String, String>) filterSearch.getAvailableVideoTypes();
		
		gui.addFilterBox(availableCategories, "Category", FilteredSearch.CATEGORYFILTER);
		gui.addFilterBox(availableLanguages, "Language", FilteredSearch.LANGUAGEFILTER);
		gui.addFilterBox(availableRegions, "Region", FilteredSearch.REGIONFILTER);
		gui.addFilterBox(availableDuration, "Duration", FilteredSearch.VIDEODURATIONFILTER);
		gui.addFilterBox(availableVideoTypes, "Video types", FilteredSearch.VIDEOTYPEFILTER);
		wait.setVisible(false);
		gui.pack();
	}
	
	
	/**
	 * Applying choseing filter and start the search. 
	 */
	public void preformSearch() {
		HashMap<Integer, String> filtersApplied = gui.getSelectedFilters();
		filterSearch.init();
		for (Integer key : filtersApplied.keySet()) {
			System.out.println("AddingFilters");
			filterSearch.setFilter(key, filtersApplied.get(key));
		}

		// filterSearch.search("hElik");
		ManagementAllRandom mar = new ManagementAllRandom(this.filterSearch);

		// mar.searchBaseOnRandomID(mar.randomUrlGenerator());
		mar.numberOfThreads(1);
		//
	}
	
	public static void main(String[] args) {
		ManagementFilteredSearch fs = new ManagementFilteredSearch();
	}
}
