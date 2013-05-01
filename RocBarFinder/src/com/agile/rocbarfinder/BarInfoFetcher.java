package com.agile.rocbarfinder;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.*;

import android.content.Context;
import android.os.AsyncTask;

public class BarInfoFetcher extends AsyncTask<String, Void, Boolean>{
	private final String apiKey = "AIzaSyD3ZznuBfcgAwbz5eqWtngbby871Epkq3U";
	private final String searchLatitude = "43.1547";		// For Rochester NY
	private final String searchLongitude = "-77.6158"; 	// For Rochester NY
	private final String searchRadius = "15000";			// In meters
	private final String searchType = "bar";
	
	private final String placesApiUrl = "https://maps.googleapis.com/maps/api/place/search/json?";
	private final String placesApiDetails = "https://maps.googleapis.com/maps/api/place/details/json?";
	private final String placesLocationParam = "&location=" + searchLatitude + "," + searchLongitude;
	private final String placesRadiusParam = "&radius=" + searchRadius;
	private final String placesNamesParam = "&types=" + searchType;
	private final String placesSensorParam = "&sensor=true";
	private final String placesKeyParam = "&key=" + apiKey;
	
	private final BarInfoStorage infoStorage;
	
	public BarInfoFetcher(Context c){
		super();
		infoStorage = BarInfoStorage.getInstance(c);
	}
	
	private List<BarInformation> getData(){
		List<BarInformation> lbi = new ArrayList<BarInformation>();
		try {
			//System.out.println("URL: " + placesApiUrl + placesLocationParam + placesRadiusParam + placesNamesParam + placesSensorParam + placesKeyParam);
		    HttpPost httppost = new HttpPost(placesApiUrl + placesLocationParam + placesRadiusParam + placesNamesParam + placesSensorParam + placesKeyParam);
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response = httpclient.execute(httppost);
		    String data = EntityUtils.toString(response.getEntity());

		    JSONArray jsonA = new JSONObject(data).getJSONArray("results");
		    
		    for (int i = 0; i < jsonA.length(); i++){
		    	JSONObject jsonO = jsonA.getJSONObject(i);
		    	String[] addressPhone = getDetails(jsonO.getString("reference"));
		    	lbi.add(new BarInformation(jsonO, addressPhone[0], addressPhone[1]));
		    	System.out.println(lbi.get(i).name);
		    }
		    
		    
	
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return lbi;
	}
	
	/**
	 * 
	 * @param reference
	 * @return String[0] = address String[1] = phone
	 */
	private String[] getDetails(String reference){
		String[] addressPhone = new String[2];
		try{
    		HttpPost httppost = new HttpPost(placesApiDetails + "reference=" + reference + placesSensorParam + placesKeyParam);
    		HttpClient httpclient = new DefaultHttpClient();
    	    HttpResponse response = httpclient.execute(httppost);
    	    String data = EntityUtils.toString(response.getEntity());
    
    	    JSONObject jsonO = new JSONObject(data).getJSONObject("result");
    	    
    	    addressPhone[0] = jsonO.getString("formatted_address");
    	    addressPhone[1] = jsonO.getString("formatted_phone_number");
    	    
    	    System.out.println(addressPhone[0] +" " + addressPhone[1]);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return addressPhone;
	}


	@Override
	protected Boolean doInBackground(String... params) {
		List<BarInformation> data = getData();
		infoStorage.addAllBars(data);

		return true;
	}
}
