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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import no.uio.ifi.management.ManagementFilteredSearch;

public class FilteredSearchGui extends JFrame{
		private JPanel contentPane = new JPanel();
		
		private FilterGui searchWindow;
		private Statistics statsWindow;
		
		private MouseClickListener mouseListener = new MouseClickListener();
		private JLabel searchMenu = new JLabel("SEARCH",SwingConstants.CENTER); 
		private JLabel resultMenu = new JLabel("RESULT",SwingConstants.CENTER);
		private JLabel statsMenu = new JLabel("STATISTICS",SwingConstants.CENTER);
		public static final Dimension CONTENT_PANE_SIZE = new Dimension(1000,600);
		public static final Dimension MENU_BUTTON_SIZE = new Dimension(150,30);
		Color menuColor = Color.LIGHT_GRAY;
		JLabel activeButton;
		Font menuButtonFont =   new Font("Areal", Font.PLAIN, 8);
		
		Border border = LineBorder.createGrayLineBorder();// BorderFactory.createRaisedBevelBorder();

		private JTextArea resultIdList = new JTextArea();
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
		public JPanel getResultPanel(){
			JPanel resultPanel = new JPanel();
			resultPanel.add(resultIdList);
			return resultPanel;
		}
		public JPanel getMenuPanel(){
			JPanel menuPanel = new JPanel();
			menuPanel.setBackground(menuColor);
			System.out.println(searchMenu.getBackground());
//			searchMenu.setBorder(border);
//			resultMenu.setBorder(border);
//			statsMenu.setBorder(border);
			
			searchMenu.setPreferredSize(MENU_BUTTON_SIZE);
			resultMenu.setPreferredSize(MENU_BUTTON_SIZE);
			statsMenu.setPreferredSize(MENU_BUTTON_SIZE);
			
			searchMenu.addMouseListener(mouseListener);
			resultMenu.addMouseListener(mouseListener);
			statsMenu.addMouseListener(mouseListener);
			
			searchMenu.setOpaque(true);
			resultMenu.setOpaque(true);
			statsMenu.setOpaque(true);
			
//			FontFactory.changeFont(menuPanel, menuButtonFont);
			
			searchMenu.setBackground(new Color(238,238,238));
			searchMenu.setFont(searchMenu.getFont().deriveFont(Font.BOLD));
			activeButton = searchMenu;
			resultMenu.setBackground(Color.LIGHT_GRAY);
			statsMenu.setBackground(Color.LIGHT_GRAY);
			
			menuPanel.setPreferredSize(new Dimension(1000, 30));
			menuPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
			menuPanel.add(searchMenu);
			menuPanel.add(resultMenu);
			menuPanel.add(statsMenu);
			
			
			
			pack();
			return menuPanel;
		}
		
		public void drawSearch(){
			setTitle("YTDownloader ~ Filtered search");
			((CardLayout) contentPane.getLayout()).show(contentPane, "SEARCH");
	//		
		}
		public void drawResult(){
			setTitle("YTDownloader ~ Result");
			((CardLayout) contentPane.getLayout()).show(contentPane, "RESULT");
		}
		public void drawStatistics(){
			setTitle("YTDownloader ~ Statistics");
			((CardLayout) contentPane.getLayout()).show(contentPane, "STATS");

		}
		
		public boolean addFilterBox(Map<String, String> filter, String filterName, Integer filterType){
			boolean result =  (searchWindow.addFilterBox(filter, filterName, filterType));
			return result;
		}
		public HashMap<Integer,String> getSelectedFilters(){
			return searchWindow.getSelectedFilters();
		}
		public void addVideoResult(String videoId){
			resultIdList.append(videoId);
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
				activeButton.setBackground(menuColor);
				activeButton.setFont(button.getFont().deriveFont(Font.PLAIN));
				activeButton = button;
				button.setBackground(new Color(238,238,238));
				button.setFont(button.getFont().deriveFont(Font.BOLD));
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
				
				//button.setForeground(Color.GREEN);
				// TODO Auto-generated method stub
//				button.setBackground(new Color(238,238,238));
				
			}
		}
}
