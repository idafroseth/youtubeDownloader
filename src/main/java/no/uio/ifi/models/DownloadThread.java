package no.uio.ifi.models;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.swing.*;

public class DownloadThread extends JFrame implements Runnable{
	private static final String JPanel = null;
	HashMap<String,SingleVideoINFO> set_of_sglVideoinfo;
	JLabel elem[];
	JLabel status[];
	String videoTittle;
	public DownloadThread(HashMap<String,SingleVideoINFO> set_of_sglVideoinfo, String videoTittle){
		super("DOWNLOAD VIDEO "+videoTittle);
		this.set_of_sglVideoinfo = set_of_sglVideoinfo;
		this.videoTittle = videoTittle;
		this.setBackground(Color.WHITE);
	}
	
	public void run(){
		elem = new JLabel[set_of_sglVideoinfo.size()];
		status = new JLabel[set_of_sglVideoinfo.size()];
		this.setLayout(new BorderLayout());
		this.getContentPane().add(createInnhold(), BorderLayout.CENTER);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		beginDownload();
		
	}
	
	JPanel createInnhold(){
		JPanel mainPanel = new JPanel(new FlowLayout());
		JScrollPane listScroller = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		int i = 0;
		for(SingleVideoINFO v :set_of_sglVideoinfo.values()){
			elem[i] = new JLabel(v.toString());
			status[i] = new JLabel("0/"+((v.len=="") ? "unknown" : v.len));
			mainPanel.add(elem[i]);mainPanel.add(status[i]);
			i++;
		}
		
		return mainPanel;
	}
	
	void beginDownload(){
		//create folder for this video
		File theDir = new File(System.getProperty("user.dir")+"/VideoDownloadFiles/"+videoTittle);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    System.out.println("creating directory: " + System.getProperty("user.dir")+"/VideoDownloadFiles/"+videoTittle);

		    try{
		        theDir.mkdir();
		    } 
		    catch(SecurityException se){
		        //handle it
		    	System.out.println("Fail to create the folder");
		    	return;
		    }        
		    
		} else {
			System.out.println("This video has been downloaded");
		}
		
		int i = 0;
		Thread[] t = new Thread[set_of_sglVideoinfo.size()];
		for(SingleVideoINFO v :set_of_sglVideoinfo.values()){
			t[i] = new Thread(new StatusDownload(v,this,i));
			t[i].start();
			i++;
		}
		
		for(i = 0; i<t.length ;i++){
			try{t[i].join();
			}catch(Exception e){ 
				this.dispose(); 
				JOptionPane.showMessageDialog(new JFrame(), "FEIL WITH DOWNLOAD", "ERROR",
			        JOptionPane.ERROR_MESSAGE);}
		}
		
		this.dispose();
		JOptionPane.showMessageDialog(new JFrame(), "DOWNLOAD ALL STREAM OF " + videoTittle +" FINISHED!!!", "DOWNLOAD HAS FINISHED",
		        JOptionPane.INFORMATION_MESSAGE);
	}
	
	
}

class StatusDownload implements Runnable{
	SingleVideoINFO urlStreamObj;
	DownloadThread dlThrd;
	int threadnr;
	public StatusDownload(SingleVideoINFO urlStreamObj, DownloadThread dlThrd, int threadnr){
		this.urlStreamObj = urlStreamObj;
		this.dlThrd = dlThrd;
		this.threadnr = threadnr;
	}

	public void run(){
		downloadStream(urlStreamObj.downloadLink);
	}
	
	public void downloadStream(String urlStream){
        try{
            URL url = new URL(urlStream);
            HttpURLConnection connect = (HttpURLConnection)url.openConnection();
            connect.setRequestProperty("User-Agent", "DASH-Group1-INF5090");
            connect.setConnectTimeout(20000);
            connect.setReadTimeout(20000);

            File f = new File(System.getProperty("user.dir")+"/VideoDownloadFiles/"+urlStreamObj.videoTitle+"/"+urlStreamObj.itag+" "+urlStreamObj.videoTitle+urlStreamObj.type);

            RandomAccessFile fileOutputStream = new RandomAccessFile(f,"rw");
            BufferedInputStream breader = new BufferedInputStream(connect.getInputStream());
            int readedbytes = 0;
            byte[] bytesBuffRead = new byte[102400];
            int nrBytesRead  = breader.read(bytesBuffRead);
            System.out.println("DOWNLOADDING.....");
            while(nrBytesRead > 0){
            	readedbytes+=nrBytesRead;
            	dlThrd.status[this.threadnr].setText(readedbytes+"/"+((urlStreamObj.len=="") ? "unknown" : urlStreamObj.len));
                fileOutputStream.write(bytesBuffRead,0,nrBytesRead);
                nrBytesRead  = breader.read(bytesBuffRead);
            }
            System.out.println("DOWNLOAD FINISHED");
            dlThrd.status[this.threadnr].setText("FINISHED!!!");
            breader.close();
            fileOutputStream.close();
            
        }catch(Exception e){
        	dlThrd.status[this.threadnr].setText("FEIL TO DOWNLOAD THIS STREAM!!!");
            System.out.println("CAN NOT SAVE FILE "+e);
        }

    }
}
