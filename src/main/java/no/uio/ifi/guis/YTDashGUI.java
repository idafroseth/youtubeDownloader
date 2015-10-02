package no.uio.ifi.guis;

import no.uio.ifi.management.ManagementAll;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.io.File;

public class YTDashGUI extends JFrame{

	/**
	 * I don't know why it give warning when I do not put this versionUID
	 */
	private static final long serialVersionUID = -6248064151272849308L;
	//Important attribute of GUI
	public String filePath = null;
	public String linkYoutube = null;
	public ManagementAll mng;

	//Button on the GUI
	public JButton randomQ_JB, categoryQ_JB, popularQ_JB, search_JB;
	public JButton viewMdata_JB, exportMdata_JB, download_JB;

	//Textfield
	public JTextField search_Text = null;
	public JTextArea videoQueryList_Text = null;
	public JTextArea videoInfo_Text = null;

	//Radio
	public JRadioButton p1080, p720, sd, mp4video, flv, audio;

	public YTDashGUI(ManagementAll mng){
		this.mng = mng;
		init();

		this.setLayout(new BorderLayout());
		
		JPanel head = createHead();
		JPanel body = createBody();
		JPanel tail = createTail();

		getContentPane().add(head,BorderLayout.NORTH);
		getContentPane().add(body,BorderLayout.CENTER);
		getContentPane().add(tail,BorderLayout.SOUTH);

		//this.pack();
		this.setSize(1200, 700);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void init(){
		p1080 = new JRadioButton("1080p");
		p720 = new JRadioButton("p720");
		sd = new JRadioButton("sd");
		mp4video = new JRadioButton("MP4 Video");
		flv = new JRadioButton("FLV");
		audio = new JRadioButton("audio");

		search_Text = new JTextField("key search here");
		videoQueryList_Text = new JTextArea("Video Query List");
		videoInfo_Text = new JTextArea("Video Information");
				
		videoQueryList_Text.setColumns(50);
		videoInfo_Text.setColumns(50);
		videoQueryList_Text.setLineWrap(true);
		videoInfo_Text.setLineWrap(true);
		
		videoInfo_Text.setLineWrap(true);
		videoInfo_Text.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		videoQueryList_Text.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		randomQ_JB = new JButton("Random Query"); 
		categoryQ_JB = new JButton("Category Query");  
		popularQ_JB = new JButton("Popularity Query");
		viewMdata_JB = new JButton("View Metadata");
		exportMdata_JB = new JButton("Export Metadata");
		download_JB = new JButton("Download Video");
		search_JB = new JButton("Search");

		ButtonsListen button_listen = new ButtonsListen();
		randomQ_JB.addActionListener(button_listen);
		categoryQ_JB.addActionListener(button_listen);
		popularQ_JB.addActionListener(button_listen);
		viewMdata_JB.addActionListener(button_listen);
		exportMdata_JB.addActionListener(button_listen);
		download_JB.addActionListener(button_listen);
		search_JB.addActionListener(button_listen);
	}

	public JPanel createHead(){
		
		JPanel head = new JPanel();
		head.setLayout(new BorderLayout());

		head.add(head_1_part(),BorderLayout.CENTER);
		head.add(head_2_part(), BorderLayout.LINE_END);

		return head;
	}

	JPanel head_1_part(){
		JPanel head_1 = new JPanel();
		head_1.setLayout(new BorderLayout());
		head_1.add(head_1_1(),BorderLayout.NORTH);
		head_1.add(head_1_2(),BorderLayout.SOUTH);

		return head_1;
	}

	JPanel head_1_1(){
		JPanel head_1_1 = new JPanel();
		head_1_1.setLayout(new BorderLayout());
		head_1_1.add(search_Text,BorderLayout.CENTER);
		head_1_1.add(search_JB,BorderLayout.LINE_END);
		return head_1_1;
	}

	JPanel head_1_2(){
		JPanel head_1_2 = new JPanel();
		head_1_2.setLayout(new FlowLayout()); 
		head_1_2.add(randomQ_JB);
		head_1_2.add(categoryQ_JB);
		head_1_2.add(popularQ_JB);

		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());
		ret.add(head_1_2,BorderLayout.LINE_START);
		return ret;
	}

	JPanel head_2_part(){
		JPanel head_2 = new JPanel();
	
		head_2.setLayout(new GridLayout(3,2));

		head_2.add(p1080);
		head_2.add(p720);
		head_2.add(sd);
		head_2.add(mp4video);
		head_2.add(flv);
		head_2.add(audio);		

		return head_2;
	}

	public JPanel createBody(){
		JPanel body = new JPanel();
		body.setLayout(new BorderLayout());

		JPanel body_1 = new JPanel();
		body_1.setLayout(new BorderLayout());
		body_1.add(new JScrollPane (videoQueryList_Text, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), 
				BorderLayout.LINE_START);
		body_1.add(new JLabel("  "), BorderLayout.LINE_END);
		
		body.add(body_1, BorderLayout.LINE_START);
		body.add(new JScrollPane (videoInfo_Text, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), 
				BorderLayout.CENTER);

		return body;
	}

	public JPanel createTail(){
		
		JPanel tail = new JPanel();
		tail.setLayout(new BorderLayout());

		JPanel tail_end = new JPanel(new FlowLayout());
		tail_end.add(viewMdata_JB);
		tail_end.add(exportMdata_JB);
		tail_end.add(download_JB);

		tail.add(tail_end, BorderLayout.LINE_END);

		return tail;
	}


	private class ButtonsListen implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			Object event = e.getSource();
			if(event == download_JB){
				File pwd = new File(System.getProperty("user.dir"));
				JFileChooser jFChooser = new JFileChooser();
				jFChooser.setCurrentDirectory(pwd);
				int chosen = jFChooser.showSaveDialog(null);
				if(JFileChooser.APPROVE_OPTION == chosen){
					try{
						File file = jFChooser.getSelectedFile();
						if(file.exists()){
							int choose = JOptionPane.showConfirmDialog(null,"Overwritten file?","File exists",JOptionPane.YES_NO_OPTION);
							if(choose == JOptionPane.NO_OPTION) return;
						}
						
						JOptionPane.showMessageDialog(null,"Call the save file to "+file.getAbsolutePath());
					} catch (Exception ex){
						JOptionPane.showMessageDialog(null,"Problem with save file");
					}
				}
			}else if(event == search_JB){
				System.out.println(search_Text.getText());
				videoQueryList_Text.setText("Video Query List\n");
				mng.searchBaseOnKeyWord(search_Text.getText());
			}else if(event == randomQ_JB){
				System.out.println("RANOM has clicked!!");
			}else if(event == categoryQ_JB){
				System.out.println("category has clicked!!");
			}else if(event == popularQ_JB){
				System.out.println("popular has clicked!!");
			}else if(event == viewMdata_JB){
				System.out.println("Viewmeta has clicked!!");
			}else if(event == exportMdata_JB){
				System.out.println("Export has clicked!!");
			}
		}
		
	}
}




