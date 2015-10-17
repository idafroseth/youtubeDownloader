package no.uio.ifi.models;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
	JTextField elem[];
	public DownloadThread(HashMap<String,SingleVideoINFO> set_of_sglVideoinfo){
		super("DOWNLOAD VIDEO");
		this.set_of_sglVideoinfo = set_of_sglVideoinfo;
		
	}
	
	public void run(){
		elem = new JTextField[set_of_sglVideoinfo.size()];
		this.setLayout(new FlowLayout());
		this.getContentPane().add(createInnhold());
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	JPanel createInnhold(){
		JPanel mainPanel = new JPanel();
		JScrollPane listScroller = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		int i = 0;
		for(SingleVideoINFO v :set_of_sglVideoinfo.values()){
			elem[i] = new JTextField(v.toString());
			mainPanel.add(elem[i++]);
		}
		
		return mainPanel;
	}
	
	
}

class StatusDownload implements Runnable{
	SingleVideoINFO urlStreamObj;
	public StatusDownload(SingleVideoINFO urlStreamObj){
		this.urlStreamObj = urlStreamObj;
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

            File f = new File(System.getProperty("user.dir")+"/"+urlStreamObj.itag+urlStreamObj.videoTitle+urlStreamObj.type);

            RandomAccessFile fileOutputStream = new RandomAccessFile(f,"rw");
            BufferedInputStream breader = new BufferedInputStream(connect.getInputStream());
            byte[] bytesBuffRead = new byte[4096];
            int nrBytesRead  = breader.read(bytesBuffRead);
            System.out.println("DOWNLOADDING.....");
            while(nrBytesRead > 0){
                fileOutputStream.write(bytesBuffRead,0,nrBytesRead);
                nrBytesRead  = breader.read(bytesBuffRead);
            }
            System.out.println("DOWNLOAD FINISHED");
            breader.close();
            fileOutputStream.close();
            
        }catch(Exception e){
            System.out.println("CAN NOT SAVE FILE "+e);
        }

    }
}
