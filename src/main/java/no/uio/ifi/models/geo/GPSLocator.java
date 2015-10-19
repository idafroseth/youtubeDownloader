package no.uio.ifi.models.geo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GPSLocator {
		//https://maps.googleapis.com/maps/api/geocode/json?address=Toledo&key=
		
	public static String getGeolocationCity(String city){
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
			return null;
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
			return null;
		}
		return s;	
	}

}
