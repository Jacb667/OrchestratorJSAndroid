package com.ojs.capabilities.locateCapability;

import java.util.List;

import com.ojs.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewPerson extends ArrayAdapter<String> {
	private final Activity context;
	private final List<String[]> listdata;

	public ListViewPerson (Activity context, List<String[]> listdata) {
		super(context, R.layout.list_single, new String[listdata.size()]);
		this.context = context;
		this.listdata = listdata;
	}

	@Override
	public View getView (int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_single, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.customLVtxt);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.customLVimg);
		
		txtTitle.setText(listdata.get(position)[0]);

		try
		{
			byte[] decodedString = Base64.decode(listdata.get(position)[1], Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
			imageView.setImageBitmap(decodedByte);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return rowView;
	}
}