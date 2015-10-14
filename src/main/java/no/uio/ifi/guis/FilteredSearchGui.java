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
		
		private FilterGui searchWindow;
		
		private JPanel resultWindow = new JPanel();
		private Statistics statsWindow; 
		private JPanel menuPanel = new JPanel();
		private JPanel resultPanel = new JPanel();
		private MouseListener mouseListener = new MouseListener ();
		private JButton searchMenu = new JButton("Search"); 
		private JButton resultMenu = new JButton("Result");
		private JButton statsMenu = new JButton("Statistics");
		public static final Dimension CONTENT_PANE_SIZE = new Dimension(1000,600);
		public static final Dimension MENU_BUTTON_SIZE = new Dimension(100,40);

		private ManagementFilteredSearch mng;
		
		public FilteredSearchGui(ManagementFilteredSearch mng){
			this.mng = mng;
			this.searchWindow = new FilterGui(this.mng);
			this.statsWindow = new Statistics();
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
			drawMenuBar();
			
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
			return (searchWindow.addFilterBox(filter, filterName, filterType));
		}
		public HashMap<Integer,String> getSelectedFilters(){
			return searchWindow.getSelectedFilters();
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
				}

			}
		}
}
