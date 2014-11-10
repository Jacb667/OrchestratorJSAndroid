package com.ojs.capabilities.notificationCapability;

import com.ojs.OrchestratorJsActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class NotificationCapability {

	private static Context applicationContext_;

	public void initCapability( Context applicationContext )
	{
		NotificationCapability.applicationContext_ = applicationContext;
	}

	public void showNotification( String title, String message ) throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "showing notification!");
		
		Intent i = new Intent(NotificationCapability.applicationContext_, NotificationCapabilityActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		i.putExtra("notificationTitle", title);
		i.putExtra("notificationMessage", message);
		
		NotificationCapability.applicationContext_.startActivity(i);
	}
	
	public void showNotificationTimeout( String title, String message, Integer seconds ) throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "showing notification!");
		
		Intent i = new Intent(NotificationCapability.applicationContext_, NotificationCapabilityActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		i.putExtra("notificationTitle", title);
		i.putExtra("notificationMessage", message);
		i.putExtra("timeout", seconds*1000);
		
		NotificationCapability.applicationContext_.startActivity(i);
	}
	
	public void showToast( String message, Integer seconds ) throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "showing notification!");
		
		Toast.makeText(applicationContext_, message, seconds*1000).show();
	}

}
