package com.ojs.capabilities.visitingCardCapability;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.ojs.OrchestratorJsActivity;
import com.ojs.R;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
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
import android.widget.Toast;

public class NewVisitingCardActivity extends Activity {

	private static final int SELECT_PICTURE = 1;
	 
    private String selectedImagePath;
    
	protected static final String TAG = NewVisitingCardActivity.class.getSimpleName();
	
	private EditText tname;
	private EditText taddress;
	private EditText tphone;
	private EditText tdescription;
	private EditText temail;
	private ImageView timage;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_visiting_card);

		p("VisitingCardCapabilityActivity created!");
		
		tname = (EditText)findViewById(R.id.visitingcardNameField);
		taddress = (EditText)findViewById(R.id.visitingcardAddressField);
		tphone = (EditText)findViewById(R.id.visitingcardPhoneField);
		tdescription = (EditText)findViewById(R.id.visitingcardDescriptionield);
		temail = (EditText)findViewById(R.id.visitingcardEmailField);
		timage = (ImageView)findViewById(R.id.visitingcardImageField);
		
		// Restore preferences
		SharedPreferences settings = getPreferences(0);
		
		tname.setText(settings.getString("pName", ""));
		taddress.setText(settings.getString("pAddress", ""));
		tphone.setText(settings.getString("pPhone", ""));
		tdescription.setText(settings.getString("pDescription", ""));
		temail.setText(settings.getString("pEmail", ""));
		
		if (!settings.getString("pImage", "").isEmpty())
		{
			try
			{
				byte[] decodedString = Base64.decode(settings.getString("pImage", ""), Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
				timage.setImageBitmap(decodedByte);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		// End of preferences
		
		Button imageButton = (Button)findViewById(R.id.visitingcardImageButton);
		Button previewButton = (Button)findViewById(R.id.visitingcardPreviewButton);
		Button acceptButton = (Button)findViewById(R.id.visitingcardOKButton);
		Button cancelButton = (Button)findViewById(R.id.visitingcardCancelButton);
		
        imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
			}
        });
        
        previewButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				previewVisitingCard();
			}
        });
        
        cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
        });
        
        acceptButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendVisitingCard();
				finish();
			}
        });
        
	}
	
	private void previewVisitingCard()
	{
		String name = tname.getText().toString();
		String address = taddress.getText().toString();
		String phone = tphone.getText().toString();
		String description = tdescription.getText().toString();
		String email = temail.getText().toString();
		
		byte[] b = {};
		Bitmap bm = BitmapFactory.decodeFile(selectedImagePath);
		if (bm == null)
			bm = ((BitmapDrawable)timage.getDrawable()).getBitmap();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		b = baos.toByteArray();
		
		System.out.println(Arrays.toString(b));
		String image = Base64.encodeToString(b, Base64.DEFAULT);
		
		Intent intent = new Intent(getApplicationContext(), ShowVisitingCardActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("name", name);
		intent.putExtra("address", address);
		intent.putExtra("phone", phone);
		intent.putExtra("description", description);
		intent.putExtra("email", email);
		intent.putExtra("image", image);
		intent.putExtra("preview", "yes");
		getApplicationContext().startActivity(intent);
	}
	
	private void sendVisitingCard()
	{
		String name = tname.getText().toString();
		String address = taddress.getText().toString();
		String phone = tphone.getText().toString();
		String description = tdescription.getText().toString();
		String email = temail.getText().toString();
		
		byte[] b = {};
		Bitmap bm = BitmapFactory.decodeFile(selectedImagePath);
		if (bm == null)
			bm = ((BitmapDrawable)timage.getDrawable()).getBitmap();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		b = baos.toByteArray();
		
		String image = Base64.encodeToString(b, Base64.DEFAULT);
		
		// Save preferences
		SharedPreferences settings = getPreferences(0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString("pName", tname.getText().toString());
		editor.putString("pAddress", taddress.getText().toString());
		editor.putString("pPhone", tphone.getText().toString());
		editor.putString("pDescription", tdescription.getText().toString());
		editor.putString("pEmail", temail.getText().toString());
		editor.putString("pImage", image);
		
		editor.commit();
		
		try
		{
			// Object to be sent
			JSONObject sendData = new JSONObject();
			
			// Visiting card
			JSONObject visitingCard = new JSONObject();
			
			visitingCard.put("name", name);
			visitingCard.put("address", address);
			visitingCard.put("phone", phone);
			visitingCard.put("email", email);
			visitingCard.put("description", description);
			visitingCard.put("image", image);
			
			sendData.put("visitingCard", visitingCard);
			OrchestratorJsActivity.ojsContextData(sendData);
		}
		catch(JSONException ex)
		{
			ex.printStackTrace();
			Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
		}
		
		Toast.makeText(this, "Visiting Card created succesfully!", Toast.LENGTH_SHORT).show();
	}
	

    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	ImageView timage = (ImageView)findViewById(R.id.visitingcardImageField);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                //System.out.println("Image Path : " + selectedImagePath);
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
