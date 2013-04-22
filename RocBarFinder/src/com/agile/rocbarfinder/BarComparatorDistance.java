package com.agile.rocbarfinder;

import java.util.Comparator;

public class BarComparatorDistance implements Comparator<BarInformation> {

	private double lat;
	private double lon;
	
	public BarComparatorDistance(double lat, double lon){
		this.lat = lat;
		this.lon = lon;
	}
	
	@Override
	public int compare(BarInformation lhs, BarInformation rhs) {
		return (-1)*(rhs.getDistance(this.lat, this.lon).compareTo(lhs.getDistance(this.lat, this.lon)));
	}
}
