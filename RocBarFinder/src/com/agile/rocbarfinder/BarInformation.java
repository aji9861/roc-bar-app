package com.agile.rocbarfinder;

import org.json.JSONException;
import org.json.JSONObject;

public class BarInformation {
	
	public final String name;
	public final String vicinity;
	public final String image;
	public final String id;
	public final Double longitude;
	public final Double latitude;
	
	public BarInformation(JSONObject data) throws JSONException{
		name = data.getString("name");
		vicinity = data.getString("vicinity");
		image = data.getString("icon");
		id = data.getString("id");

		JSONObject geometry = data.getJSONObject("geometry");
		JSONObject location = geometry.getJSONObject("location");
		longitude = location.getDouble("lng");
		latitude = location.getDouble("lat");
	}
	
	public BarInformation(String name, String vicinity, String image, String id, Double latitude, Double longitude){
		this.name = name;
		this.vicinity = vicinity;
		this.image = image;
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * Taken from http://www.movable-type.co.uk/scripts/latlong.html
	 * @param lat - latitude of a source location
	 * @param lng - longitude of a source location
	 * @return distance in km from specified location to this point
	 */
	public Double getDistance(Double lat, Double lng){
		long R = 6371; // km - mean radius of the earth
		double dLat = Math.toRadians(latitude-lat);
		double dLon = Math.toRadians(longitude-lng);
		double lat1 = Math.toRadians(lat);
		double lat2 = Math.toRadians(latitude);

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		        Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		return R * c;
	}
	
	public String toString(){
		return "Name: " + name + " Image: " + image;
	}
}
