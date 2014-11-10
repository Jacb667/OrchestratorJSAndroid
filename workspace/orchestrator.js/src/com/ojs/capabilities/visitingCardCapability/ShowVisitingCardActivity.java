package com.ojs.capabilities.visitingCardCapability;

import com.ojs.R;

import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowVisitingCardActivity extends Activity {

	protected static final String TAG = ShowVisitingCardActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_show_v_card);

		p("Show visiting card created!");
		
		TextView tname = (TextView)findViewById(R.id.showVCname);
		TextView tphone = (TextView)findViewById(R.id.showVCphone);
		TextView taddress = (TextView)findViewById(R.id.showVCaddress);
		TextView tdescription = (TextView)findViewById(R.id.showVCdescription);
		TextView temail = (TextView)findViewById(R.id.showVCemail);
		ImageView timage = (ImageView)findViewById(R.id.showVCimage);
		
		Button imageButton = (Button)findViewById(R.id.showVCcloseButton);
		
        imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
        });
        
        Intent args = getIntent();

        String preview = args.getStringExtra("preview");
        if (preview == null)
        {
        	Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(500);
        }
        
		String name = args.getStringExtra("name");
		String phone = args.getStringExtra("phone");
		String address = args.getStringExtra("address");
		String description = args.getStringExtra("description");
		String email = args.getStringExtra("email");
		String image = args.getStringExtra("image");
		
		tname.setText(name != null ? name : "");
		tphone.setText(phone != null ? phone : "");
		taddress.setText(address != null ? address : "");
		tdescription.setText(description != null ? description : "");
		temail.setText(email != null ? email : "");
		
		if (image != null)
		{
			try
			{
				byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
				timage.setImageBitmap(decodedByte);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	private void p(String s)
	{
		Log.d(TAG, s);
	}
	
}
