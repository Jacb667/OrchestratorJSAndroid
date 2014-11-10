package com.ojs.capabilities.notificationCapability;

import com.ojs.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NotificationCapabilityActivity extends Activity {

	
	protected static final String TAG = NotificationCapabilityActivity.class.getSimpleName();
	
	private Handler myHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_notification);

		p("NotificationCapabilityActivity created!");
		
		try
		{
			Intent args = getIntent();

			String title = args.getStringExtra("notificationTitle");
			String message = args.getStringExtra("notificationMessage");
			Integer delayTime = args.getIntExtra("timeout", 0);
			
			TextView tTitle = (TextView)findViewById(R.id.notificationTitleText);
			TextView tMessage = (TextView)findViewById(R.id.notificationMessageText);
			tTitle.setText(title);
			tMessage.setText(message);
			
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			// Vibrate for 500 milliseconds
			v.vibrate(500);
			
			final Button button = (Button) findViewById(R.id.notificationCerrarBoton);
	         button.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v)
	             {
	                 finish();
	             }
	         });
	         
	         if (delayTime != 0)
	        	 myHandler.postDelayed(closeControls, delayTime);
		}
		catch (Exception e)
		{
			p(e.toString());
		}
	}

	private void p(String s)
	{
		Log.d(TAG, s);
	}
	
	
	private Runnable closeControls = new Runnable() {
	    public void run() {
	        finish();
	    }
	};
}
