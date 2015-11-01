package no.uio.ifi.guis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import no.uio.ifi.management.ManagementFilteredSearch;
import no.uio.ifi.models.geo.GPSLocator;
import no.uio.ifi.models.search.FilteredSearch;

public class JsoupSearchGui extends FilterGui{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2800226228629567624L;
	JTextField jSoupStartUrl = new JTextField("VideoId or URL");
	private JButton jSoupApplyButton = new JButton("Apply");
	private String startUrl = "https://www.youtube.com";
	
	public JsoupSearchGui(ManagementFilteredSearch mng) {
		super(mng);
	}
	
	@Override
	public void init(boolean addComments){
		videoInfoFormats = new  JComboBox(new String[]{"CSV", "XML"});//, "JSON"});
		videoFormats = new JComboBox(new String[]{"Download Video files"});
		 selectedFilters.put(FilteredSearch.JSOUPSTARTURL,  "https://www.youtube.com" );
		System.out.println("Initialize the jsoup gui");
		this.setPreferredSize(FilteredSearchGui.CONTENT_PANE_SIZE);
		this.setLayout(new BorderLayout());
		this.add(drawSearchMenu(addComments), BorderLayout.PAGE_END);
		this.add(drawFilterMenu(), BorderLayout.CENTER);
		onTextFieldChange();
		String infoText="The JSoup crawler does not use the YouTube APi, and will provide a result that more reflect what the user is met by";

	}
	public String getStartUrl(){
		return this.startUrl;
	}
	public JPanel getSearchAlternative(){
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		panel.add(new JLabel("Choose start page"));
		jSoupStartUrl.setColumns(20);
		
		jSoupStartUrl.getDocument().addDocumentListener(new DocumentListener() {
			
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
				 String url = jSoupStartUrl.getText();
				
				 if(url.contains("watch?v=")){
					 if(url.length()==43){
						//This is a complete url
					 }
					 else if(url.length()== 35 ){
						 //miss the https
						 url = "https://"+url;
					 }
					 else if(url.length()==19){
						 url = "https://www.youtube.com/"+ url;
					 }
				 } else if(url.length() == 11){
					 //This is the id
					 url = "https://www.youtube.com/watch?v=" + url;
				 } else if(url.length() == 23){
					 //the main page
				 }else{
					 //Warning not a valid url!
					 url = "Url not valid";
				 }
				 startUrl = url;	
				 filtersAppliedText.setText(url);
			  }
		});
			
		
		panel.add(jSoupStartUrl);
//		panel.add(jSoupApplyButton);
		return panel;
	}
	
	@Override 
	public JPanel drawFilterMenu() {
		JPanel panel =  new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));


	//	filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
		filterPanel.setLayout(new GridLayout(1, 2));
		
		filterPanel.add(filterAddPanel);
		filterPanel.add(filterActivePanel);
	
		filtersAppliedText.setBackground(Color.WHITE);
		filterAddPanel.setLayout(new GridLayout(10, 1));
		filterAddPanel.setPreferredSize(new Dimension(100, 500));
		filterAddPanel.add(getNumberOfVideosPanel());
		filterAddPanel.add(getSearchAlternative());
		filterAddPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
//		FontFactory.changeFont(filterAddPanel, getNumberOfVideosPanel().getFont());

		filtersAppliedText.setPreferredSize(new Dimension(PANEL_WIDTH/2, PANEL_HEIGHT-1));

		filterAddPanel.setPreferredSize(new Dimension(400, 500));
		filtersAppliedText.setBorder(BorderFactory.createTitledBorder("Applied filters"));
		
		filterActivePanel.add(filtersAppliedText);
		filtersAppliedText.setBackground(new Color(238,238,238));
		panel.add(filterPanel);
		return panel;
	}



}
