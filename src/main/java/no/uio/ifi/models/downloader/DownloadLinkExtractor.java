package no.uio.ifi.models.downloader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.google.api.services.youtube.model.SearchResult;

import no.uio.ifi.models.SingleVideoINFO;

public class DownloadLinkExtractor {
	private HashMap<String, HashMap<String, SingleVideoINFO>> downloadLinks;
	HashMap<HashMap<String, SingleVideoINFO>,SearchResult> forSaveOnly;
	DownloadVideoMonitor dlmonitor;
	boolean finished = false;
	public DownloadLinkExtractor(String pathfile){
		downloadLinks = new HashMap<String, HashMap<String, SingleVideoINFO>>();
		forSaveOnly = new HashMap<HashMap<String, SingleVideoINFO>,SearchResult>();
		if(pathfile != null) dlmonitor = new DownloadVideoMonitor(pathfile,forSaveOnly);
	}
	
	public String extract(SearchResult res){
		YoutubeDownloader yt = new YoutubeDownloader("https://www.youtube.com/watch?v="+res.getId().getVideoId());
		HashMap<String, SingleVideoINFO> links = yt.get_single_video_info();
	
		//PARSING TO JSON
		String json = ",\"videoLinks\":{"
				+ "\"downloadLink\":[{";
		int count = 0;
		for(String s : links.keySet()){
			System.out.println("********s " + s + " key " + links.get(s));
			json += "\"itag\":\""+s+"\",\"url\":\""+links.get(s).downloadLink+"\"}";
			count++;
			if(count == links.size()){
				json += "]}}";
			}else{
				json+=",{";
			}
		}
		//================================
		
		downloadLinks.put(res.getId().getVideoId(), links);
		//We have to decide which is the smallets video
		
		forSaveOnly.put(links, res);
		
		DownloadThread.nrOflinkextracted.setText("Number of video has been extracted download-links: " + (downloadLinks.size()));
		if(dlmonitor != null){
			dlmonitor.setnewDLlinks(links);
		}
		return json;
	}
	public void setfinishedExtr(){
		finished = true;
		if(dlmonitor != null){
			dlmonitor.finishedDL();
		}
	}
	
	public HashMap<String, HashMap<String, SingleVideoINFO>> getdownloadLinks(){
		return downloadLinks;
	}
	
	public void stopCall(){
		if(dlmonitor!= null){
			dlmonitor.dlTH.dispose();
		}
		
	}
	
}

class DownloadVideoMonitor{
	HashMap<HashMap<String, SingleVideoINFO>,SearchResult> forSaveOnly;
	public ArrayList<HashMap<String, SingleVideoINFO>> linkstoDL = new ArrayList<HashMap<String, SingleVideoINFO>>(); 
	public boolean havesomethingtodo = false, stopdl = false;
	String pathtoSave;
	DownloadThread dlTH;
	
	DownloadVideoMonitor(String pathtoSaved, HashMap<HashMap<String, SingleVideoINFO>,SearchResult> forSaveOnly){
		this.forSaveOnly = forSaveOnly;
		this.pathtoSave = pathtoSaved;
		(new Thread(dlTH = new DownloadThread(this,pathtoSave))).start();
	}
	
	synchronized void vent(){
		if(linkstoDL.size() > 0) return;
		while(!havesomethingtodo){
			try{
				wait();
			} catch (InterruptedException e){
				System.exit(0);
			}
		}
	}
	
	synchronized void finishedDL(){
		stopdl = true;
		havesomethingtodo = false;
		notify();
	}
	
	synchronized void setnewDLlinks(HashMap<String, SingleVideoINFO> links){
		linkstoDL.add(links);
		havesomethingtodo = true;
		if(dlTH != null && dlTH.res != null)
			dlTH.nrOflinkwaitforDL.setText("Current video is "+dlTH.res.getId().getVideoId()+" and "+linkstoDL.size() +" video(s) wait for download.");
		notify();
	}
	
}

class DownloadThread extends JFrame implements Runnable{
	DownloadVideoMonitor dlmonitor;
	private static final String JPanel = null;
	HashMap<String,SingleVideoINFO> set_of_sglVideoinfo;
	JLabel elem[];
	JLabel status[];
	String pathfile;
	SearchResult res;
	JScrollPane inhold;
	public static JLabel nrOflinkextracted = new JLabel("Number of video has been extracted download-links: ");
	JLabel nrOflinkwaitforDL = new JLabel("Current video and the number of video wait for download");
	public DownloadThread(DownloadVideoMonitor dlmonitor, String pathfile){
		super("DOWNLOAD VIDEO MONITOR");
		this.dlmonitor = dlmonitor;
		this.pathfile = pathfile;
	}
	
	public void run(){
		while(!dlmonitor.stopdl || dlmonitor.linkstoDL.size() > 0){
			dlmonitor.vent();
			set_of_sglVideoinfo = dlmonitor.linkstoDL.remove(0);
			
			res = dlmonitor.forSaveOnly.get(set_of_sglVideoinfo);
			
			//The links will expire therefore We just do JUST IN TIME extract download-link :)
			YoutubeDownloader yt = new YoutubeDownloader("https://www.youtube.com/watch?v="+res.getId().getVideoId());
			set_of_sglVideoinfo = yt.get_single_video_info();
			
			dl_init();
		}
		
	}
	
	void dl_init(){
		elem = new JLabel[set_of_sglVideoinfo.size()];
		status = new JLabel[set_of_sglVideoinfo.size()];
		this.setLayout(new BorderLayout());
		if(inhold != null) this.getContentPane().remove(inhold);
		setTitle("Curr_video title - "+res.getSnippet().getTitle());
		inhold = createInnhold();
		this.getContentPane().add(inhold, BorderLayout.CENTER);
		this.getContentPane().add(nrOflinkextracted, BorderLayout.PAGE_END);
		this.getContentPane().add(nrOflinkwaitforDL, BorderLayout.PAGE_START);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		beginDownload();
	}
	
	JScrollPane createInnhold(){
		JPanel mainPanel = new JPanel(new FlowLayout());
		mainPanel.setBackground(Color.WHITE);
		JScrollPane listScroller = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		int i = 0;
		for(SingleVideoINFO v :set_of_sglVideoinfo.values()){
			elem[i] = new JLabel(v.toString());
			elem[i].setFont(new Font("Serif", Font.ROMAN_BASELINE, 12));
			status[i] = new JLabel("0/"+((v.len=="") ? "unknown" : v.len));
			status[i] .setFont(new Font("Serif", Font.BOLD, 12));
			mainPanel.add(elem[i]);
			mainPanel.add(status[i]);
			i++;
		}
		
		return listScroller;
	}
	
	void beginDownload(){
		//create folder for this video
		File theDir = new File(pathfile+"/"+res.getId().getVideoId());

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			
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
			t[i] = new Thread(new StatusDownload(v,this,i,pathfile,res));
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
		this.setVisible(false);
		//JOptionPane.showMessageDialog(new JFrame(), "DOWNLOAD ALL STREAM OF " + dlmonitor.videoID +" FINISHED!!!", "DOWNLOAD HAS FINISHED",
		//        JOptionPane.INFORMATION_MESSAGE);
	}
	
	
}

class StatusDownload implements Runnable{
	SingleVideoINFO urlStreamObj;
	DownloadThread dlThrd;
	int threadnr;
	String filepath;
	SearchResult res;
	public StatusDownload(SingleVideoINFO urlStreamObj, DownloadThread dlThrd, int threadnr, String filepath, SearchResult res){
		this.urlStreamObj = urlStreamObj;
		this.dlThrd = dlThrd;
		this.threadnr = threadnr;
		this.filepath = filepath;
		this.res = res;
	}

	public void run(){
		downloadStream(urlStreamObj.downloadLink);
	}
	
	public void downloadStream(String urlStream){
		File f = null;
        try{
        	//System.out.println(urlStream);
            URL url = new URL(urlStream);
            HttpURLConnection connect = (HttpURLConnection)url.openConnection();
            connect.setRequestProperty("User-Agent", "DASH-Group1-INF5090");
            connect.setConnectTimeout(20000);
            connect.setReadTimeout(20000);

            f = new File(filepath+"/"+res.getId().getVideoId()+"/"+urlStreamObj.itag+" "+res.getSnippet().getTitle()+urlStreamObj.type);

            RandomAccessFile fileOutputStream = new RandomAccessFile(f,"rw");
            BufferedInputStream breader = new BufferedInputStream(connect.getInputStream());
            int readedbytes = 0;
            byte[] bytesBuffRead = new byte[9192];
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
            if(f != null) f.delete();
        }

    }
}

class YoutubeDownloader{

    String urlIn;

    public YoutubeDownloader(String url){
        this.urlIn = url;
    }

    public HashMap<String,SingleVideoINFO> get_single_video_info(){
    	HashMap<String,SingleVideoINFO> listSingleVideo = new HashMap<String,SingleVideoINFO>();
    	String html = getContentHtmlOfLink(urlIn);
        
        ArrayList<String> listStreamLinks = listStreamRaw(html);
        
        for(String urlFull : listStreamLinks){
	        
        	//downloadlink
        	String downloadLink = unescape(urlFull);
	        Pattern link = Pattern.compile("([^&,]*)[&,]");
	        Matcher linkMatch = link.matcher(downloadLink);
	        if (linkMatch.find()) downloadLink = linkMatch.group(1);
	            
	        try{
	        	downloadLink = URLDecoder.decode(downloadLink, "UTF-8");
	        	downloadLink = URLDecoder.decode(downloadLink, "UTF-8");
	        }catch(Exception e){System.out.println(e);}
	        
	        
	        try{urlFull = URLDecoder.decode(urlFull, "UTF-8");}catch(Exception e){System.out.println(e);}
	        
	        //itag
	        String itag = null;
	        Pattern patItag = Pattern.compile("itag=(\\d+)");
	        Matcher matchItag = patItag.matcher(urlFull);
	        if (matchItag.find()) itag = matchItag.group(1);
	        if(itag == null){
	        	System.out.println(urlFull);
	        	itag = "";
	        }
	        
	        //type
	        String type = null;
	        Pattern patType = Pattern.compile("mime=([^&,]*)[&,]");
	        Matcher matchType = patType.matcher(downloadLink);
	        if (matchType.find()) type = matchType.group(1);
	        if(type == null) type = "";
	        
	        //dur
	        String dur = null;
	        Pattern patDur = Pattern.compile("dur=(\\d+)\\.(\\d+)");
	        Matcher matchDur = patDur.matcher(urlFull);
	        if (matchDur.find()) dur = matchDur.group(1);
	        if(dur == null) dur = "";
	        
	        //len
	        String len = null;
	        Pattern patLen = Pattern.compile("clen=(\\d+)");
	        Matcher matchLen = patLen.matcher(urlFull);
	        if (matchLen.find()) len = matchLen.group(1);
	        if(len == null) len = "";
	        
	        //size
	        String size = null;
	        Pattern patSize = Pattern.compile("size=(\\d+)x(\\d+)");
	        Matcher matchSize = patSize.matcher(urlFull);
	        if (matchSize.find()) size = matchSize.group(1)+"x"+matchSize.group(2);
	        if(size == null) size = "";
	        
	        //quality_label
	        String quality_label = null;
	        Pattern patQuality_label = Pattern.compile("quality_label=(\\d+)p");
	        Matcher matchQuality_label = patQuality_label.matcher(urlFull);
	        if (matchQuality_label.find()) quality_label = matchQuality_label.group(1)+"p";
	        if(quality_label == null) quality_label = "";
	        
	        SingleVideoINFO videoinfo = new SingleVideoINFO(downloadLink,itag,type,dur,len,size,quality_label);
	        listSingleVideo.put(itag, videoinfo);
        }
        System.out.println("FINISHED GET INFO FROM SINGLE VIDEO");
        return listSingleVideo;
        
    }

    //NOW WE HAVE ALL THE STREAM LINKS RAW = not decode URL and Decryt signature
    public ArrayList<String> listStreamRaw(String html){
        ArrayList<String> listStream1Raw = get_url_encoded_fmt_stream_map(html);  
        ArrayList<String> listStream2Raw = get_adaptive_fmts(html);
        ArrayList<String> retStreamRaw = new ArrayList<String>();

        for(String s: listStream1Raw){
            Pattern encod = Pattern.compile("url=(.*)");
            Matcher encodMatch = encod.matcher(s);
            if (encodMatch.find()) {
                s = encodMatch.group(1);
            }
            String[] sarr = s.split("url=");
            for(String s1 : sarr){
                retStreamRaw.add(s1);
            }
        }
        for(String s: listStream2Raw){
            Pattern encod = Pattern.compile("url=(.*)");
            Matcher encodMatch = encod.matcher(s);
            if (encodMatch.find()) {
                s = encodMatch.group(1);
            }
            String[] sarr = s.split("url=");
            for(String s1 : sarr){
                retStreamRaw.add(s1);
            }
        }
        return retStreamRaw;
        
    }

    public ArrayList<String> get_adaptive_fmts(String htmlContent){
    	System.out.println("DOWNLOAD MINIFEST...");
        Pattern url_adaptive_fmts = Pattern.compile("\"adaptive_fmts\":\"([^\"]*)\"");
        Matcher url_adaptive_fmts_Match = url_adaptive_fmts.matcher(htmlContent);

        ArrayList<String> urlMedia = new ArrayList<String>();

        if (url_adaptive_fmts_Match.find()) {
            String adaptive_fmts = url_adaptive_fmts_Match.group(1);
            
            //System.out.println(adaptive_fmts);
            
            get_video(urlMedia,adaptive_fmts);
        }

        return urlMedia;
    }

    public ArrayList<String> get_url_encoded_fmt_stream_map(String htmlContent){
    	System.out.println("DOWNLOAD MINIFEST...");
        Pattern url_efsm = Pattern.compile("\"url_encoded_fmt_stream_map\":\"([^\"]*)\"");
        Matcher url_efsm_match = url_efsm.matcher(htmlContent);

        ArrayList<String> urlMedia = new ArrayList<String>();

        if (url_efsm_match.find()) {
            String url_e_f_s_m = url_efsm_match.group(1);
            
            //System.out.println(url_e_f_s_m);
            
            get_video(urlMedia,url_e_f_s_m);
        }
        return urlMedia;
    }

    private void get_video(ArrayList<String> urlMedia, String urldMatch){
        Pattern urlNormal = Pattern.compile("url=(.*)");//normal video
        Matcher urlNormalMatch = urlNormal.matcher(urldMatch);
        if (urlNormalMatch.find()) {
            String line = urlNormalMatch.group(1);
            urlMedia.add(line);
        }
    }

    public String getContentHtmlOfLink(String linkYoutube){
        try {
            URL url = new URL(linkYoutube);
            HttpURLConnection connect = (HttpURLConnection)url.openConnection();
            connect.setRequestProperty("User-Agent", "DASH-Group1-INF5090");
            connect.setConnectTimeout(200000);
            connect.setReadTimeout(9999999);
               
            String encode = connect.getContentEncoding();
            //force UTF8 encoding if not found encoding
            if (encode == null) encode = "UTF-8";

            InputStream is = connect.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, encode));
            String htmlContent = "";

            String line = br.readLine();

            while(line != null){
                htmlContent += line;
                line = br.readLine();
            }
            connect.disconnect();
            return htmlContent;
            
        } catch (Exception e) {
            System.out.println("EXCEPTION FOUND: "+e);
        }
        return null;
    }

    public String unescape(String originalString){
        String s = "";
        for(int i = 0; i< originalString.length();){
            if(originalString.charAt(i)=='\\'){
                i+=6;
                s+="&";
            }else {
                s+=originalString.charAt(i); 
                i++;
            }
        }
        return s;
    }
}
