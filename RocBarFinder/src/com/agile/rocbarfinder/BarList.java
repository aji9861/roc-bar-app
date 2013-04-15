package com.agile.rocbarfinder;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class BarList extends ListActivity {
	
	public ArrayList<String> BARS = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		populateList();
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.bar_list,(String[]) BARS.toArray()));
	}
	
	private void populateList(){
		List<BarInformation> barList =  BarInfoStorage.getInstance().getAllBars();
		
		for(BarInformation bar : barList){
			BARS.add(bar.name);
		}
	}
}
