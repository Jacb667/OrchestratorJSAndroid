package com.ojs.capabilities.gpsCapability;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ojs.OrchestratorJsActivity;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GpsCapability implements LocationListener {
	
	private static Context applicationContext_;
	
	private LocationManager locationManager;
	private String provider;
	
	public void initCapability( Context applicationContext )
	{
		System.out.println("INITIALIZING GPS");
		
		GpsCapability.applicationContext_ = applicationContext;
		
		// Get the location manager
	    locationManager = (LocationManager) applicationContext_.getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the locatioin provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    /*Location location = locationManager.getLastKnownLocation(provider);
	    
	    // Initialize the location fields
	    if (location != null)
	    {
	    	onLocationChanged(location);
	    }*/
	}
	
	public void getGpsPosition()
	{
		//locationManager.requestLocationUpdates(provider, 1000, 0, this);
		locationManager.requestSingleUpdate(provider, this, null);
	}

	@Override
	public void onLocationChanged(Location location)
	{
		double lat = (double) (location.getLatitude());
		double lng = (double) (location.getLongitude());
		System.out.println("LAT = " + lat);
		System.out.println("LNG = " + lng);
		
		try
    	{
    		// Create a JSON Object to send the data
    		JSONObject sendData = new JSONObject();
    		
    		// Create a JSON Array to store the coordinates
    		JSONArray coords = new JSONArray();
    		
    		coords.put(lat);
    		coords.put(lng);
    		
    		sendData.put("gps_location", coords);
			OrchestratorJsActivity.ojsContextData(sendData);
    	}
    	catch(Exception ex)
    	{
    		
    	}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE)
		{
			Toast.makeText(applicationContext_, "GPS Signal lost " + provider, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		//Toast.makeText(applicationContext_, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		//Toast.makeText(applicationContext_, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
	}
	
	public void onDisconnect()
    {
    	Log.d(OrchestratorJsActivity.TAG, "GPS Capability: Connection lost");
    	
    	locationManager.removeUpdates(this);
    }
}
