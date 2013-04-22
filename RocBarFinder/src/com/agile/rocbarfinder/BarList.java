package com.agile.rocbarfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BarList extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ArrayList<String> bars = getList();
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.bar_list,(Arrays.copyOf(bars.toArray(), bars.size(), String[].class))));
	}
	
	private ArrayList<String> getList() {
		List<BarInformation> barList =  BarInfoStorage.getInstance().getAllBars();
		ArrayList<String> bars = new ArrayList<String>();
		
		for(BarInformation bar : barList){
			bars.add(bar.name);
		}
		
		return bars;
	}
	
	private ArrayList<String> getAlphabeticalList() {
		List<BarInformation> barList =  BarInfoStorage.getInstance().getAllBars(BarSortingOption.Name, 0, 0);
		ArrayList<String> bars = new ArrayList<String>();
		
		for(BarInformation bar : barList){
			bars.add(bar.name);
		}
		
		return bars;
	}
	
	private ArrayList<String> getDistanceList() {
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Location wasn't found, check the next most accurate place for the current location
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = lm.getBestProvider(criteria, true);
            // Use the provider to get the last known location
            myLocation = lm.getLastKnownLocation(provider);
        }
        
		List<BarInformation> barList =  BarInfoStorage.getInstance().getAllBars(BarSortingOption.DistanceToBar, myLocation.getLatitude(), myLocation.getLongitude());
		ArrayList<String> bars = new ArrayList<String>();
		
		for(BarInformation bar : barList){
			bars.add(bar.name);
		}
		
		return bars;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ArrayList<String> bars;
		switch(item.getItemId()){
		case R.id.SortAlphabetical:
			bars = getAlphabeticalList();
			
			setListAdapter(new ArrayAdapter<String>(this, R.layout.bar_list,(Arrays.copyOf(bars.toArray(), bars.size(), String[].class))));
			break;
		case R.id.SortDistance:
			bars = getDistanceList();
			
			setListAdapter(new ArrayAdapter<String>(this, R.layout.bar_list,(Arrays.copyOf(bars.toArray(), bars.size(), String[].class))));
			break;
		case android.R.id.home:
			Intent intent = new Intent(BarList.this,MainActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
				return super.onOptionsItemSelected(item);
		}
		
		return true;
	}
	
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(BarList.this,MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	 @Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
	    super.onListItemClick(l, v, position, id);
	    // Get the item that was clicked
	    Object o = this.getListAdapter().getItem(position);
	    String keyword = o.toString();
	    Toast.makeText(this, "You selected: " + keyword, Toast.LENGTH_SHORT)
	        .show();
	  }
}

