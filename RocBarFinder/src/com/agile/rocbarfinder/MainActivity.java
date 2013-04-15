package com.agile.rocbarfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;


public class MainActivity extends Activity {
 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        new BarInfoFetcher(this).execute();
        checkGPSEnabled();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(findViewById(R.id.ViewList))){
			Intent intent = new Intent(MainActivity.this, BarList.class);
			startActivity(intent);
		}
		return true;
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
}

