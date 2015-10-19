package no.uio.ifi.guis;

import java.util.List;

import javax.swing.JButton;

//import javax.swing.JPanel;
import com.google.api.client.json.Json;

/**
 * The ResultWindow should extend JPanel and display two textarea or similar
 * that show the retrieved videoId in and the json of a video in the other. It
 * should also be possible to click one of the ID in the videoId list and hit a
 * button "show Video info" And then the corresponding json is shown in the
 * other text area
 * 
 * @author Ida Marie Frøseth
 *
 */
interface ResultWindow {// extends JPanel{
	/**
	 * The first three button should show a JFilechooser or similar when hit so
	 * the user can decide where to store the data.
	 */
	JButton exportVideo = new JButton("Export videos");
	JButton exportJson = new JButton("Export Videos Info");
	JButton exportAll = new JButton("Export vidoes and videoInfo");
	
	/**
	 * This should display the json of the selected video id
	 */
	JButton showJson = new JButton("Show video info");

	/**
	 * should display a scrollable list of all the available videoID and it
	 * should be possible to click one of the IDs and display the JSON for that
	 * video. When a button called "Show video info" is pressed.
	 * 
	 * @param videoID
	 */
	void displayVideoFromSearch(List<String> videoID);

	/**
	 * Should display the provided Json(does not have to be of the type Json can
	 * also be text (Just feel free to change the interface on this point)
	 * 
	 * @param videoInfoOfvideoID
	 */
	void displayVideoInfo(Json videoInfoOfvideoID);
}
