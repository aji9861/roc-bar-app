package com.agile.rocbarfinder;

import java.util.Comparator;

public class BarComparatorName implements Comparator<BarInformation> {	
	@Override
	public int compare(BarInformation lhs, BarInformation rhs) {
		return lhs.name.compareTo(rhs.name);		
	}
}
