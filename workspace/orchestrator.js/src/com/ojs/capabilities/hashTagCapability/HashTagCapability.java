package com.ojs.capabilities.hashTagCapability;

import com.ojs.OrchestratorJsActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class HashTagCapability {

	private static Context applicationContext_;

	public void initCapability( Context applicationContext )
	{
		HashTagCapability.applicationContext_ = applicationContext;
	}

	public void requestHashTags() throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "requesting Hash Tags!");
		
		Intent i = new Intent(HashTagCapability.applicationContext_, HashTagCapabilityActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		HashTagCapability.applicationContext_.startActivity(i);
	}

}
