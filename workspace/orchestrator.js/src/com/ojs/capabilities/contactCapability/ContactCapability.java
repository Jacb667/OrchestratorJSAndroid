package com.ojs.capabilities.contactCapability;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ojs.OrchestratorJsActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactCapability {
	
	private static Context applicationContext_;
	
	public void initCapability( Context applicationContext )
	{
		ContactCapability.applicationContext_ = applicationContext;
	}

	public void getContactList()
	{
		ArrayList<String> phoneNumbers = new ArrayList<String>();
		ContentResolver cr = applicationContext_.getContentResolver();
		Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
	    while (phones.moveToNext())
	    {
	      //String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
	      String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	      phoneNumbers.add(phoneNumber);
	    }
	    
	    phones.close();  // close cursor

		System.out.println(phoneNumbers);
		
		try
		{
			// Create a JSON Object to send the data
			JSONObject sendData = new JSONObject();
			
			// Create a JSON Array to store all the phone numbers
			JSONArray numbers = new JSONArray();
			
			for (String number : phoneNumbers)
			{
				numbers.put(number);
			}
			
			sendData.put("contactList", numbers);
			OrchestratorJsActivity.ojsContextData(sendData);
		}
		catch(JSONException ex)
		{
			ex.printStackTrace();
		}
	}
}
