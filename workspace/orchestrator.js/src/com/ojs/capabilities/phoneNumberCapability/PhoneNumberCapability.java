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
		
		Intent i = new Intent(PhoneNumberCapability.applicationContext_, PhoneNumberCapabilityActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		i.putExtra("phoneMessage", message);
		i.putExtra("timeout", timeout*1000);
		
		PhoneNumberCapability.applicationContext_.startActivity(i);
	}
	
	public String getPhoneNumber() throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "getting Phone Number!");
		
		TelephonyManager tMgr = (TelephonyManager)applicationContext_.getSystemService(Context.TELEPHONY_SERVICE);
	    String mPhoneNumber = tMgr.getLine1Number();
	    
	    return mPhoneNumber;
	}
	
}
