package com.ojs.capabilities.visitingCardCapability;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class VisitingCardCapability extends Activity {

	private static Context applicationContext_;


	public void initCapability( Context applicationContext )
	{
		VisitingCardCapability.applicationContext_ = applicationContext;
	}

	public void showVisitingCard(String name, String address, String phone, String email, String description, String image)
	{
		Log.d("com.ojs", "showVisitingCard " + name);
		
		Intent intent = new Intent(VisitingCardCapability.applicationContext_, ShowVisitingCardActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("name", name);
		intent.putExtra("address", address);
		intent.putExtra("phone", phone);
		intent.putExtra("description", description);
		intent.putExtra("email", email);
		intent.putExtra("image", image);
		applicationContext_.startActivity(intent);
	}
		
	public void requestVisitingCard()
    {
		Intent intent = new Intent(VisitingCardCapability.applicationContext_, NewVisitingCardActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		applicationContext_.startActivity(intent);
    }

}
