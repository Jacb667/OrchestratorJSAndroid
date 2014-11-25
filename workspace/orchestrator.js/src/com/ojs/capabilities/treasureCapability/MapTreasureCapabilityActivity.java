package com.ojs.capabilities.treasureCapability;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ojs.OrchestratorJsActivity;
import com.ojs.R;

public class MapTreasureCapabilityActivity extends Activity implements LocationListener {

	protected static final String TAG = MapTreasureCapabilityActivity.class.getSimpleName();
	
	private TextView tPoints;
	private TextView tMyPosition1;
	private TextView tMyPosition2;
	private TextView tMessage;
	private TextView tDistance;
	private TextView tBluetooth;
	private RelativeLayout tlayout;
	private Button closeButton;
	
	private boolean bt_updates;
	private String found;
	
	private LocationListener loclist;
	
	private LocationManager locationManager;
	private Location location;
	private String provider;
	
	private final Handler handler = new Handler();
	private Handler myHandler;
	private Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		bt_updates = false;
		loclist = this;
		
        setContentView(R.layout.activity_map_treasure);

		p("MapLocateCapabilityActivity created!");
		
		tPoints = (TextView) findViewById(R.id.mapTreasurePoints);
		tPoints.setVisibility(View.INVISIBLE);
		tMyPosition1 = (TextView) findViewById(R.id.mapTreasureMyPosition1);
		tMyPosition2 = (TextView) findViewById(R.id.mapTreasureMyPosition2);
		tMessage = (TextView) findViewById(R.id.mapTreasureMessage);
		tDistance = (TextView) findViewById(R.id.mapTreasureDistance);
		tBluetooth = (TextView) findViewById(R.id.mapTreasureBluetooth);
		tlayout = (RelativeLayout) findViewById(R.id.mapTreasureLayout);
		closeButton = (Button) findViewById(R.id.mapTreasureClose);
		
		
		Intent args = getIntent();

		String message = args.getStringExtra("locateName");
		
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
	    myHandler = new Handler();
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
		System.out.println("LAT = " + lat);
		System.out.println("LNG = " + lng);
		
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
	
	private Runnable treasureFoundToServer = new Runnable() {
	    public void run() {
	    	try
			{
				// Object to be sent
				JSONObject sendData = new JSONObject();
				
				sendData.put("foundTreasure", found);
				OrchestratorJsActivity.ojsContextData(sendData);
			}
			catch(JSONException ex)
			{
				ex.printStackTrace();
			}
	    }
	};
	
	private TimerTask updateGUI = new TimerTask()
	{       
		@Override
	    public void run()
	    {
	        handler.post(new Runnable()
	        {
	            public void run()
	            {
	            	if (!TreasureCapability.getConnection())
	            	{
	            		tMessage.setText("Connection lost");
	            	}
	            	
	            	if (TreasureCapability.getTarget() == null)
	        			return;
	            	
	        		Integer rssi = TreasureCapability.getTargetRSSI();
	        		System.out.println("rssi " + rssi);
	        		if (rssi != null)
	        		{
	        			TreasureCapability.setFastUpdates(true);
	        			if (rssi > 195)
	        			{
	        				if (!TreasureCapability.isTreasureFound(TreasureCapability.getTarget()))
	        				{
	        			        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_points);
	        					
	        					anim.setAnimationListener(new AnimationListener() {
	        					    @Override
	        					    public void onAnimationEnd(Animation arg0)
	        					    {
	        					    	tPoints.setVisibility(View.INVISIBLE);
	        					    }

									@Override
									public void onAnimationRepeat(Animation arg0)
									{
										tPoints.setVisibility(View.VISIBLE);
									}

									@Override
									public void onAnimationStart(Animation arg0)
									{
										tPoints.setVisibility(View.VISIBLE);
									}
	        					});
	        					
	        					tPoints.setAnimation(anim);
	        					
	        					TreasureCapability.addTreasureFound(TreasureCapability.getTarget());
	        					found = TreasureCapability.getTarget();
	        					myHandler.postDelayed(treasureFoundToServer, 5000);
	        				}
	        				tBluetooth.setText("You have already found this treasure!");
	        				tDistance.setText("Congratulations!");
	        				tlayout.setBackgroundColor(Color.WHITE);
	        			}
	        			else if (rssi > 140)
	        			{
	        				if (!TreasureCapability.isTreasureFound(TreasureCapability.getTarget()))
	        				{
		        				tBluetooth.setText("The treasure is inside your Bluetooth range.");
		        				tDistance.setText("You are close your target!");
		        				tlayout.setBackgroundColor(Color.parseColor("#B2FF66"));
	        				}
	        				else
	        				{
	        					tBluetooth.setText("You have already found this treasure!");
		        				tDistance.setText("Congratulations!");
		        				tlayout.setBackgroundColor(Color.WHITE);
	        				}
	        			}
	        			else if (rssi > 60)
	        			{
	        				if (!TreasureCapability.isTreasureFound(TreasureCapability.getTarget()))
	        				{
		        				tBluetooth.setText("The treasure is inside your Bluetooth range.");
		        				tDistance.setText("You are getting closer...");
		        				tlayout.setBackgroundColor(Color.parseColor("#FFFF66"));
	        				}
	        				else
	        				{
	        					tBluetooth.setText("You have already found this treasure!");
		        				tDistance.setText("Congratulations!");
		        				tlayout.setBackgroundColor(Color.WHITE);
	        				}
	        			}
	        			else
	        			{
	        				if (!TreasureCapability.isTreasureFound(TreasureCapability.getTarget()))
	        				{
	        					tBluetooth.setText("The treasure is inside your Bluetooth range.");
	        					tlayout.setBackgroundColor(Color.parseColor("#FFB266"));
	        				}
	        				else
	        				{
	        					tBluetooth.setText("You have already found this treasure!");
		        				tDistance.setText("Congratulations!");
		        				tlayout.setBackgroundColor(Color.WHITE);
	        				}
	        			}
	        			
	        			if (!bt_updates)
    	    			{
    	    				bt_updates = true;
    	    				TreasureCapability.setFastUpdates(true);
    	    			}
	        		}
	        		else
	        		{
	        			tBluetooth.setText("No bluetooth proximity");
	        			Location targetLoc = TreasureCapability.getTargetLocation();
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
		    	    			if (!bt_updates)
		    	    			{
		    	    				bt_updates = true;
		    	    				TreasureCapability.setFastUpdates(true);
		    	    			}
		    	    		}
		    	    		else
		    	    		{
		    	    			tlayout.setBackgroundColor(Color.WHITE);
		    	    			if (bt_updates)
		    	    			{
		    	    				bt_updates = false;
		    	    				TreasureCapability.setFastUpdates(false);
		    	    			}
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
