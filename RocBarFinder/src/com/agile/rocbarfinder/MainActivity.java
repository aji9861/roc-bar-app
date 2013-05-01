package com.agile.rocbarfinder;

import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.content.Context;
import android.view.MenuItem;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;


public class MainActivity extends Activity {
 
private GoogleMap mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        setupMapView();
        new BarInfoFetcher(this).execute();
        checkGPSEnabled();

        placeMarkers();
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch(item.getItemId()){
			case R.id.ViewList:
				intent = new Intent(MainActivity.this, BarList.class);
				startActivity(intent);
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

    private void checkGPSEnabled(){
    	String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    	if(!provider.equals("")){
    		// Location is enabled
    		Toast.makeText(MainActivity.this, "Location Enabled: " + provider,
    		          Toast.LENGTH_LONG).show();
    	}
    	else{
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS module is disabled. Would you like to enable it ?")
                   .setCancelable(false)
                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);     						
     						startActivity(intent);
                            dialog.dismiss();
                       }
                   })
                   .setNegativeButton("No", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                       }
                   });
            AlertDialog alert = builder.create();
            alert.show();
    	}    	
    }
   
    private void setupMapView() {
    	
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
        
    	if(myLocation!=null){
    		LatLng currentCoordinates = new LatLng(
    				myLocation.getLatitude(),
    				myLocation.getLongitude());
    	       		mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinates, 18));
    	}  
    	mapView.setMyLocationEnabled(true);
    	mapView.setOnInfoWindowClickListener(
      		  new OnInfoWindowClickListener(){
      		    public void onInfoWindowClick(Marker marker){
      		        BarInformation bar = BarInfoStorage.getInstance().getBarByName(marker.getTitle());
      		         LatLng barLocation = new LatLng(bar.latitude, bar.longitude); 
      		         
      		         launchNavigation(barLocation);
      		     }
      		   });
    	
    }
    
    

    
    
    public void placeMarkers()
    {
    	List<BarInformation> barList = BarInfoStorage.getInstance().getAllBars();
    	
    	if (mapView != null){
    		
    		for(BarInformation bar : barList)
    		{
    			LatLng position = new LatLng(bar.latitude, bar.longitude);
    			mapView.addMarker(new MarkerOptions().position(position)
    	          .title(bar.name));
    		}
	    }
    }
    
    public void launchNavigation(LatLng barLocation){
    	
    	String uri = "google.navigation:ll=%f,%f";
    	double barLocationLat = barLocation.latitude;
    	double barLocationLong = barLocation.longitude;
    	
    	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
    		    Uri.parse(String.format(uri, barLocationLat, barLocationLong)));
    		startActivity(intent);
    }
}

