package no.uio.ifi.guis;

import java.util.List;
//import javax.swing.JPanel;
import com.google.api.client.json.Json;

//The ResultWindow contains of two textfields or similar that display the //retrieved videoId and it is possible to click one videoId and hit "Show video //info"
interface ResultWindow {//extends JPanel{ 
	
	//should display a scrollable list of all the available videoID and it should be
	//possible to click one of the IDs and display the JSON for that video. When //a button called "Show video info" is pressed. 
	void displayVideoFromSearch(List<String> videoID);
	
	//Should display the provided Json(does not have to be of the type Json //can also be text
	void displayVideoInfo(Json videoInfoOfvideoID);
}
