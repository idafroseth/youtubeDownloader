package no.uio.ifi.guis;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import no.uio.ifi.management.ManagementFilteredSearch;

public class FilteredSearchGui extends JFrame{
		private JPanel contentPane = new JPanel();
		private JPanel searchWindow = new JPanel();
		private JPanel resultWindow = new JPanel();
		private JPanel statsWindow = new JPanel();
		private JPanel filterPanel = new JPanel();
		private JPanel filterAddPanel = new JPanel();
		private JPanel filterActivePanel = new JPanel();
		
		private JPanel menuPanel = new JPanel();
		private JPanel resultPanel = new JPanel();
		
		private SelectorListener filterListener = new SelectorListener();
		private MouseListener mouseListener = new MouseListener ();
		//limited to 10 filters
		private ArrayList<Integer> filters = new ArrayList<Integer>(10);
		private HashMap<Integer, String> selectedFilters = new HashMap<Integer, String>(10);
		private JButton searchButton = new JButton("Search");
		private JButton searchMenu = new JButton("Search"); 
		private JButton resultMenu = new JButton("Result");
		private JButton statsMenu = new JButton("Statistics");
		private final Dimension CONTENT_PANE_SIZE = new Dimension(1000,600);
		private final Dimension MENU_BUTTON_SIZE = new Dimension(100,40);
		
		private JTextArea filtersAppliedText= new JTextArea("No filter");
		
		private ManagementFilteredSearch mng;
		
		public FilteredSearchGui(ManagementFilteredSearch mng){
			this.mng = mng;
			initWindow();
		}
		/**
		 * Configure the layout of the window and starts at the search card
		 */
		public void initWindow(){
			contentPane.setLayout(new CardLayout());
			contentPane.setPreferredSize(CONTENT_PANE_SIZE);
			contentPane.add(searchWindow, "SEARCH");
			contentPane.add(resultWindow, "RESULT");
			contentPane.add(statsWindow, "STATS");
			
			searchButton.setActionCommand("SEARCHBUTTON");
			searchButton.addActionListener(mouseListener);
			searchButton.setPreferredSize(new Dimension(100,30));
			
			drawMenuBar();
			drawFilterMenu();
			
			searchWindow.setPreferredSize(CONTENT_PANE_SIZE);
			searchWindow.setLayout(new BorderLayout());
			searchWindow.add(searchButton, BorderLayout.PAGE_END);
			searchWindow.add(filterPanel, BorderLayout.CENTER);
			
			
			this.setVisible(true);
			this.setLayout(new BorderLayout());
			this.add(contentPane, BorderLayout.CENTER);
			this.add(menuPanel, BorderLayout.PAGE_START);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			drawSearch();
		}
		public void drawMenuBar(){
			searchMenu.setPreferredSize(MENU_BUTTON_SIZE);
			resultMenu.setPreferredSize(MENU_BUTTON_SIZE);
			statsMenu.setPreferredSize(MENU_BUTTON_SIZE);
			searchMenu.setActionCommand("SEARCH");
			resultMenu.setActionCommand("RESULT");
			statsMenu.setActionCommand("STATS");
			searchMenu.addActionListener(mouseListener);
			resultMenu.addActionListener(mouseListener);
			statsMenu.addActionListener(mouseListener);
			menuPanel.setPreferredSize(new Dimension(1000, 50));
			menuPanel.setLayout(new FlowLayout(FlowLayout.LEADING, -5, 0));
			menuPanel.add(searchMenu);
			menuPanel.add(resultMenu);
			menuPanel.add(statsMenu);
		}
		public void drawFilterMenu(){
			
			filterPanel.setPreferredSize(new Dimension(1000, 500));
			filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
			filterPanel.setLayout(new GridLayout(1, 2));
			filterPanel.add(filterAddPanel);
			filterPanel.add(filterActivePanel);
			filterAddPanel.setLayout(new GridLayout(15, 1));
			filterAddPanel.setPreferredSize(new Dimension(100, 500));
			filterAddPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
//			JPanel numSearchPanel = new JPanel();
//			numSearchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 10));
			filterAddPanel.add(new JLabel("# Videos to search"));
			filterAddPanel.add(new JTextField("100 000"));
			//filterAddPanel.add(numSearchPanel);
			filterActivePanel.setBorder(BorderFactory.createTitledBorder("Applied filter"));
			filterAddPanel.setPreferredSize(new Dimension(400, 500));
			filtersAppliedText.setPreferredSize(new Dimension(450,500));
			filterActivePanel.add(filtersAppliedText);
			
		}
		public void drawSearch(){
			setTitle("YTDownloader ~ Filtered search");
			((CardLayout) contentPane.getLayout()).show(contentPane, "SEARCH");
			pack();
		}
		public void drawResult(){
			setTitle("YTDownloader ~ Result");
			((CardLayout) contentPane.getLayout()).show(contentPane, "RESULT");
			pack();
		}
		public void drawStatistics(){
			setTitle("YTDownloader ~ Statistics");
			((CardLayout) contentPane.getLayout()).show(contentPane, "STATS");
			pack();
		}
		
		public boolean addFilterBox(Map<String, String> filter, String filterName, Integer filterType){
			String[] dropDownList = new String[filter.size()+1];
			dropDownList[0] = "No "+ filterName +" filter";
			int i = 1;
			if(filters.contains(filterName)){
				System.out.println("A filter with this name is already defined!");
				return false;
			}else{
				
				for(String key : filter.keySet()){
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
				pack();
				return true;
			}
		
		}
		public HashMap<Integer,String> getSelectedFilters(){
			return this.selectedFilters;
		}
		private class SelectorListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			
				JComboBox comboBox = (JComboBox)e.getSource();
				System.out.println(comboBox.getName());
		        String category = (String)comboBox.getSelectedItem();
		    	if(selectedFilters.containsKey(comboBox.getName())){
		    		selectedFilters.replace(Integer.parseInt(comboBox.getName()),category);
		    	}else{
		    		selectedFilters.put(Integer.parseInt(comboBox.getName()),category);
		    	}
		    	String outputText = "";
		    	for(String filter : selectedFilters.values()){
		    		outputText += filter + "; \n";
		    	}
		    	
		    	filtersAppliedText.setText(outputText);
			}
		}
		private class MouseListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			
				JButton button = (JButton)e.getSource();
				String action = button.getActionCommand();
				switch (action){
					case "SEARCH":
						drawSearch();
						break;
					case "STATS":
						drawStatistics();
						break;
					case "RESULT":
						drawResult();
						break;
					case "SEARCHBUTTON":
						mng.preformSearch();
					//	System.out.println("PREFORM SEARCH");
						break;
				}

			}
		}
//		public static void main(String[] args){
//
//		}
}
