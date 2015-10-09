package no.uio.ifi.guis;

import no.uio.ifi.management.ManagementAll;
import no.uio.ifi.models.DownloadThread;
import no.uio.ifi.models.SingleVideoINFO;
import no.uio.ifi.models.YoutubeDownloader;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

public class MainGUI extends JFrame{
	
	//MainGUI buttons
	JButton downloadButton, crawlerButton, statisticButton;//and more
	
	//Module search
	boolean mainwindow = true;
	JPanel initialSear;
	
	//Module Download attributes
	SingleVideoINFO dataVideoLink;
	JLabel thumbnailPic_Down;
	JPanel mainPanelDownload;
	String videoTittle;
	
	public ManagementAll mng;
	public JTextField search_Key = null;
	public JTextField search_City = null;
	public JTextField search_Distance = null;
	final int NRELEM = 25;
	
	public ImageIcon ourPicture = null;
	
	public JButton searchButton = null;
	
	
	public MainGUI(ManagementAll mng){
		super("INF5090 GROUP1 DASH-YOUTUBE DOWNLOAD");
		this.mng = mng;
		init();
	}
	
	void init(){
		search_Key = new JTextField();
		search_Key.setColumns(15);
		search_City = new JTextField();
		search_City.setColumns(15);
		search_Distance = new JTextField();
		search_Distance.setColumns(10);
		//System.out.println(System.getProperty("user.dir"));
		ourPicture = new ImageIcon(System.getProperty("user.dir")+"/src/main/java/no/uio/ifi/pictures/doremon.png");
		searchButton = new JButton("Search");
		ButtonsListen button_listen = new ButtonsListen();
		searchButton.addActionListener(button_listen);
		
		this.setLayout(new BorderLayout());
		
		initialSear = initialSearchView();
		this.getContentPane().add(initialSear, BorderLayout.CENTER);
		this.getContentPane().add(functionButtons(button_listen), BorderLayout.PAGE_END);
		
		this.pack();
		//this.setSize(700, 700);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	JPanel initialSearchView(){
		JPanel mainSear = new JPanel(new BorderLayout());
		JPanel head = createHead();
		JPanel tail = createTail();
		mainSear.add(head,BorderLayout.LINE_START);
		mainSear.add(tail,BorderLayout.LINE_END);
		return mainSear;
	}
	
	private JPanel createTail() {
		JPanel tail = new JPanel();
		tail.setLayout(new BorderLayout());
		
		tail.add(new JLabel(ourPicture), BorderLayout.CENTER);
		
		return tail;
	}

	private JPanel createHead() {
		JPanel head = new JPanel();
		
		head.setLayout(new BoxLayout(head, BoxLayout.PAGE_AXIS));
		
		head.add(new JLabel("Search Key"));
		head.add(search_Key);
		head.add(new JLabel("City (option)"));
		head.add(search_City);
		head.add(new JLabel("Distance (option) Example: 100km"));
		head.add(search_Distance);
		head.add(searchButton);

		return head;
	}

	private JPanel functionButtons(ButtonsListen button_listen){
		JPanel funcs = new JPanel(new FlowLayout());
		
		downloadButton = new JButton("Download");
		downloadButton.setEnabled(false);
		crawlerButton = new JButton("Crawler");
		crawlerButton.setEnabled(false);
		statisticButton= new JButton("Statistic");
		statisticButton.setEnabled(false);
		
		funcs.add(downloadButton);
		funcs.add(crawlerButton);
		funcs.add(statisticButton);
		downloadButton.addActionListener(button_listen);
		crawlerButton.addActionListener(button_listen);
		statisticButton.addActionListener(button_listen);
		return funcs;
	}

	private class ButtonsListen implements ActionListener{
		public void actionPerformed(ActionEvent e){
			Object event = e.getSource();
			if(event == searchButton){
				if(mainwindow) {
					getContentPane().remove(initialSear);
					mainwindow = false;
				}else{
					getContentPane().remove(mainPanelDownload);
				}
				
				mainwindow = false;
				String key = search_Key.getText();
				String city = search_City.getText();
				String dist = search_Distance.getText();
				ListIterator<SearchResult> result = null;
				ListIterator<Video> resultGeo = null;
				if(city.equals("")){
					System.out.println("SEARCH CLICKED");
					result = mng.searchBaseOnKeyWord(key);
					
					System.out.println("HAS RESULT NOW");
					JPanel tmp = createDownLoad_View_Module(result);
					
					System.out.println("HAS BUILD DL MODULE");
					getContentPane().add(tmp,BorderLayout.CENTER);
					revalidate();
					pack();
				}else{
					if (dist.equals("")) dist = "100km";
					resultGeo = mng.searchOnGeolocation(key, city, dist);
					
				}
			} else if(event == downloadButton){
				System.out.println("DOWNLOAD CLICKED.");
				System.out.println(dataVideoLink.downloadLink);
				downloadButton.setEnabled(false);
				new Thread(new DownloadThread(dataVideoLink,videoTittle)).start();
			}else if(event == crawlerButton){
				
			}else if(event == statisticButton){
				
			}
			
		}
		
	}
	
	JPanel createDownLoad_View_Module(ListIterator<SearchResult> result){
		
		mainPanelDownload = new JPanel(new BorderLayout());
		JPanel searchModule = new JPanel(new FlowLayout());
		searchModule.add(new JLabel("Search Key"));
		searchModule.add(search_Key);
		searchModule.add(new JLabel("City (option)"));
		searchModule.add(search_City);
		searchModule.add(new JLabel("Distance (option) Example: 100km"));
		searchModule.add(search_Distance);
		searchModule.add(searchButton);
		
		mainPanelDownload.add(searchModule,BorderLayout.PAGE_START);
		JScrollPane jspanelUp= createJPanelResultUp_DL_Module(result);
		//JPanel jpanelDown = createTailPanel_DL_Module(null);
		mainPanelDownload.add(jspanelUp, BorderLayout.CENTER);
		//mainPanelDownload.add(jpanelDown, BorderLayout.PAGE_END);
		return mainPanelDownload;
	}
	
	JScrollPane createJPanelResultUp_DL_Module(ListIterator<SearchResult> result){
		JPanel jpanelR_UP = new JPanel();
		JScrollPane listScroller = new JScrollPane(jpanelR_UP, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jpanelR_UP.setLayout(new BoxLayout(jpanelR_UP, BoxLayout.PAGE_AXIS));
		Iterator<SearchResult> it = (Iterator<SearchResult>)result;
		
		ResultElem[] outerScope = new ResultElem[NRELEM];
		
		int cnt = 0;
		it = (Iterator<SearchResult>)result;
		while (it.hasNext()) {
            SearchResult sVideo = it.next();
            ResourceId rId = sVideo.getId();
            if (rId.getKind().equals("youtube#video")) {
            	ResultElem re = new ResultElem(sVideo, outerScope);
            	outerScope[cnt++] = re;
            	jpanelR_UP.add(re);
            }
        }
		
		return listScroller;
		
	}
	
	class ResultElem extends JPanel implements MouseListener{
		
		SearchResult sr;
		
		JPanel jp_tail;
		JLabel jjtitle, jjLink;
		JLabel jicon;
		public String videoLink;
		
		ResultElem[] outerScope;
		boolean selected = false;
		public ResultElem(SearchResult sr, ResultElem[] outerScope){
			this.sr = sr;
			this.outerScope = outerScope;
			setBorder(BorderFactory.createLineBorder(Color.GRAY));
			createInfoForElem();
			addMouseListener(this);
		}
		
		public void createInfoForElem(){
			this.setLayout(new BorderLayout());
			
			String urlThumbnail = sr.getSnippet().getThumbnails().getDefault().getUrl();
			videoLink = "https://www.youtube.com/watch?v="+sr.getId().getVideoId();
			//System.out.println(videoLink);
			String title = sr.getSnippet().getTitle();
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
			System.out.println("SELECTED "+sr.getSnippet().getTitle());
			videoTittle = sr.getSnippet().getTitle();
			for(int i = 0; i < outerScope.length; i++){
				if(outerScope[i] != null && outerScope[i].selected == true){
					outerScope[i].jp_tail.setBackground(Color.WHITE);
					outerScope[i].jjtitle.setFont(new Font("Serif", Font.BOLD, 18));
					outerScope[i].jjLink.setFont(new Font("Serif", Font.ROMAN_BASELINE, 16));
					outerScope[i].selected = false;
					break;
				}
			}
			selected = true;
			jjtitle.setFont(new Font("Serif", Font.BOLD, 21));
			jjLink.setFont(new Font("Serif", Font.ROMAN_BASELINE, 19));
			jp_tail.setBackground(Color.PINK);
			JPanel pjn = createTailPanel_DL_Module(this);
			mainPanelDownload.add(pjn, BorderLayout.PAGE_END);
			mainPanelDownload.revalidate();
			revalidate(); setSize(500, 500);
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
	
	JPanel createTailPanel_DL_Module(ResultElem relem){
		JPanel below_Part = new JPanel(new BorderLayout());
		String urlThumbnail = relem.sr.getSnippet().getThumbnails().getDefault().getUrl();
		URL url = null;
		BufferedImage image =null;
		try{
			url = new URL(urlThumbnail);
			image = ImageIO.read(url);
		}catch(Exception e){}
		
		thumbnailPic_Down = new JLabel(new ImageIcon(image));
		
		below_Part.add(thumbnailPic_Down, BorderLayout.LINE_START);
		JScrollPane onTheRight = createSingleVideoPanel_Right(relem);
		below_Part.add(onTheRight, BorderLayout.CENTER);
		return below_Part;
	}
	
	JScrollPane createSingleVideoPanel_Right(ResultElem relem){
		JPanel jpanelR_DOWN_R = new JPanel();
		JScrollPane listScroller = new JScrollPane(jpanelR_DOWN_R, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jpanelR_DOWN_R.setLayout(new BoxLayout(jpanelR_DOWN_R, BoxLayout.PAGE_AXIS));
		if(relem != null){
			YoutubeDownloader yt_dl = new YoutubeDownloader(relem.videoLink);
			HashMap<String,SingleVideoINFO> sglVideoinfo = yt_dl.get_single_video_info();
			ArrayList<SingleVideoElemInfo> outerscope = new ArrayList<SingleVideoElemInfo>();
			for(SingleVideoINFO s : sglVideoinfo.values()){
				SingleVideoElemInfo svei = new SingleVideoElemInfo(s,outerscope);
				outerscope.add(svei);
				jpanelR_DOWN_R.add(svei);
			}
			
		}
		return listScroller;
	}
	
	class SingleVideoElemInfo extends JPanel implements MouseListener{
		SingleVideoINFO sglVideoinfo;
		ArrayList<SingleVideoElemInfo> outerscope;
		public SingleVideoElemInfo(SingleVideoINFO sglVideoinfo, ArrayList<SingleVideoElemInfo> outerscope){
			this.outerscope = outerscope;
			this.sglVideoinfo = sglVideoinfo;
			this.setLayout(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(Color.GRAY));
			add(new JLabel(sglVideoinfo.toString()));
			setBackground(Color.WHITE);
			setBorder(BorderFactory.createLineBorder(Color.GRAY));
			addMouseListener(this);
		}
		
		public void mouseClicked(MouseEvent e){
			for(SingleVideoElemInfo s: outerscope){
				if(s != this){
					s.setBackground(Color.WHITE);
				}
			}
			dataVideoLink = sglVideoinfo;
			downloadButton.setEnabled(true);
			setBackground(Color.PINK);
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
	
}
