package com.ojs.capabilities.treasureCapability;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ojs.OrchestratorJsActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

public class TreasureCapability {
	
	private static Context applicationContext_;
	
	private static TreasureBluetoothHelper lbh;
	private static String targetName = null;
	private static Location targetLoc = null;
	private static Integer bluetoothRSSI = null;
	private static Date lastRSSI = null;
	
	private static List<String> foundTreasures;
	
	private static boolean connected = false;

	public void initCapability( Context applicationContext )
	{
		TreasureCapability.applicationContext_ = applicationContext;
		foundTreasures = new ArrayList<String>();
	}

	public void askLocateTreasure( String message, String jsondata ) throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "requesting Locate Person!");
		
		Intent i = new Intent(TreasureCapability.applicationContext_, LocateTreasureCapabilityActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		i.putExtra("locateMessage", message);
		i.putExtra("locateJson", jsondata);
		
		TreasureCapability.applicationContext_.startActivity(i);
	}
	
	public static Location getTargetLocation()
	{
		return targetLoc;
	}
	
	public static Integer getTargetRSSI()
	{
		Date expire = new Date(lastRSSI.getTime() + 30*1000);
		Date now = new Date();
		
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
	
	public void setTargetBluetooth( String name, String bmac ) throws Exception
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
			lbh = new TreasureBluetoothHelper(applicationContext_);
		
		lbh.setTarget(bmac);
		lbh.updateProximity(60000);
		
		Intent i = new Intent(TreasureCapability.applicationContext_, MapTreasureCapabilityActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		i.putExtra("locateName", name);
		
		TreasureCapability.applicationContext_.startActivity(i);
	}
	
	public void showScoreList( String title, String message, String jsondata ) throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "showing scorelist!");
		
		Intent i = new Intent(TreasureCapability.applicationContext_, ScoreListActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		i.putExtra("notificationTitle", title);
		i.putExtra("notificationMessage", message);
		i.putExtra("notificationJson", jsondata);
		
		TreasureCapability.applicationContext_.startActivity(i);
	}
	
	public static boolean isTreasureFound(String name)
	{
		return foundTreasures.contains(name);
	}
	
	public static void addTreasureFound(String name)
	{
		foundTreasures.add(name);
	}
	
	public void onDisconnect()
    {
    	Log.d(OrchestratorJsActivity.TAG, "Connection lost");
    	
    	connected = false;
    	
    	if (lbh != null)
    		lbh.onDisconnect();
    	
    	lbh = null;
    }

	public static void setFastUpdates(boolean b)
	{
		lbh.updateProximity(12000);
	}
}
