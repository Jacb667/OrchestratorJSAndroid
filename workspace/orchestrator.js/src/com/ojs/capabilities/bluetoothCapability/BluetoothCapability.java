package com.ojs.capabilities.bluetoothCapability;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ojs.OrchestratorJsActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;


public class BluetoothCapability {
	
	private static Context applicationContext_;
	
	private BluetoothAdapter mBtAdapter;
    private Map<String, Integer> mDevicesMap;
    private boolean connected = false;
    
    private Handler myHandler = new Handler();

	public void initCapability( Context applicationContext )
	{
		BluetoothCapability.applicationContext_ = applicationContext;
		
		mDevicesMap = new HashMap<String, Integer>();
		
		// Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        applicationContext_.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        applicationContext_.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	public void updateProximity(Integer delay) throws Exception
	{
		Log.d(OrchestratorJsActivity.TAG, "updating bluetooth proximity!");
		connected = true;
		
		doDiscovery();
		
		if (delay != 0)
        	myHandler.postDelayed(sendDevices, delay*1000);
	}
	
	private void doDiscovery()
	{
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
         // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }
	
	private Runnable sendDevices = new Runnable() {
	    public void run() {
	    	
	    	try
	    	{
	    		// Create a JSON Object to send the data
	    		JSONObject sendData = new JSONObject();
	    		
	    		// Create a JSON Array to store all the devices
	    		JSONArray devices = new JSONArray();
	    		
	    		for (Entry<String, Integer> entry : mDevicesMap.entrySet())
	    		{
	    			// Create a JSON Array to add the device
	    			JSONArray device = new JSONArray();
		    		device.put(entry.getKey());
		    		device.put(entry.getValue());
		    		
		    		// Add the device to devices array
		    		devices.put(device);
	    		}
	    		System.out.println("ENVIO DATOS BLUETOOTH!");
	    		sendData.put("bt_devices", devices);
		    	System.out.println(sendData);
				OrchestratorJsActivity.ojsContextData(sendData);
	    	}
	    	catch(Exception ex)
	    	{
	    		
	    	}
	    }
	};
	
	// The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                mDevicesMap.put(device.getAddress().toLowerCase(), RSSI);
            }
            // When discovery is finished, change the Activity title
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
            	if (connected && OrchestratorJsActivity.getConnected())
            		sendDevices.run();
            }
        }
    };
    
    public void onDisconnect()
    {
    	Log.d(OrchestratorJsActivity.TAG, "BluetoothCapability: Connection lost");
    	
    	connected = false;
    	
    	// Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
         // Unregister broadcast listeners
        applicationContext_.unregisterReceiver(mReceiver);
    }

}
