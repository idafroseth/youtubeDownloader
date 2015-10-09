/*
 * Copyright (c) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * This sample lists videos that are associated with a particular keyword and are in the radius of
 *   particular geographic coordinates by:
 *
 * 1. Searching videos with "youtube.search.list" method and setting "type", "q", "location" and
 *   "locationRadius" parameters.
 * 2. Retrieving location details for each video with "youtube.videos.list" method and setting
 *   "id" parameter to comma separated list of video IDs in search result.
 *
 * @author Ibrahim Ulukaya
 */

//WE MODIFIED THE SAMPLE CODE FROM GOOGLE API EXAMPLE TO FIXED OUR ASSIGMENT

package no.uio.ifi.models;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.Joiner;
import no.uio.ifi.Auth;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class GeolocationSearch {
	
	class FindGPSOnName{
		//https://maps.googleapis.com/maps/api/geocode/json?address=Toledo&key=
		
		public String getGeolocationCity(String city){
			String s = "";
			
			try{
				
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address="+city);
				
				URLConnection conn = url.openConnection();
				InputStreamReader istr = new InputStreamReader(conn.getInputStream());
				BufferedReader br = new BufferedReader(istr);
				String line = br.readLine();
	            while (line != null) {
	                    s+=line;
	                    line = br.readLine();
	            }
	            br.close();
				
			}catch (Exception e){
				
			}
			
			JSONParser parser = new JSONParser();
			try{
				JSONObject objJson = (JSONObject)parser.parse(s);
				JSONArray results = (JSONArray)objJson.get("results");
				//System.out.println(results.size()+" "+ results.toString());
				JSONObject geometry = (JSONObject)((JSONObject)results.get(0)).get("geometry");
				JSONObject location = (JSONObject)geometry.get("location");
				//System.out.println(location.get("lat")+":"+location.get("lng"));
				s = ""+ location.get("lat")+","+location.get("lng");
			}catch(Exception e){
				System.out.println("FEIL MED FINN LOCATION"+e);
			}
			return s;	
		}
	}

    /**
     * Define a global variable that identifies the name of a file that
     * contains the developer's API key.
     */
    private static final String PROPERTIES_FILENAME = "youtube.properties";

    public static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     * @param args command line args.
     */
    
    public List<Video> searchVideoBaseOnLocation(String keyword, String city, String distance) {
        // Read the developer key from the properties file.
    	List<Video> videoList = null;
        Properties properties = new Properties();
        try {
            InputStream in = GeolocationSearch.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-geolocationsearch-sample").build();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://console.developers.google.com/
            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setQ(keyword);
            
            FindGPSOnName locationGPS = new FindGPSOnName();
            String location = locationGPS.getGeolocationCity(city);
            
            search.setLocation(location);
            search.setLocationRadius(distance);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // As a best practice, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/videoId)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            
            // Call the API
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            List<String> videoIds = new ArrayList<String>();

            if (searchResultList != null) {

                // Merge video IDs
                for (SearchResult searchResult : searchResultList) {
                    videoIds.add(searchResult.getId().getVideoId());
                }
                Joiner stringJoiner = Joiner.on(',');
                String videoId = stringJoiner.join(videoIds);

                // Call the YouTube Data API's youtube.videos.list method to
                // retrieve the resources that represent the specified videos.
                YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet, recordingDetails").setId(videoId);
                
                // Set your developer key
                listVideosRequest.setKey(apiKey);
                
                VideoListResponse listResponse = listVideosRequest.execute();
                videoList = listResponse.getItems();
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        return videoList;
    }
}








