package no.uio.ifi.models;

public class SingleVideoINFO{
	public String downloadLink;
	public String itag;
	public String type;
	public String dur;
	public String len;
	public String size;
	public String quality_label;
	public String infoVideo;
	
	public SingleVideoINFO(String downloadLink ,String itag, String type, String dur, String len, String size, String quality_label){
		this.downloadLink = downloadLink;
		this.itag = itag;
		this.type = type;
		this.dur = dur;
		this.len = len;
		this.size = size;
		this.quality_label = quality_label;
		infoVideo = getVideoInfo(itag);
	}
	
	public String getVideoInfo(String itaginfo){
		int itaginfonr = Integer.parseInt(itaginfo);
		switch(itaginfonr){
			//NoDASH
			case 5:
				type = ".flv";
				return "  5.  FLV. Resolution 240p. Video Enc  H.263 - bitrate 0.25Mbit/s. Audio Enc MP3 - bitrate 64kbit/s";
			case 6:
				type = ".flv";
				return "  6.  FLV. Resolution 270p. Video Enc  H.263 - bitrate 0.80Mbit/s. Audio Enc MP3 - bitrate 64kbit/s";
			case 13:
				type = ".3gp";
				return " 13.  3GP. Video Enc MPEG-4 - bitrate 0.5Mbit/s. Audio Enc AAC";
			case 17:
				type = ".3gp";
				return " 17.  3GP. Resolution 144p. Video Enc MPEG-4 - bitrate 0.05Mbit/s. Audio Enc AAC - bitrate 24kbit/s";
			case 18:
				type = ".mp4";
				return " 18.  MP4. Resolution 360p. Video Enc  H.264 - bitrate 0.5Mbit/s. Audio Enc AAC - bitrate 96kbit/s";
			case 22:
				type = ".mp4";
				return " 22.  MP4. Resolution 720p. Video Enc  H.264 - bitrate 2-3Mbit/s. Audio Enc AAC - bitrate 192kbit/s";
			case 34:
				type = ".flv";
				return " 34.  FLV. Resolution 360p. Video Enc  H.264 - bitrate 0.5Mbit/s. Audio Enc AAC - bitrate 128kbit/s";
			case 35:
				type = ".flv";
				return " 35.  FLV. Resolution 480p. Video Enc  H.264 - bitrate 0.8-1Mbit/s. Audio Enc AAC - bitrate 128kbit/s";
			case 36:
				type = ".3gp";
				return " 36.  3GP. Resolution 240p. Video Enc  MPEG-4 - bitrate 0.175Mbit/s. Audio Enc AAC - bitrate 32kbit/s";
			case 37:
				type = ".mp4";
				return " 37.  MP4. Resolution 1080p. Video Enc H.264 - bitrate 3-5.9Mbit/s. Audio Enc AAC - bitrate 192kbit/s";
			case 38:
				type = ".mp4";
				return " 38.  MP4. Resolution 3072p. Video Enc H.264 - bitrate 3.5-5Mbit/s. Audio Enc AAC - bitrate 192kbit/s";
			case 43:
				type = ".webm";
				return " 43. WebM. Resolution 360p. Video Enc VP8 - bitrate 0.5Mbit/s. Audio Enc Vorbis - bitrate 128kbit/s";
			case 44:
				type = ".webm";
				return " 44. WebM. Resolution 480p. Video Enc VP8 - bitrate 1Mbit/s. Audio Enc Vorbis - bitrate 128kbit/s";
			case 45:
				type = ".webm";
				return " 45. WebM. Resolution 720p. Video Enc VP8 - bitrate 2Mbit/s. Audio Enc Vorbis - bitrate 192kbit/s";
			case 46:
				type = ".webm";
				return " 46. WebM. Resolution 1080p. Video Enc VP8. Audio Enc Vorbis - bitrate 192kbit/s";
			case 82:
				type = ".mp4";
				return " 82.  MP4. Resolution 360p. Video Enc H.264 - 3D - bitrate 0.5Mbit/s. Audio Enc AAC - bitrate 96kbit/s";
			case 83:
				type = ".mp4";
				return " 83.  MP4. Resolution 240p. Video Enc H.264 - 3D - bitrate 0.5Mbit/s. Audio Enc AAC - bitrate 96kbit/s";
			case 84:
				type = ".mp4";
				return " 84.  MP4. Resolution 720p. Video Enc H.264 - 3D - bitrate 2-3Mbit/s. Audio Enc AAC - bitrate 192kbit/s";
			case 85:
				type = ".mp4";
				return " 85.  MP4. Resolution 1080p. Video Enc H.264 - 3D - bitrate 3-4Mbit/s. Audio Enc AAC - bitrate 192kbit/s";
			case 100:
				type = ".webm";
				return "100. WebM. Resolution 360p. Video Enc VP8 - 3D. Audio Enc Vorbis - bitrate 128kbit/s";
			case 101:
				type = ".webm";
				return "101. WebM. Resolution 360p. Video Enc VP8 - 3D. Audio Enc Vorbis - bitrate 192kbit/s";
			case 102:
				type = ".webm";
				return "102. WebM. Resolution 720p. Video Enc VP8 - 3D. Audio Enc Vorbis - bitrate 192kbit/s";
				
			//DASH-VIDEO ONLY
			case 133:
				type = ".mp4";
				return "133.  MP4. Resolution 240p. Video Enc H.264 - bitrate 0.2-0.3Mbit/s";
			case 134:
				type = ".mp4";
				return "134.  MP4. Resolution 360p. Video Enc H.264 - bitrate 0.3-0.4Mbit/s";
			case 135:
				type = ".mp4";
				return "135.  MP4. Resolution 480p. Video Enc H.264 - bitrate 0.5-1Mbit/s";
			case 136:
				type = ".mp4";
				return "136.  MP4. Resolution 720p. Video Enc H.264 - bitrate 1-1.5Mbit/s";
			case 137:
				type = ".mp4";
				return "137.  MP4. Resolution 1080p. Video Enc H.264 - bitrate 2.5-3Mbit/s";
			case 138:
				type = ".mp4";
				return "138.  MP4. Resolution 2160p-4320p. Video Enc H.264 - bitrate 13.5-25Mbit/s";
			case 160:
				type = ".mp4";
				return "160.  MP4. Resolution 144p. Video Enc H.264 - bitrate 0.1Mbit/s";
			case 242:
				type = ".webm";
				return "242. WebM. Resolution 240p. Video Enc VP9 - bitrate 0.1-0.2Mbit/s";
			case 243:
				type = ".webm";
				return "243. WebM. Resolution 360p. Video Enc VP9 - bitrate 0.25Mbit/s";
			case 244:
				type = ".webm";
				return "244. WebM. Resolution 480p. Video Enc VP9 - bitrate 0.5Mbit/s";
			case 247:
				type = ".webm";
				return "247. WebM. Resolution 720p. Video Enc VP9 - bitrate 1.5Mbit/s";
			case 248:
				type = ".webm";
				return "248. WebM. Resolution 1080p. Video Enc VP9 - bitrate 1.5Mbit/s";
			case 264:
				type = ".webm";
				return "264. WebM. Resolution 1440p. Video Enc H.264 - bitrate 4-4.5Mbit/s";
			case 266:
				type = ".webm";
				return "266. WebM. Resolution 2160p-2304p. Video Enc H.264 - bitrate 12.5-16Mbit/s";
			case 271:
				type = ".webm";
				return "271. WebM. Resolution 1440p. Video Enc VP9 - bitrate 9Mbit/s";
			case 272:
				type = ".webm";
				return "272. WebM. Resolution 2160p. Video Enc VP9 - bitrate 15-17.5Mbit/s";
			case 278:
				type = ".webm";
				return "278. WebM. Resolution 144p. Video Enc VP9 - bitrate 0.08Mbit/s";
			case 298:
				type = ".mp4";
				return "298.  MP4. Resolution 360p/720p. Video Enc H.264 - bitrate 3-3.5Mbit/s";
			case 299:
				type = ".mp4";
				return "299.  MP4. Resolution 480p/1080p. Video Enc H.264 - bitrate 5.5Mbit/s";
			case 302:
				type = ".webm";
				return "302. WebM. Resolution 360p/720p. Video Enc VP9 - bitrate 2.5Mbit/s";
			case 303:
				type = ".webm";
				return "303. WebM. Resolution 480p/1080p. Video Enc VP9 - bitrate 5Mbit/s";
			case 308:
				type = ".webm";
				return "308. WebM. Resolution 1440p. Video Enc VP9 - bitrate 10Mbit/s";
			case 313:
				type = ".webm";
				return "313. WebM. Resolution 2160p. Video Enc VP9 - bitrate 13-15Mbit/s";
			case 315:
				type = ".webm";
				return "315. WebM. Resolution 2160p. Video Enc VP9 - bitrate 20-25Mbit/s";
			
			//AUDIO DASH
			case 139:
				type = ".m4a";
				return "139.  M4A. Audio Enc AAC - bitrate 48kbit/s";
			case 140:
				type = ".m4a";
				return "140.  M4A. Audio Enc AAC - bitrate 128kbit/s";
			case 141:
				type = ".m4a";
				return "141.  M4A. Audio Enc AAC - bitrate 256kbit/s";
			case 171:
				type = ".webm";
				return "171. WebM. Audio Enc Vorbis - bitrate 128kbit/s";
			case 172:
				type = ".webm";
				return "172. WebM. Audio Enc Vorbis - bitrate 192kbit/s";
			case 249:
				type = ".webm";
				return "249. WebM. Audio Enc Opus - bitrate 48kbit/s";
			case 250:
				type = ".webm";
				return "250. WebM. Audio Enc Opus - bitrate 64kbit/s";
			case 251:
				type = ".webm";
				return "251. WebM. Audio Enc Opus - bitrate 160kbit/s";
			default:
				System.out.println("itag not found!!!");
				return "UNKNOWN!!!!";

		}
	}
	
	public String toString(){
		return infoVideo;
	}
}