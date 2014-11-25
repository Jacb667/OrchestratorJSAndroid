package com.ojs.capabilities.treasureCapability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ScoreListActivity extends Activity {

	protected static final String TAG = ScoreListActivity.class.getSimpleName();
	
	private Handler myHandler = new Handler();
	
	final private String LV_TITLE = "LV_TITLE";
	final private String LV_TEXT = "LV_TEXT";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_notification);

		p("ScoreListActivity created!");
		

		Intent args = getIntent();

		String title = args.getStringExtra("notificationTitle");
		String message = args.getStringExtra("notificationMessage");
		String jsondata = args.getStringExtra("notificationJson");
		Integer delayTime = args.getIntExtra("timeout", 0);
		
		TextView tTitle = (TextView)findViewById(R.id.notificationTitleText);
		TextView tMessage = (TextView)findViewById(R.id.notificationMessageText);
		tTitle.setText(title);
		tMessage.setText(message);
		
		if (jsondata != null)
		{
			ListView tlist = (ListView) findViewById(R.id.notificationMessageList);
			
			try
			{
				List<Map<String, String>> datalist = new ArrayList<Map<String, String>>();
				JSONObject jsonobject = new JSONObject(jsondata);
				
				Iterator<?> keys = jsonobject.keys();
		        while( keys.hasNext() )
		        {
		            String key = (String) keys.next();
		            Map<String, String> map = new HashMap<String, String>();
		            map.put(LV_TITLE, key);
		            Object object = jsonobject.get(key);
		            if (object instanceof Integer)
		            	map.put(LV_TEXT, ((Integer) object).toString());
		            else if (object instanceof String)
		            	map.put(LV_TEXT, (String) object);
		            datalist.add(map);
		        }
				
				ListAdapter listAdapter = new SimpleAdapter(this, datalist, android.R.layout.simple_list_item_2,
                        new String[] {LV_TITLE, LV_TEXT}, new int[] {android.R.id.text1, android.R.id.text2});

		        tlist.setAdapter(listAdapter);
			}
			catch (JSONException ex)
			{
				ex.printStackTrace();
			}
		}
		
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		// Vibrate for 500 milliseconds
		if (savedInstanceState == null || !savedInstanceState.getBoolean("done"))
		{
			v.vibrate(500);
		}
		
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

	private void p(String s)
	{
		Log.d(TAG, s);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
	    savedInstanceState.putBoolean("done", true);
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	private Runnable closeControls = new Runnable() {
	    public void run() {
	        finish();
	    }
	};
}
