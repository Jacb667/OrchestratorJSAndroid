package com.ojs.capabilities.treasureCapability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ojs.OrchestratorJsActivity;
import com.ojs.R;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class LocateTreasureCapabilityActivity extends Activity {

	protected static final String TAG = LocateTreasureCapabilityActivity.class.getSimpleName();
	private String selectedName;
	
	final private String LV_TITLE = "LV_TITLE";
	final private String LV_TEXT = "LV_TEXT";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_locate_person);

		p("LocateTreasureCapabilityActivity created!");
		
		Intent intent = getIntent();
		String message = intent.getStringExtra("locateMessage");
		String jsondata = intent.getStringExtra("locateJson");
		
		TextView title = (TextView) findViewById(R.id.locatePersonTitle);
		title.setText(message);
		
		selectedName = null;
		
		ListView tlist = (ListView) findViewById(R.id.locatePersonList);
		
		Criteria criteria = new Criteria();
		LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
	    String provider = locationManager.getBestProvider(criteria, false);
	    Location mylocation = locationManager.getLastKnownLocation(provider);
	    
	    final List<Map<String, String>> datalist = new ArrayList<Map<String, String>>();
		
		if (jsondata != null)
		{
			try
			{
				JSONObject jsonobject = new JSONObject(jsondata);
				
				// Get a new device
				Iterator<?> keys = jsonobject.keys();
		        while( keys.hasNext() )
		        {
		            String key = (String) keys.next();
		            JSONObject json = jsonobject.getJSONObject(key);
		            
		            JSONArray jlocation = json.getJSONArray("location");
		            
		            Double lat = jlocation.getDouble(0);
		            Double lng = jlocation.getDouble(1);

		            Map<String, String> map = new HashMap<String, String>();
		            
		            map.put(LV_TITLE, key);
		            if (mylocation != null)
		            {
		            	Location loc = new Location("");
						loc.setLatitude(lat);
						loc.setLongitude(lng);
						
						String unit = "meters";
						Float distance = mylocation.distanceTo(loc);
						if (distance > 1000f)
						{
							unit = "kilometers";
							distance = distance / 1000;
						}
						
						map.put(LV_TEXT, "Distance " + distance + " " + unit);
		            }
		            
		            datalist.add(map);
		        }
			}
			catch (JSONException ex)
			{
				ex.printStackTrace();
			}
			

			ListAdapter listAdapter = new SimpleAdapter(this, datalist, android.R.layout.simple_list_item_2,
                    new String[] {LV_TITLE, LV_TEXT}, new int[] {android.R.id.text1, android.R.id.text2});

	        tlist.setAdapter(listAdapter);
		}
		
        tlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
            	for (int j = 0; j < parent.getChildCount(); j++)
            		parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

                // change the background color of the selected element
                view.setBackgroundColor(Color.parseColor("#33b5e5"));
                
                selectedName = datalist.get(position).get(LV_TITLE);
            }
        });
		
		Button buttonAccept = (Button) findViewById(R.id.locatePersonAceptar);
		buttonAccept.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)
			{
				if (selectedName != null)
				{
					try
					{
						// Object to be sent
						JSONObject sendData = new JSONObject();
						sendData.put("locateTreasure", selectedName);
						OrchestratorJsActivity.ojsContextData(sendData);
					}
					catch(JSONException ex)
					{
						ex.printStackTrace();
					}
					
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "No treasure selected", Toast.LENGTH_SHORT).show();
				}
			}
        });
		
		Button buttonCancel = (Button) findViewById(R.id.locatePersonCancelar);
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)
			{
				finish();
			}
        });
	}

	private void p(String s)
	{
		Log.d(TAG, s);
	}
	
}
