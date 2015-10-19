package no.uio.ifi.guis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
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
import javax.swing.SwingWorker;

import com.google.api.client.util.DateTime;

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

	private JLabel outputNumberOfVideos = new JLabel("");
	
	private JTextField cityInput = new JTextField();
	private JTextField radiusInput = new JTextField();
	private JButton applyGeoFilter = new JButton("Set geofilter");
	
	private JTextField startDateTextField = new JTextField("YYYY-MM-DD");
	private JTextField endDateTextField = new JTextField("YYYY-MM-DD");
	private JLabel outputPeriodVideos = new JLabel("");
	
	private JTextField numberOfVideosInput = new JTextField("100 000");
	// limited to 10 filters
	private ArrayList<Integer> filters = new ArrayList<Integer>(10);
	private SelectorListener filterListener = new SelectorListener();
	private ButtonListener mouseListener = new ButtonListener();
	private ManagementFilteredSearch mng;
	public static final Integer SEARCH_HEIGHT = 100;
	
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
		this.setPreferredSize(FilteredSearchGui.CONTENT_PANE_SIZE);
		this.setLayout(new BorderLayout());
		this.add(drawSearchMenu(), BorderLayout.PAGE_END);
		this.add(drawFilterMenu(), BorderLayout.CENTER);
		selectedFilters.replace(FilteredSearch.GEOFILTER, "No City");
	}
	public JPanel drawSearchMenu(){
		JPanel searchPanel = new JPanel(new FlowLayout());
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
		searchPanel.add(searchButton);
		return searchPanel;
	}


	/**
	 * Making the layout of the FilterGui
	 */
	public JPanel drawFilterMenu() {
		JPanel panel =  new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	//	this.setBorder(LineBorder.createGrayLineBorder());
		searchButton.setActionCommand("SEARCHBUTTON");
		searchButton.addActionListener(mouseListener);
		searchButton.setPreferredSize(new Dimension(100, 30));
		panel.add(getNumberOfVideosPanel());
		filterPanel.setPreferredSize(new Dimension(FilteredSearchGui.WINDOW_WIDTH, FilteredSearchGui.WINDOW_HEIGHT -100));
		filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
		filterPanel.setLayout(new GridLayout(1, 2));
		filterPanel.add(filterAddPanel);
		filterPanel.add(filterActivePanel);
	
		filtersAppliedText.setBackground(Color.WHITE);
		filterAddPanel.setLayout(new GridLayout(10, 1));
		filterAddPanel.setPreferredSize(new Dimension(100, 500));
		filterAddPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		filterAddPanel.add(getPeriodPanel());
		filterAddPanel.setPreferredSize(new Dimension(400, 500));
		filterAddPanel.add(getGelocationPanel());
		filtersAppliedText.setPreferredSize(new Dimension(450, 450));
		filtersAppliedText.setBorder(BorderFactory.createTitledBorder("Applied filters"));
		filterActivePanel.add(filtersAppliedText);
		filtersAppliedText.setBackground(new Color(238,238,238));
		panel.add(filterPanel);
		return panel;
	}
	private JPanel getNumberOfVideosPanel(){
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
		JButton setNumVideos = new JButton("Apply");
		setNumVideos.setActionCommand("NUMVIDEOS");
		outputNumberOfVideos.setForeground(Color.RED);
		setNumVideos.addActionListener(mouseListener);
		numberOfVideosInput.setColumns(8);
		//numberOfVideosInput.addActionListener(l);
		//panel.setBorder(BorderFactory.createTitledBorder("# of videos"));
		panel.add(new JLabel("# Videos to search:"));
		panel.add(numberOfVideosInput);
		panel.add(setNumVideos);
		panel.add(outputNumberOfVideos);
		FontFactory.changeFont(panel, new Font("Arial", Font.PLAIN, 15));
		
		return panel;
	}
	private JPanel getGelocationPanel(){
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 10));

		cityInput.setColumns(15);
		radiusInput.setColumns(5);
		applyGeoFilter.setActionCommand("APPLYGEOFILTER");
		applyGeoFilter.addActionListener(mouseListener);

		panel.add(new JLabel("City:"));
		panel.add(cityInput);
		panel.add(new JLabel("Radius:"));
		panel.add(radiusInput);
		panel.add(applyGeoFilter);
		
		return panel;
		
	}
	private JPanel getPeriodPanel(){
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 10));
		panel.add(new JLabel("From: "));
		outputPeriodVideos.setForeground(Color.RED);
		startDateTextField.setColumns(8);
		endDateTextField.setColumns(8);
		panel.add(startDateTextField);
		panel.add(new JLabel("To:"));
		panel.add(endDateTextField);
	
		JButton apply = new JButton("Apply");
		apply.setActionCommand("PERIOD");
		apply.addActionListener(mouseListener);
		panel.add(apply);	
		panel.add(outputPeriodVideos);
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
				String videoInfo = "";
				String videoQuality = "";
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

				mng.preformFilteredSearch( videoInfo,  videoQuality, filePath);
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
				break;
			case "NUMVIDEOS":
				String number = numberOfVideosInput.getText().replaceAll("\\s+","");
				try {
					Integer numVideos = Integer.parseInt(number);
				     System.out.println("An integer");
				     outputNumberOfVideos.setText(null);
				     mng.NUMBER_OF_VIDEOS_TO_SEARCH = numVideos;
				}
				catch (NumberFormatException num) {
					 outputNumberOfVideos.setText("Not a number");
					 numberOfVideosInput.setText(mng.NUMBER_OF_VIDEOS_TO_SEARCH+"");
				     //Not an integer
				}
				selectedFilters.put(FilteredSearch.NUMBERTOSEARCHFILTER, "Videos to search: " + number);
				outputPeriodVideos.setText("");

				String o = "";
				for (String filter : selectedFilters.values()) {
					o += filter + "; \n";
				}
				filtersAppliedText.setText(o);
				break;
			case "PERIOD":
				String dateFrom = startDateTextField.getText();//+"T00:00:00Z";
				String dateTo = endDateTextField.getText();//+"T00:00:00Z";
				
					LocalDate from = null;
					LocalDate to = null;
					DateTimeFormatter dfs = new DateTimeFormatterBuilder()
	                        .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))                                                                 
	                        .appendOptional(DateTimeFormatter.ofPattern("dd.MM.yyyy"))                                                                                     
	                        .toFormatter();
					if(!dateFrom.contains("YY")&&dateTo.length()>0){
						try{
							from = LocalDate.parse(dateFrom, dfs);
						}catch(DateTimeParseException ex){
							outputPeriodVideos.setText("From date has wrong format");
							System.out.println("Wrong fromat");
							break;
						}
					}
					if(!dateTo.contains("YY")&&dateTo.length()>0 ){
						try{
							to = LocalDate.parse(dateTo, dfs);
						}catch(DateTimeParseException ex){
							outputPeriodVideos.setText("To date has wrong format");
							System.out.println("Wrong fromat");
							break;
						}
					}
					if(!dateTo.contains("YY") && !dateFrom.contains("YY")){
						if(to.compareTo(from)<=0){
							outputPeriodVideos.setText("StartDate is less then EndDate");
							break;
						}
					}
					
				
					String period = from + "|" + to;
					selectedFilters.put(FilteredSearch.TIMEFILTER, period);
					outputPeriodVideos.setText("");
			
					String ot = "";
					for (String filter : selectedFilters.values()) {
						ot += filter + "; \n";
					}

					filtersAppliedText.setText(ot);
				
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
