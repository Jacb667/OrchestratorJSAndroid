package com.ojs.capabilities.locateCapability;

import java.io.ByteArrayOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.ojs.OrchestratorJsActivity;
import com.ojs.R;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NewLocateCapabilityActivity extends Activity {

	protected static final String TAG = NewLocateCapabilityActivity.class.getSimpleName();
	
	private static final int SELECT_PICTURE = 1;
    
	private EditText nameEdit;
	private ImageView photoEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_new_locate);

		p("LocateCapabilityActivity created!");
		
		nameEdit = (EditText)findViewById(R.id.newLocateInsert);
		photoEdit = (ImageView)findViewById(R.id.newLocateImage);
		
		// Restore preferences
		SharedPreferences settings = getPreferences(0);
		
		nameEdit.setText(settings.getString("iName", ""));
		
		if (!settings.getString("iImage", "").isEmpty())
		{
			try
			{
				byte[] decodedString = Base64.decode(settings.getString("iImage", ""), Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
				photoEdit.setImageBitmap(decodedByte);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		// End of preferences
		
		Intent args = getIntent();

		String message = args.getStringExtra("locateMessage");
		
		TextView tMessage = (TextView)findViewById(R.id.newLocatePersonMessage);
		tMessage.setText(message);
		
		final Button buttonCancelar = (Button) findViewById(R.id.newLocateCancelar);
		buttonCancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)
			{
				finish();
			}
        });
        
        final Button buttonAceptar = (Button) findViewById(R.id.newLocateAceptar);
        buttonAceptar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
            	sendProfile();
                finish();
            }
        });
        
        final Button buttonImage = (Button) findViewById(R.id.newLocateImageButton);
        buttonImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
			}
        });
		
	}
	
	private void sendProfile()
	{
		String name = nameEdit.getText().toString();
		Bitmap bm = ((BitmapDrawable) photoEdit.getDrawable()).getBitmap();
		
		byte[] b = {};
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		b = baos.toByteArray();
		
		String image = Base64.encodeToString(b, Base64.DEFAULT);
		
		// Save preferences
		SharedPreferences settings = getPreferences(0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString("iName", nameEdit.getText().toString());
		editor.putString("iImage", image);
		
		editor.commit();
		
		try
		{
			BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
			String bmac = "";
			if (mBtAdapter != null)
				bmac = mBtAdapter.getAddress();
			
			// Object to be sent
			JSONObject sendData = new JSONObject();
			
			// Visiting card
			JSONObject profile = new JSONObject();
			
			profile.put("name", name);
			profile.put("image", image);
			profile.put("bmac", bmac);
			
			sendData.put("locateProfile", profile);
			OrchestratorJsActivity.ojsContextData(sendData);
		}
		catch(JSONException ex)
		{
			ex.printStackTrace();
			Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
		}
		
		Toast.makeText(this, "Profile created succesfully!", Toast.LENGTH_SHORT).show();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	ImageView timage = (ImageView)findViewById(R.id.newLocateImage);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                timage.setImageURI(selectedImageUri);
            }
        }
    }
 
    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

	private void p(String s)
	{
		Log.d(TAG, s);
	}
}
