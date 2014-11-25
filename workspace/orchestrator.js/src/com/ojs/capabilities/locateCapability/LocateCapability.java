package com.ojs.capabilities.locateCapability;

import java.util.Date;

import org.json.JSONArray;

import com.ojs.OrchestratorJsActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

public class LocateCapability {

	private static String myName;
	private static Context applicationContext_;
	
	private LocateBluetoothHelper lbh;
	private static String targetName = null;
	private static Location targetLoc = null;
	private static Integer bluetoothRSSI = null;
	private static Date lastRSSI = null;
	
	private static boolean connected = false;
	
	public void initCapability( Context applicationContext )
	{
		LocateCapability.applicationContext_ = applicationContext;
	}

	public void requestLocateProfile( String message ) throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "requesting Locate Profile!");
		
		Intent i = new Intent(LocateCapability.applicationContext_, NewLocateCapabilityActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		i.putExtra("locateMessage", message);
		
		LocateCapability.applicationContext_.startActivity(i);
	}
	
	public void askLocatePerson( String message, String jsondata ) throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "requesting Locate Person!");
		
		Intent i = new Intent(LocateCapability.applicationContext_, LocatePersonCapabilityActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		i.putExtra("locateMessage", message);
		i.putExtra("locateJson", jsondata);
		
		LocateCapability.applicationContext_.startActivity(i);
	}
	
	public void updateTargetPosition( String name, String position ) throws Exception
	{
		System.out.println("name = " + name);
		System.out.println("position = " + position);
		
		JSONArray jloc = new JSONArray(position);
		Double lat = jloc.getDouble(0);
		Double lng = jloc.getDouble(1);
		
		targetLoc = new Location("");
		targetLoc.setLatitude(lat);
		targetLoc.setLongitude(lng);
		
		System.out.println("targetLoc " + targetLoc);
	}
	
	public static Location getTargetLocation()
	{
		return targetLoc;
	}
	
	public static Integer getTargetRSSI()
	{
		Date expire = new Date(lastRSSI.getTime() + 30*1000);
		Date now = new Date();
		
		// Expire = 60000
		// Now = 0
		// Result: No expired
		
		// Expire = 60000
		// Now = 61000
		// Result: Expired
		
		if (expire.before(now))
			bluetoothRSSI = null;
		
		return bluetoothRSSI;
	}
	
	public static String getTarget()
	{
		return targetName;
	}
	
	public static boolean getConnection()
	{
		return connected;
	}
	
	public static void setTargetRSSI(Integer rssi)
	{
		bluetoothRSSI = rssi;
		lastRSSI = new Date();
	}
	
	public void setTargetBluetooth( String name, String bmac, String image ) throws Exception
	{
		if (name == null || name.isEmpty())
		{
			targetName = null;
			lbh.onDisconnect();
			return;
		}
		
		connected = true;
		
		targetName = name;
		bluetoothRSSI = null;
		lastRSSI = new Date();
		
		if (lbh == null)
			lbh = new LocateBluetoothHelper(applicationContext_);
		
		lbh.setTarget(bmac);
		lbh.updateProximity();
		
		Intent i = new Intent(LocateCapability.applicationContext_, MapLocateCapabilityActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		i.putExtra("locateName", name);
		i.putExtra("locateImage", image);
		
		LocateCapability.applicationContext_.startActivity(i);
	}
	
	public static String MyName()
	{
		return myName;
	}
	
	public void onDisconnect()
    {
    	Log.d(OrchestratorJsActivity.TAG, "Connection lost");
    	
    	connected = false;
    	
    	if (lbh != null)
    		lbh.onDisconnect();
    }
	
}
