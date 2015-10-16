package no.uio.ifi.models;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFrame;

public class DownloadThread implements Runnable{
	SingleVideoINFO urlStreamObj;
	String videoTittle;
	public DownloadThread(SingleVideoINFO urlStreamObj, String videoTittle){
		this.urlStreamObj = urlStreamObj;
		this.videoTittle = videoTittle;
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

            File f = new File(System.getProperty("user.dir")+"/"+videoTittle+urlStreamObj.type);

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

class StatusDownload extends JFrame{
	
	public StatusDownload(){
		super("DOWNLOAD VIDEO");
	}
}
