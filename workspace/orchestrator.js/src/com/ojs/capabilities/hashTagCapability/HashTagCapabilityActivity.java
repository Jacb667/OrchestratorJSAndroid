package com.ojs.capabilities.hashTagCapability;

import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ojs.capabilities.hashTagCapability.SwipeDismissListViewTouchListener;
import com.ojs.OrchestratorJsActivity;
import com.ojs.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class HashTagCapabilityActivity extends Activity {

	protected static final String TAG = HashTagCapabilityActivity.class.getSimpleName();

	private ListView tlist;
	private EditText tinsertHashTag;
	private ArrayList<String> hashTags;

    private ArrayAdapter<String> itemAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_hashtag);

		p("HashTagCapabilityActivity created!");
		
		// Initialize components
		tlist = (ListView)findViewById(R.id.hashtagList);
		tinsertHashTag = (EditText)findViewById(R.id.hasttagInsert);
		
		// Restore saved preferences
		SharedPreferences settings = getPreferences(0);
		HashSet<String> loadedHashTags = (HashSet<String>) settings.getStringSet("hashTags", null);
		hashTags = new ArrayList<String>();
		if (loadedHashTags != null)
			hashTags.addAll(loadedHashTags);

		// Prepare and set the adapter
        itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, hashTags);
        tlist.setAdapter(itemAdapter);

        // Button add new tag
		Button buttonAdd = (Button)findViewById(R.id.hashtagNew);
		buttonAdd.setOnClickListener(new View.OnClickListener()
		{
            public void onClick(View v)
            {

            	hashTags.add(0, tinsertHashTag.getText().toString());
                tinsertHashTag.setText("");

                itemAdapter.notifyDataSetChanged();
            }
        });
		
		// Button cancel - closes the activity without doing anything
		Button buttonCancel = (Button)findViewById(R.id.hashtagCancel);
		buttonCancel.setOnClickListener(new View.OnClickListener()
		{
            public void onClick(View v)
            {
            	finish();
            }
        });
		
		// Button accept - sends data and save preferences
		Button buttonAccept = (Button)findViewById(R.id.hashtagAccept);
		buttonAccept.setOnClickListener(new View.OnClickListener()
		{
            public void onClick(View v)
            {
            	try
     			{
            		// Save preferences
					SharedPreferences settings = getPreferences(0);
					SharedPreferences.Editor editor = settings.edit();
					HashSet<String> hashTagsSet = new HashSet<String>();
     				hashTagsSet.addAll(hashTags);
					editor.putStringSet("hashTags", hashTagsSet);
					editor.commit();
					
					// Send data to orchestrator
     				JSONObject sendData = new JSONObject();
     				JSONArray JSONhashTags = new JSONArray(hashTags);
     				sendData.put("hashTags", JSONhashTags);
					OrchestratorJsActivity.ojsContextData(sendData);
					
				}
     			catch (JSONException e)
     			{
					e.printStackTrace();
				}
                finish();
            }
        });

		// Listener for deleting tags
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(tlist, 
			new SwipeDismissListViewTouchListener.DismissCallbacks()
			{
				@Override
				public boolean canDismiss(int position) {
					return true;
				}

				@Override
				public void onDismiss(ListView listView,
						int[] reverseSortedPositions) {
					for (int position : reverseSortedPositions)
					{
						itemAdapter.remove(itemAdapter.getItem(position));
					}
					itemAdapter.notifyDataSetChanged();
				}
			});
		
		tlist.setOnTouchListener(touchListener);
		
		// Make sure the scroll keeps working
		tlist.setOnScrollListener(touchListener.makeScrollListener());
	}
	

	private void p(String s)
	{
		Log.d(TAG, s);
	}
	
}
