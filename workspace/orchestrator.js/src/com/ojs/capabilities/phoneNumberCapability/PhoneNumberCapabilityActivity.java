package com.ojs.capabilities.phoneNumberCapability;

import org.json.JSONException;
import org.json.JSONObject;

import com.ojs.OrchestratorJsActivity;
import com.ojs.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PhoneNumberCapabilityActivity extends Activity {

	
	protected static final String TAG = PhoneNumberCapabilityActivity.class.getSimpleName();
	
	private Handler myHandler = new Handler();
	private EditText phoneEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_get_phone_number);

		p("PhoneNumberCapabilityActivity created!");
		
		phoneEdit = (EditText)findViewById(R.id.phoneNumberInsert);
		
		// Restore preferences
		SharedPreferences settings = getPreferences(0);
		phoneEdit.setText(settings.getString("phoneNumber", ""));
		
		try
		{
			Intent args = getIntent();

			String message = args.getStringExtra("phoneMessage");
			Integer delayTime = args.getIntExtra("timeout", 0);
			
			TextView tMessage = (TextView)findViewById(R.id.phoneNumberMessage);
			tMessage.setText(message);
			
			final Button buttonCancelar = (Button) findViewById(R.id.phoneNumberCancelar);
			buttonCancelar.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v)
				{
					finish();
				}
	        });
	         
	        final Button buttonAceptar = (Button) findViewById(R.id.phoneNumberAceptar);
	        buttonAceptar.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v)
	            {
	            	try
	     			{
	     				JSONObject sendPhone = new JSONObject();
	     				sendPhone.put("phoneNumber", phoneEdit.getText().toString());
						OrchestratorJsActivity.ojsContextData(sendPhone);
						
						SharedPreferences settings = getPreferences(0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("phoneNumber", phoneEdit.getText().toString());
						
						editor.commit();
					}
	     			catch (JSONException e)
	     			{
						e.printStackTrace();
					}
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
	
	
	private Runnable closeControls = new Runnable()
	{
	    public void run() {
	        finish();
	    }
	};
}
