package com.ojs.capabilities.socialNetworkCapability;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ojs.OrchestratorJsActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

public class SocialNetworkCapability {

	private static Context applicationContext_;

	public void initCapability( Context applicationContext )
	{
		SocialNetworkCapability.applicationContext_ = applicationContext;
	}

	public void requestDetails() throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "requesting Social Network data!");
		
		try
		{
			JSONObject sendData = new JSONObject();
			JSONArray socialData = new JSONArray();
			
			Account[] accounts = AccountManager.get(applicationContext_).getAccounts();
			for (Account account : accounts)
			{
			    System.out.println("Account: " + account);
			    
			    if (account.type.equalsIgnoreCase("com.twitter.android.auth.login"))
			    {
			    	JSONObject cuenta = new JSONObject();
			    	cuenta.put("twitter", account.name);
			    	socialData.put(cuenta);
			    }
			    else if (account.type.equalsIgnoreCase("com.facebook.auth.login"))
			    {
			    	JSONObject cuenta = new JSONObject();
			    	cuenta.put("facebook", account.name);
			    	socialData.put(cuenta);
			    }
			    // Instagram seems to be not accessible
			}
			
			sendData.put("socialData", socialData);
			OrchestratorJsActivity.ojsContextData(sendData);
		}
		catch(Exception ex)
    	{
    		
    	}
	}
}
