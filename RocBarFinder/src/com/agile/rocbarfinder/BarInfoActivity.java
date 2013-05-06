package com.agile.rocbarfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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

public class BarInfoActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		String bar_name = intent.getStringExtra("bar_name");
		ArrayList<String> bar_info = getList(bar_name);
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.bar_info,(Arrays.copyOf(bar_info.toArray(), bar_info.size(), String[].class))));
	}
	
	private ArrayList<String> getList(String bar_name) {
		BarInformation barInfo = BarInfoStorage.getInstance().getBarByName(bar_name);
		
		ArrayList<String> info_list = new ArrayList<String>();
		
		info_list.add("Name: " + barInfo.name);
		info_list.add("Address: " + barInfo.address);
		info_list.add("Phone Number: " + barInfo.phone);

		return info_list;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			Intent intent = new Intent(BarInfoActivity.this,BarList.class);
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
		Intent intent = new Intent(BarInfoActivity.this,BarList.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	    super.onListItemClick(l, v, position, id);
	    // Get the item that was clicked
	    Object o = this.getListAdapter().getItem(position);
	    String keyword = o.toString();
	    
	    if(keyword.startsWith("Address")){
	    	Intent intent = getIntent();
	    	BarInformation bar = BarInfoStorage.getInstance().getBarByName(intent.getStringExtra("bar_name"));
	    	launchNavigation(new LatLng(bar.latitude, bar.longitude));
	    }
	    
	    if(keyword.startsWith("Phone")){
		    Intent intent = new Intent(Intent.ACTION_DIAL);
		    String uri = "tel:" + keyword.split("Phone Number: ")[1];
		    intent.setData(Uri.parse(uri));
		    startActivity(intent);
	    }
	}
	
	private void launchNavigation(LatLng barLocation){
    	
    	String uri = "google.navigation:ll=%f,%f";
    	double barLocationLat = barLocation.latitude;
    	double barLocationLong = barLocation.longitude;
    	
    	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
    		    Uri.parse(String.format(uri, barLocationLat, barLocationLong)));
    		startActivity(intent);
    }
}


