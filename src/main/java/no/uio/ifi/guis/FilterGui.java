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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.api.services.youtube.model.Video;

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
	
	private JButton searchKeywordTest = new JButton("Test Search Kword");
	
	private JCheckBox videoInfoDL = new JCheckBox();
	private JCheckBox videoDownload  = new JCheckBox();

	private JComboBox videoInfoFormats = new JComboBox(new String[]{"JSON", "XML", "CSV"});
	private JComboBox videoFormats = new JComboBox(new String[]{"Store video download link in metadata", "Download Video files"});
	private JButton fileChooserButton = new JButton("Choose path");
	private JFileChooser fileChooser = new JFileChooser();

	private JLabel outputNumberOfVideos = new JLabel("");
	
	private JTextField cityInput = new JTextField();
	private JTextField keyWordInput = new JTextField();
	private JTextField radiusInput = new JTextField();

	
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
	
	//======== FOR THE RESULT JPANEL =======
			JPanel mainResultPanel;
			JScrollPane jscrollResultUp,jscrollResultDown;
	//======================================
			

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
		onTextFieldChange();
	}
	
	
public void onTextFieldChange() {	
		
		keyWordInput.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
			    changed();
			  }
			  public void removeUpdate(DocumentEvent e) {
			    changed();
			  }
			  public void insertUpdate(DocumentEvent e) {
			    changed();
			  }

			  public void changed() {
			     if (keyWordInput.getText().equals("")){
			      
			     }
			     else {
			    	 
						selectedFilters.put(FilteredSearch.KEYWORDFILTER,  keyWordInput.getText() );					

			    	
			    }
			 	String outputText = "";
				for (String filter : selectedFilters.values()) {
					outputText += filter + "; \n";
				}

				filtersAppliedText.setText(outputText);

			  }
			});
		
		
		cityInput.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				    changed();
				  }
				  public void removeUpdate(DocumentEvent e) {
				    changed();
				  }
				  public void insertUpdate(DocumentEvent e) {
				    changed();
				  }

				  public void changed() {
				     if (cityInput.getText().equals("")){

				     }
				     else {
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
				});
		
		radiusInput.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				    changed();
				  }
				  public void removeUpdate(DocumentEvent e) {
				    changed();
				  }
				  public void insertUpdate(DocumentEvent e) {
				    changed();
				  }

				  public void changed() {
				     if (cityInput.getText().equals("")){

				     }
				     else {
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
				});
	}
	public JPanel drawSearchMenu(){
		JPanel searchPanel = new JPanel(new FlowLayout());
		fileChooserButton.setActionCommand("FILECHOOSER");
		fileChooserButton.addActionListener(mouseListener);
		
		
		JPanel videoInfo = new JPanel(new FlowLayout());
		JPanel videoDL = new JPanel(new FlowLayout());
		videoInfo.add(new JLabel("Select video metadata format: "));
		//videoInfo.add(videoInfoDL);
		videoInfoDL.setSelected(true);
		videoInfo.add(videoInfoFormats);
		videoDL.add(new JLabel("Include video?"));
		videoDL.add(videoDownload);
		videoDL.add(videoFormats);
		searchPanel.add(videoInfo);
		searchPanel.add(videoDL);
		searchPanel.add(fileChooserButton);
		searchPanel.add(searchButton);
	//	searchPanel.add(searchKeywordTest);//==================================
		return searchPanel;
	}


	/**
	 * Making the layout of the FilterGui
	 */
	public JPanel drawFilterMenu() {
		JPanel panel =  new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	//	this.setBorder(LineBorder.createGrayLineBorder());
		//TEST ===========================
		searchKeywordTest.setActionCommand("SEARCHBUTTONTEST");
		searchKeywordTest.addActionListener(mouseListener);
		searchKeywordTest.setPreferredSize(new Dimension(SEARCH_HEIGHT, 30));
		//=================================
		
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
		filterAddPanel.add(getKeyWordPanel());
		filterAddPanel.add(getGelocationPanel());
		filtersAppliedText.setPreferredSize(new Dimension(450, 450));
		filterAddPanel.add(getPeriodPanel());
		filterAddPanel.setPreferredSize(new Dimension(400, 500));
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
	
	private JPanel getKeyWordPanel(){
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 10));

		keyWordInput.setColumns(15);

		JLabel keyword = new JLabel("Keyword: ");
		Font font = keyword.getFont();
		keyword.setFont(font.deriveFont(font.getStyle() ^ Font.BOLD));
		keyword.setPreferredSize(new Dimension(100, 20));
		panel.add(keyword);
		panel.add(keyWordInput);

		return panel;
		
	}
	
	private JPanel getGelocationPanel(){
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 10));

		cityInput.setColumns(15);
		radiusInput.setColumns(5);

		JLabel city = new JLabel("City: ");
		Font font = city.getFont();
		city.setFont(font.deriveFont(font.getStyle() ^ Font.BOLD));
		city.setPreferredSize(new Dimension(100, 20));

		panel.add(city);

		panel.add(cityInput);

		JLabel radius = new JLabel("Radius: ");
		font = radius.getFont();
		radius.setFont(font.deriveFont(font.getStyle() ^ Font.BOLD));
		radius.setPreferredSize(new Dimension(60, 20));

		panel.add(radius);
		panel.add(radiusInput);

		return panel;
	}
	
	private JPanel getPeriodPanel(){
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 10));
		JLabel from = new JLabel("Year: ");

		Font font = from.getFont();
		from.setFont(font.deriveFont(font.getStyle() ^ Font.BOLD));
		from.setPreferredSize(new Dimension(100, 20));

		panel.add(from);

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

		String filterNameWithoutColon = filterName.replaceAll(":", "");

		dropDownList[0] = "No " + filterNameWithoutColon + " filter";
		int i = 1;
		if (filters.contains(filterNameWithoutColon)) {
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
			categoryList.setPreferredSize(new Dimension(250, 20));

			filters.add(filterType);
			JLabel filterLabel = new JLabel(filterName);
			Font font = filterLabel.getFont();
			filterLabel.setFont(font.deriveFont(font.getStyle() ^ Font.BOLD));
			filterLabel.setPreferredSize(new Dimension(100, 20));
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
//			case "SEARCHBUTTONTEST": //=========================TEST======
//				String keysearch = numberOfVideosInput.getText();
//				String address = cityInput.getText();
//				String distance = radiusInput.getText();
//				JPanel resultcontain = null;
//				if(address.equals("")) resultcontain = resultPartInGUI(mng.preformKeywordSearch(keysearch));
//				else {
//					distance = (distance.equals("")) ? "100km" : distance;
//					if(distance.charAt(distance.length()-1) != 'm') distance+="km";
//					resultcontain = resultPartInGUI(mng.performKeywordSearchWithGeolocation(keysearch, address, distance));
//				}
//				mng.displayresultFromKeySearch(resultcontain);
//				break;
			
			case "SEARCHBUTTON":
				String videoInfo = "";
				String videoQuality = "";
				if(videoInfoDL.isSelected()){
					videoInfo = (String) videoInfoFormats.getSelectedItem();
					System.out.println(videoInfo);
					if(filePath == null){
						new WaitDialog("Please select a path to store metadata");
						return;
					}
				}
				if(videoDownload.isSelected()){
					videoQuality = (String) videoFormats.getSelectedItem();
					if(videoQuality.toLowerCase().contains("metadata")){
						videoQuality ="VIDEOLINK";
					}else{
						videoQuality =  "VIDEOFILE";
					}
					
					System.out.println(videoInfo);
					if(filePath == null){
						new WaitDialog("Please select a path to store everything");
						return;
					}
				}
				mng.preformFilteredSearch( videoInfo, videoQuality, filePath);
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
				String gps =  GPSLocator.getGeolocationCity(cityInput.getText());
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
					if(!dateFrom.contains("YY")){//&&dateFrom.length()>0){
						try{
							from = LocalDate.parse(dateFrom, dfs);
						}catch(DateTimeParseException ex){
							outputPeriodVideos.setText("From date has wrong format");
							System.out.println("Wrong fromat");
							break;
						}
					}
					if(!dateTo.contains("YY")){//&&dateTo.length()>0 ){
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
	
	/* when perform the search, the result must be a list of Video ==============BEGIN RESULT PART================= */
	JPanel resultPartInGUI(List<Video> listOfvideo){
		if(mainResultPanel == null){
			mainResultPanel = new JPanel(new BorderLayout());
			mainResultPanel.add(createJPanelResultUp(listOfvideo), BorderLayout.CENTER);
			return mainResultPanel;
		} else {
			if(jscrollResultUp != null) mainResultPanel.remove(jscrollResultUp);
			if(jscrollResultDown != null) mainResultPanel.remove(jscrollResultDown);
			mainResultPanel.add(createJPanelResultUp(listOfvideo),BorderLayout.CENTER);
			return mainResultPanel;
		}
		
	}
	
	JScrollPane createJPanelResultUp(List<Video> listOfvideo){
		
		JPanel jpanelR_UP = new JPanel();
		jscrollResultUp = new JScrollPane(jpanelR_UP, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jpanelR_UP.setLayout(new BoxLayout(jpanelR_UP, BoxLayout.PAGE_AXIS));
		ResultElem[] outerScope = new ResultElem[listOfvideo.size()];//DETERMINE THIS LATER --------------
		
		int cnt = 0;
		if(listOfvideo != null){
			ListIterator<Video> listIter = listOfvideo.listIterator();
			Iterator<Video> it = (Iterator<Video>)listIter;
			while (it.hasNext()) {
	            Video sVideo = it.next();
	            if (sVideo.getKind().equals("youtube#video")) {
	            	ResultElem re = new ResultElem(sVideo, outerScope);
	            	outerScope[cnt++] = re;
	            	jpanelR_UP.add(re);
	            }
	        }
		}
		return jscrollResultUp;
	}
	
	/**
	 * 
	 * @author Viet Thi Tran
	 *
	 */
	class ResultElem extends JPanel implements MouseListener{
		Video svideo;
		
		JPanel jp_tail;
		JLabel jjtitle, jjLink;
		JLabel jicon;
		public String videoLink;
		
		
		ResultElem[] outerScope;
		boolean selected = false;
		
		public ResultElem(Video svideo, ResultElem[] outerScope){
			this.svideo = svideo;
			this.outerScope = outerScope;
			setBorder(BorderFactory.createLineBorder(Color.GRAY));
			createInfoVideo();
			addMouseListener(this);
		}
		
		public void createInfoVideo(){
			this.setLayout(new BorderLayout());
			
			String urlThumbnail = svideo.getSnippet().getThumbnails().getDefault().getUrl();
			videoLink = "https://www.youtube.com/watch?v="+svideo.getId();
			//System.out.println(videoLink);
			String title = svideo.getSnippet().getTitle();
			URL url = null;
			BufferedImage image =null;
			try{
				url = new URL(urlThumbnail);
				image = ImageIO.read(url);
			}catch(Exception e){}
			
			jicon = new JLabel(new ImageIcon(image));
			this.add(jicon, BorderLayout.LINE_START);
			
			jp_tail = new JPanel();
			jp_tail.setBackground(Color.WHITE);
			jp_tail.setLayout(new BoxLayout(jp_tail, BoxLayout.PAGE_AXIS));
			
			jjtitle = new JLabel(title);
			jjtitle.setFont(new Font("Serif", Font.BOLD, 18));
			jp_tail.add(jjtitle);
			
			jjLink = new JLabel(videoLink);
			jjLink.setFont(new Font("Serif", Font.ROMAN_BASELINE, 16));
			jp_tail.add(jjLink);
			
			this.add(jp_tail, BorderLayout.CENTER);
		}
		
		
		public void mouseClicked(MouseEvent e){
		
			System.out.println("SELECTED "+svideo.getSnippet().getTitle());
			String videoTittle = svideo.getSnippet().getTitle();
			
			
			for(int i = 0; i < outerScope.length; i++){
				if(outerScope[i] != null && outerScope[i].selected == true){
					outerScope[i].jp_tail.setBackground(Color.WHITE);
					outerScope[i].jjtitle.setFont(new Font("Serif", Font.BOLD, 18));
					outerScope[i].jjLink.setFont(new Font("Serif", Font.ROMAN_BASELINE, 16));
					outerScope[i].selected = false;
					//break;
				}
			}
			selected = true;
			jjtitle.setFont(new Font("Serif", Font.BOLD, 21));
			jjLink.setFont(new Font("Serif", Font.ROMAN_BASELINE, 19));
			jp_tail.setBackground(Color.PINK);
			if(jscrollResultDown != null) mainResultPanel.remove(jscrollResultDown);
			jscrollResultDown = createResultBelow(this);
			
			mainResultPanel.add(jscrollResultDown, BorderLayout.PAGE_END);
			jscrollResultDown.revalidate();
			mainResultPanel.revalidate();
			revalidate();
			//pack();
		}
		
		public void mouseEntered(MouseEvent e){
			
		}

		public void mouseExited(MouseEvent e){
			
		}

		public void mousePressed(MouseEvent e){

		}

		public void mouseReleased(MouseEvent e){

		}
	}
	
	public String getKeyWordText() {
		return keyWordInput.getText();
		
	}
	
	
	/*The information of the selected video will be displayed here*/
	JScrollPane createResultBelow(ResultElem relem){
		JPanel jpanelR_down = new JPanel();
		jscrollResultDown = new JScrollPane(jpanelR_down, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		jpanelR_down.add(new JLabel("THE VIDEO INFO WILL BE HERE "+relem.svideo.getSnippet().getTitle()));
		
		return jscrollResultDown;
	}
	
	
	/*=================================================END RESULT PART==========================================================*/
	
	
	
}
