package com.ojs.capabilities.locateCapability;

import java.util.Timer;
import java.util.TimerTask;

import com.ojs.R;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MapLocateCapabilityActivity extends Activity implements LocationListener {

	protected static final String TAG = MapLocateCapabilityActivity.class.getSimpleName();
	
	private TextView tMyPosition1;
	private TextView tMyPosition2;
	private TextView tMessage;
	private TextView tDistance;
	private TextView tBluetooth;
	private RelativeLayout tlayout;
	private Button closeButton;
	private ImageView tImage;
	
	private LocationListener loclist;
	
	private LocationManager locationManager;
	private Location location;
	private String provider;
	
	private final Handler handler = new Handler();
	private Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		loclist = this;
		
        setContentView(R.layout.activity_map_locate);

		p("MapLocateCapabilityActivity created!");
		
		tMyPosition1 = (TextView) findViewById(R.id.mapLocateMyPosition1);
		tMyPosition2 = (TextView) findViewById(R.id.mapLocateMyPosition2);
		tMessage = (TextView) findViewById(R.id.mapLocateMessage);
		tDistance = (TextView) findViewById(R.id.mapLocateDistance);
		tBluetooth = (TextView) findViewById(R.id.mapLocateBluetooth);
		tImage = (ImageView) findViewById(R.id.mapLocateImage);
		tlayout = (RelativeLayout) findViewById(R.id.mapLocateLayout);
		closeButton = (Button) findViewById(R.id.mapLocateClose);
		
		Intent args = getIntent();

		String message = args.getStringExtra("locateName");
		String image = args.getStringExtra("locateImage");

		if (image == null || image.isEmpty())
			tImage.setVisibility(View.GONE);
		else
		{
			try
			{
				byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
				tImage.setImageBitmap(decodedByte);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		tMessage.setText("Locating " + message + "...");
		
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)
			{
				timer.cancel();
				locationManager.removeUpdates(loclist);
				finish();
			}
        });
		
		// Get the location manager
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
	    // Define the criteria how to select the locatioin provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    location = locationManager.getLastKnownLocation(provider);
	    
	    // Initialize the location fields
	    if (location != null)
	    {
	    	onLocationChanged(location);
	    }
	    
	    locationManager.requestLocationUpdates(provider, 5000, 0, loclist);
	    
	    timer.schedule(updateGUI, 0, 1000);
	}

	private void p(String s)
	{
		Log.d(TAG, s);
	}

	@Override
	public void onLocationChanged(Location location)
	{
		this.location = location;
		double lat = (double) (location.getLatitude());
		double lng = (double) (location.getLongitude());
		
		tMyPosition1.setText("Lat: " + lat);
		tMyPosition2.setText("Lng: " + lng);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE)
		{
			Toast.makeText(getApplicationContext(), "GPS Signal lost " + provider, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}
	 
	private TimerTask updateGUI = new TimerTask()
	{       
		@Override
	    public void run()
	    {
	        handler.post(new Runnable()
	        {
	            public void run()
	            {
	            	if (!LocateCapability.getConnection())
	            	{
	            		tMessage.setText("Connection lost");
	            	}
	            	
	            	if (LocateCapability.getTarget() == null)
	        			return;
	        		
	        		Integer rssi = LocateCapability.getTargetRSSI();
	        		System.out.println("rssi " + rssi);
	        		if (rssi != null)
	        		{
	        			if (rssi > 190)
	        			{
	        				tBluetooth.setText("The target is inside your Bluetooth range.");
	        				tDistance.setText("You are very close!");
	        				tlayout.setBackgroundColor(Color.parseColor("#00CC00"));
	        			}
	        			else if (rssi > 135)
	        			{
	        				tBluetooth.setText("The target is inside your Bluetooth range.");
	        				tDistance.setText("You are close your target!");
	        				tlayout.setBackgroundColor(Color.parseColor("#B2FF66"));
	        			}
	        			else if (rssi > 60)
	        			{
	        				tBluetooth.setText("The target is inside your Bluetooth range.");
	        				tDistance.setText("You are getting closer...");
	        				tlayout.setBackgroundColor(Color.parseColor("#FFFF66"));
	        			}
	        			else
	        			{
	        				tBluetooth.setText("The target is inside your Bluetooth range.");
	        				tlayout.setBackgroundColor(Color.parseColor("#FFB266"));
	        			}
	        		}
	        		else
	        		{
	        			tBluetooth.setText("No bluetooth proximity");
	        			Location targetLoc = LocateCapability.getTargetLocation();
	        			if (targetLoc != null)
	        			{
	        				String unit = "meters";
	        				String orientation = "";
		        			Float distance = location.distanceTo(targetLoc);
		        			Float distance_r = distance;
		        			if (distance > 1000f)
		        			{
		        				unit = "kilometers";
		        				distance_r = distance / 1000f;
		        			}
		        			
		        			// Bigger difference
		        			Double latdiff = Math.abs(targetLoc.getLatitude() - location.getLatitude());
		        			Double lngdiff = Math.abs(targetLoc.getLongitude() - location.getLongitude());
		        			
		        			// Latitude difference is bigger (North-South)
		        			if (latdiff > lngdiff)
		        			{
		        				// The target is to North
		        				if (targetLoc.getLatitude() > location.getLatitude())
		        					orientation = "North";
		        				else
		        					orientation = "South";
		        			}
		        			// Longitude difference is bigger (East-West)
		        			else
		        			{
		        				// The target is to East
		        				if (targetLoc.getLongitude() > location.getLongitude())
		        					orientation = "East";
		        				else
		        					orientation = "West";
		        			}
		        			
		        			tDistance.setText("Your target is " + distance_r.intValue() + " " + unit + " to " + orientation);
		    	    		if (distance < 200f)
		    	    		{
		    	    			tlayout.setBackgroundColor(Color.parseColor("#99FFFF"));
		    	    		}
		    	    		else
		    	    		{
		    	    			tlayout.setBackgroundColor(Color.WHITE);
		    	    		}
	        			}
	        			else
	        			{
	        				tDistance.setText("Unknown distance... please wait");
	        				tlayout.setBackgroundColor(Color.WHITE);
	        			}
	        		}
	            }
	        });
	    }
	};
}
