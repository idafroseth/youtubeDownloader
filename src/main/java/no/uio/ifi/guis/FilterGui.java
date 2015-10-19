package no.uio.ifi.guis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import no.uio.ifi.management.ManagementFilteredSearch;
import no.uio.ifi.models.geo.GPSLocator;
import no.uio.ifi.models.search.FilteredSearch;

/**
 * This Class display a JPanel with three content panes. one contentpane hold
 * all the possible filters, one holding the chosen filters and the last one
 * containing the search button
 * 
 * @author Ida Marie Frøseth
 *
 */
public class FilterGui extends JPanel {

	private static final long serialVersionUID = -7018213359683528690L;
	private JPanel filterPanel = new JPanel();
	private JPanel filterAddPanel = new JPanel();
	private JPanel filterActivePanel = new JPanel();
	private JButton searchButton = new JButton("Search");
	private JCheckBox videoInfoDL = new JCheckBox();
	private JCheckBox videoDownload  = new JCheckBox();

	private JComboBox videoInfoFormats = new JComboBox(new String[]{"JSON", "XML", "CSV"});
	private JComboBox videoFormats = new JComboBox(new String[]{"Smallest available", "Best available"});
	private JButton fileChooserButton = new JButton("Choose path");
	private JFileChooser fileChooser = new JFileChooser();

	
	private JTextField cityInput = new JTextField();
	private JTextField radiusInput = new JTextField();
	private JButton applyGeoFilter = new JButton("Set geofilter");
	
	private JTextField numberOfVideosInput = new JTextField("100 000");
	// limited to 10 filters
	private ArrayList<Integer> filters = new ArrayList<Integer>(10);
	private SelectorListener filterListener = new SelectorListener();
	private ButtonListener mouseListener = new ButtonListener();
	private ManagementFilteredSearch mng;
//	private final String VIDEO_INFO_DOWNLOAD_CHECK = "100";
//	private final String VIDEO_INFO_DOWNLOAD_CHOISE = "200";
//	private final String VIDEO_DOWNLOAD_CHECK = "300";
//	private final String VIDEO_DOWNLOAD_CHOISE = "400";
	public File filePath = null;
//	
	private HashMap<Integer, String> selectedFilters = new HashMap<Integer, String>(10);

	private JTextArea filtersAppliedText = new JTextArea("No filter");

	/**
	 * Contructor
	 * 
	 * @param mng
	 *            the Managment that has to be alerted when the search button is
	 *            hit.
	 */
	public FilterGui(ManagementFilteredSearch mng) {
		this.mng = mng;
		init();
	}

	public void init(){
		cityInput.setColumns(15);
		radiusInput.setColumns(5);
		numberOfVideosInput.setColumns(20);
		this.drawFilterMenu();
		this.setPreferredSize(FilteredSearchGui.CONTENT_PANE_SIZE);
		this.setLayout(new BorderLayout());
		this.add(drawSearchMenu(), BorderLayout.PAGE_END);
		this.add(filterPanel, BorderLayout.CENTER);
		selectedFilters.replace(FilteredSearch.GEOFILTER, "No City");
	}
	public JPanel drawSearchMenu(){
		JPanel searchPanel = new JPanel(new FlowLayout());
//		videoInfoDL.setActionCommand(VIDEO_INFO_DOWNLOAD_CHECK);
//		videoDownload.setActionCommand(VIDEO_DOWNLOAD_CHOISE);
//		videoInfoFormats.setActionCommand(VIDEO_INFO_DOWNLOAD_CHOISE);
//		VideoFormats.setActionCommand(VIDEO_DOWNLOAD_CHOISE);
		fileChooserButton.setActionCommand("FILECHOOSER");
		fileChooserButton.addActionListener(mouseListener);
		
		
		JPanel videoInfo = new JPanel(new FlowLayout());
		JPanel videoDL = new JPanel(new FlowLayout());
		videoInfo.add(new JLabel("Download video info?"));
		videoInfo.add(videoInfoDL);
		videoInfo.add(videoInfoFormats);
		videoDL.add(new JLabel("Download video?"));
		videoDL.add(videoDownload);
		videoDL.add(videoFormats);
		searchPanel.add(videoInfo);
		searchPanel.add(videoDL);
		searchPanel.add(fileChooserButton);
		searchPanel.add(searchButton);//, FlowLayout.TRAILING);
		
	//	videoFormats.
		return searchPanel;
		
	}


	/**
	 * Making the layout of the FilterGui
	 */
	public void drawFilterMenu() {
	//	this.setBorder(LineBorder.createGrayLineBorder());
		searchButton.setActionCommand("SEARCHBUTTON");
		searchButton.addActionListener(mouseListener);
		searchButton.setPreferredSize(new Dimension(100, 30));
		
		

		filterPanel.setPreferredSize(new Dimension(1000, 500));
		filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
		filterPanel.setLayout(new GridLayout(1, 2));
		filterPanel.add(filterAddPanel);
		filterPanel.add(filterActivePanel);
		filtersAppliedText.setBackground(Color.WHITE);
		filterAddPanel.setLayout(new GridLayout(15, 1));
		filterAddPanel.setPreferredSize(new Dimension(100, 500));
		filterAddPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		filterAddPanel.add(new JLabel("# Videos to search"));
		filterAddPanel.add(numberOfVideosInput);
		filterAddPanel.setPreferredSize(new Dimension(400, 500));
		filterAddPanel.add(getGelocationPanel());
		filtersAppliedText.setPreferredSize(new Dimension(450, 500));
		filtersAppliedText.setBorder(BorderFactory.createTitledBorder("Applied filters"));
		filterActivePanel.add(filtersAppliedText);
		filtersAppliedText.setBackground(new Color(238,238,238));
	}
	public JPanel getGelocationPanel(){
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 10));
		applyGeoFilter.setActionCommand("APPLYGEOFILTER");
		applyGeoFilter.addActionListener(mouseListener);

		panel.add(new JLabel("City:"));
		panel.add(cityInput);
		panel.add(new JLabel("Radius:"));
		panel.add(radiusInput);
		panel.add(applyGeoFilter);
		return panel;
		
	}

	/**
	 * Add a filter box to the gui in the filterAddPanel
	 * 
	 * @param filter
	 *            Map contaning all the possible filter values
	 * @param filterName
	 *            The name of the filter, is displayed to the left of the
	 *            JComboBox
	 * @param filterType
	 *            is a defined filtertype that you can find in FilterSearch
	 *            class
	 * @return
	 */
	public boolean addFilterBox(Map<String, String> filter, String filterName, Integer filterType) {
		String[] dropDownList = new String[filter.size() + 1];
		dropDownList[0] = "No " + filterName + " filter";
		int i = 1;
		if (filters.contains(filterName)) {
			System.out.println("A filter with this name is already defined!");
			return false;
		} else {

			for (String key : filter.keySet()) {
				dropDownList[i] = key;
				i++;
			}
			JComboBox categoryList = new JComboBox(dropDownList);
			categoryList.setName(filterType.toString());
			categoryList.setSelectedIndex(0);
			categoryList.addActionListener(filterListener);
			filters.add(filterType);
			JLabel filterLabel = new JLabel(filterName);
			JPanel filterRow = new JPanel();
			filterRow.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 10));
			filterRow.add(filterLabel);
			filterRow.add(categoryList);
			filterAddPanel.add(filterRow);
			return true;
		}
	}

	/**
	 * 
	 * @return a list HashMap<FilterSearch.FILTERTYPE, FilterValue> of all the
	 *         selected filters
	 */
	public HashMap<Integer, String> getSelectedFilters() {
		return this.selectedFilters;
	}

	/**
	 * 
	 * Class that act when a button is clicked
	 * 
	 * @author Ida Marie Frøseth
	 *
	 */
	private class ButtonListener implements ActionListener {

		/**
		 * Alters the management that the search button is clicked and display a
		 * progress bar in a JDialogBox
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			//get the choise
			String action = e.getActionCommand();
			switch(action){
			case "SEARCHBUTTON":
				String videoInfo = null;
				String videoQuality = null;
				if(videoInfoDL.isSelected()){
					videoInfo = (String) videoInfoFormats.getSelectedItem();
					System.out.println(videoInfo);
					if(filePath == null){
						new WaitDialog("If you like to download the video info, you have to select a path");
						return;
					}
				}
				if(videoDownload.isSelected()){
					videoQuality = (String) videoFormats.getSelectedItem();
					System.out.println(videoInfo);
					if(filePath == null){
						new WaitDialog("If you like to download the video, you have to select a path");
						return;
					}
				}
				mng.preformSearch( videoInfo,  videoQuality, filePath);//, filePath);
				break;
			case "FILECHOOSER":
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(FilterGui.this);
				filePath = fc.getSelectedFile();
				System.out.println(returnVal);
				System.out.println(fc.getSelectedFile());
				break;
			case "APPLYGEOFILTER":
				//First we must check if the location is valid
				String gps = GPSLocator.getGeolocationCity(cityInput.getText());
				if(gps == null){
					selectedFilters.put(FilteredSearch.GEOFILTER, "Could not find city: " + cityInput.getText() );
				}else{
					if(gps.length()<30 ){
						selectedFilters.put(FilteredSearch.GEOFILTER, gps + " - " + cityInput.getText() + " - " + radiusInput.getText());					
					}else{
						selectedFilters.replace(FilteredSearch.GEOFILTER, "Could not find city: " + cityInput.getText() );
					}
				}
				String outputText = "";
				for (String filter : selectedFilters.values()) {
					outputText += filter + "; \n";
				}

				filtersAppliedText.setText(outputText);

			}
		}
	}

	/**
	 * Listen when the user choose one of the filters
	 * For now the user can only chose one filter for each Filter type. 
	 * @author Ida Marie Frøseth
	 *
	 */
	private class SelectorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			JComboBox comboBox = (JComboBox) e.getSource();
			System.out.println(comboBox.getName());
			String category = (String) comboBox.getSelectedItem();
			if (selectedFilters.containsKey(comboBox.getName())) {
				selectedFilters.replace(Integer.parseInt(comboBox.getName()), category);
			} else {
				selectedFilters.put(Integer.parseInt(comboBox.getName()), category);
			}
			String outputText = "";
			for (String filter : selectedFilters.values()) {
				outputText += filter + "; \n";
			}

			filtersAppliedText.setText(outputText);
		}
	}
}
