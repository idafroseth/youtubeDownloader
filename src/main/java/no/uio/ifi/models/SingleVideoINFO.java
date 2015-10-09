package no.uio.ifi.models;

public class SingleVideoINFO{
	public String downloadLink;
	public String itag;
	public String type;
	public String dur;
	public String len;
	public String size;
	public String quality_label;
	
	public SingleVideoINFO(String downloadLink ,String itag, String type, String dur, String len, String size, String quality_label){
		this.downloadLink = downloadLink;
		this.itag = itag;
		this.type = type;
		this.dur = dur;
		this.len = len;
		this.size = size;
		this.quality_label = quality_label;
	}
	
	public String toString(){
		return String.format("%3d:   Type: %s.",Integer.parseInt(itag),type) + " " +((dur.equals("0")) ? "" : dur) +
				" "+((len=="") ? "" : len) + " " + ((quality_label=="") ? "" : quality_label);
	}
}