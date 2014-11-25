package com.ojs.capabilities.phoneNumberCapability;

import com.ojs.OrchestratorJsActivity;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneNumberCapability {

	private static Context applicationContext_;

	public void initCapability( Context applicationContext )
	{
		PhoneNumberCapability.applicationContext_ = applicationContext;
	}

	public void askPhoneNumber( String message, Integer timeout ) throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "asking Phone Number!");
		
		// Create a new Intent to start the activity
		Intent i = new Intent(PhoneNumberCapability.applicationContext_, PhoneNumberCapabilityActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Send the arguments from the platform
		i.putExtra("phoneMessage", message);
		i.putExtra("timeout", timeout*1000);
		
		// Start the capability
		PhoneNumberCapability.applicationContext_.startActivity(i);
	}
	
	public String getPhoneNumber() throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "getting Phone Number!");
		
		// Gets the phone number from Android
		TelephonyManager tMgr = (TelephonyManager)applicationContext_.getSystemService(Context.TELEPHONY_SERVICE);
	    String mPhoneNumber = tMgr.getLine1Number();
	    
	    // Return it to server
	    return mPhoneNumber;
	}
	
}
