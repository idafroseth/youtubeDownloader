package no.uio.ifi.models.downloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.uio.ifi.models.SingleVideoINFO;

 public class YoutubeDownloader{

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
            connect.setConnectTimeout(20000);
            connect.setReadTimeout(20000);
               
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