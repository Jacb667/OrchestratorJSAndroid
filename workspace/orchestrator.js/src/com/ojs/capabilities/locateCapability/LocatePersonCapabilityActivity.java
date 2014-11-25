package com.ojs.capabilities.locateCapability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ojs.OrchestratorJsActivity;
import com.ojs.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocatePersonCapabilityActivity extends Activity {

	protected static final String TAG = LocatePersonCapabilityActivity.class.getSimpleName();
	private List<String[]> listdata;
	private String selectedName;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_locate_person);

		p("LocatePersonCapabilityActivity created!");
		
		Intent intent = getIntent();
		String message = intent.getStringExtra("locateMessage");
		String jsondata = intent.getStringExtra("locateJson");
		
		TextView title = (TextView) findViewById(R.id.locatePersonTitle);
		title.setText(message);
		
		selectedName = null;
		
		if (jsondata != null)
		{
			listdata = new ArrayList<String[]>();
			try
			{
				JSONObject jsonobject = new JSONObject(jsondata);
				
				// Get a new device
				Iterator<?> keys = jsonobject.keys();
		        while( keys.hasNext() )
		        {
		            String key = (String) keys.next();
		            JSONObject json = jsonobject.getJSONObject(key);
		            
		            String jname = json.getString("name");
		            String jimage = json.getString("image");

					String[] row = new String[2];
		            row[0] = jname;
		            row[1] = jimage;
					
					listdata.add(row);
		        }
			}
			catch (JSONException ex)
			{
				ex.printStackTrace();
			}
			
			ListViewPerson adapter = new ListViewPerson (this, listdata);
		    ListView tlist = (ListView) findViewById(R.id.locatePersonList);
	        tlist.setAdapter(adapter);
	        tlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                	for (int j = 0; j < parent.getChildCount(); j++)
                		parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

                    // change the background color of the selected element
                    view.setBackgroundColor(Color.parseColor("#33b5e5"));
                    
                    selectedName = listdata.get(position)[0];
                }
            });
		}
		
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
						sendData.put("locatePerson", selectedName);
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
					Toast.makeText(getApplicationContext(), "No person selected", Toast.LENGTH_SHORT).show();
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
