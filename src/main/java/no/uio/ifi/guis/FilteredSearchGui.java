package no.uio.ifi.guis;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import no.uio.ifi.management.ManagementFilteredSearch;

public class FilteredSearchGui extends JFrame{
		private JPanel contentPane = new JPanel();
		
		private FilterGui searchWindow;
		private Statistics statsWindow;
		
		private MouseClickListener mouseListener = new MouseClickListener();
		private JLabel searchMenu = new JLabel("SEARCH",SwingConstants.CENTER); 
		private JLabel resultMenu = new JLabel("RESULT",SwingConstants.CENTER);
		private JLabel statsMenu = new JLabel("STATISTICS",SwingConstants.CENTER);
		public static final Integer WINDOW_WIDTH = 1200;
		public static final Integer WINDOW_HEIGHT = 600;
		public static final Dimension CONTENT_PANE_SIZE = new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT);
		public static final Dimension MENU_BUTTON_SIZE = new Dimension(150,30);
		Color menuColor = Color.LIGHT_GRAY;
		JLabel activeButton;
		Font menuButtonFont =   new Font("Arial", Font.PLAIN, 10);
		
		Border border = LineBorder.createGrayLineBorder();// BorderFactory.createRaisedBevelBorder();

		//private JTextArea resultIdList = new JTextArea();
		JPanel resultPanel;
		private ManagementFilteredSearch mng;
		/**
		 * Constructor
		 * @param mng indicates the management for this gui
		 */
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
			contentPane.add(getResultPanel(), "RESULT");
			contentPane.add(statsWindow, "STATS");
			
			this.setVisible(true);
			this.setLayout(new BorderLayout());
			this.getContentPane().add(contentPane, BorderLayout.CENTER);
			this.getContentPane().add(getMenuPanel(), BorderLayout.PAGE_START);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			drawSearch();

//			changeFont(this, new Font("Courier New", Font.PLAIN, 15));
		
			pack();
		}
		
		/**
		 * Return a panel with the result
		 * @return
		 */
		public JPanel getResultPanel(){
			resultPanel = new JPanel();
			//resultPanel.add(resultIdList);
			return resultPanel;
		}
		
		/**
		 * 
		 * @return a panel with the buttons for the menu
		 */
		public JPanel getMenuPanel(){
			JPanel menuPanel = new JPanel();
			menuPanel.setBackground(menuColor);
			System.out.println(searchMenu.getBackground());
			
			searchMenu.setPreferredSize(MENU_BUTTON_SIZE);
			resultMenu.setPreferredSize(MENU_BUTTON_SIZE);
			statsMenu.setPreferredSize(MENU_BUTTON_SIZE);
			
			searchMenu.addMouseListener(mouseListener);
			resultMenu.addMouseListener(mouseListener);
			statsMenu.addMouseListener(mouseListener);
			
			searchMenu.setOpaque(true);
			resultMenu.setOpaque(true);
			statsMenu.setOpaque(true);
			
			searchMenu.setBackground(new Color(238,238,238));
			searchMenu.setFont(searchMenu.getFont().deriveFont(Font.BOLD));
			activeButton = searchMenu;
			resultMenu.setBackground(Color.LIGHT_GRAY);
			statsMenu.setBackground(Color.LIGHT_GRAY);
			
			menuPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, 30));
			menuPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
			menuPanel.add(searchMenu);
			menuPanel.add(resultMenu);
			menuPanel.add(statsMenu);
			
			
			
			pack();
			return menuPanel;
		}
		/**
		 * Draw that search tab. This is invoked when the search menu button is hit
		 */
		private void drawSearch(){
			setTitle("YTDownloader ~ Filtered search");
			((CardLayout) contentPane.getLayout()).show(contentPane, "SEARCH");	
		}
		/**
		 * Draw that result tab. This is invoked when the result menu button is hit
		 */
		private void drawResult(){
			setTitle("YTDownloader ~ Result");
			((CardLayout) contentPane.getLayout()).show(contentPane, "RESULT");
		}
		/**
		 * Draw that statistics tab. This is invoked when the stat menu button is hit
		 */
		private void drawStatistics(){
			setTitle("YTDownloader ~ Statistics");
			((CardLayout) contentPane.getLayout()).show(contentPane, "STATS");

		}
		/**
		 * Add a filterbox to the search GUI
		 * @param filter contains a Map with the values and a name of the value
		 * @param filterName the name of the filter would be display as a lable before the filter
		 * @param filterType indicates the defined filter type in ManagementFilteredSearch
		 * @return
		 */
		public boolean addFilterBox(Map<String, String> filter, String filterName, Integer filterType){
			boolean result =  (searchWindow.addFilterBox(filter, filterName, filterType));
			return result;
		}
		
		/**
		 * Return the selected filters from the FilterGui
		 * @return
		 */
		public HashMap<Integer,String> getSelectedFilters(){
			return searchWindow.getSelectedFilters();
		}
		
		/*
		public void newResult(Map<String, Video> videoInfo){
			//First add all the info in the result
			int count = 0;
			for(String videoId : videoInfo.keySet()){
				count++;
				resultIdList.append(count + ": " +videoId + "\n");
			}
			setButtonAsActive(resultMenu);
			drawResult();
		}*/
		
		public void displayResult(JPanel resultcontain){
			if(resultPanel != null) contentPane.remove(resultPanel);
			contentPane.add(resultcontain,"RESULT");
			resultPanel = resultcontain;
			setButtonAsActive(resultMenu);
			drawResult();
		}
		
		
		
		private void setButtonAsActive(JLabel button){
			activeButton.setBackground(menuColor);
			activeButton.setFont(button.getFont().deriveFont(Font.PLAIN));
			activeButton = button;
			button.setBackground(new Color(238,238,238));
			button.setFont(button.getFont().deriveFont(Font.BOLD));
		}
		public Statistics getStatWindow(){
			return this.statsWindow;
		}

		private class MouseClickListener implements MouseListener{
			JLabel hovered = new JLabel();

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				JLabel button = (JLabel)e.getSource();
			//BorderFactory.createLoweredBevelBorder());
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				JLabel button = (JLabel)e.getSource();
				setButtonAsActive(button);
				String action = button.getText();
				switch (action){
					case "SEARCH":
						drawSearch();
						break;
					case "STATISTICS":
						drawStatistics();
						break;
					case "RESULT":
						drawResult();
						break;
				}
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				JLabel button = (JLabel)e.getSource();	
				button.setBackground(new Color(238,238,238));
				hovered = button; 
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				JLabel button = (JLabel)e.getSource();
				if(hovered != null && button != activeButton){
					button.setBackground(menuColor);
					hovered = null;
				}
				
			}
		}
}
