package com.ojs.capabilities.locateCapability;

import java.util.Timer;
import java.util.TimerTask;

import com.ojs.OrchestratorJsActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class LocateBluetoothHelper {
	
	private static Context applicationContext_;
	
	private BluetoothAdapter mBtAdapter;
    private String targetMac = null;
    
	private Timer timer;

    public LocateBluetoothHelper(Context applicationContext)
    {
    	LocateBluetoothHelper.applicationContext_ = applicationContext;
		
		// Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        applicationContext_.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        applicationContext_.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        
        timer = new Timer();
    }
    
    public void setTarget(String bmac)
    {
    	targetMac = bmac;
    }
    
	public void updateProximity() throws Exception
	{
		System.out.println("Updating Bluetooth!");
		
		TimerTask updateBT = new TimerTask()
		{       
			@Override
		    public void run()
		    {
				doDiscovery();
		    }
		};
		timer = new Timer();
		timer.schedule(updateBT, 0, 12000);
	}
	
	private void doDiscovery()
	{
		if (mBtAdapter == null)
			return;
		
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
         // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }
	
	// The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                System.out.println("Found " + device.getAddress());
                if (device.getAddress().equalsIgnoreCase(targetMac))
                {
                	LocateCapability.setTargetRSSI(RSSI);
                }
            }
        }
    };
    
    /*private TimerTask updateBT = new TimerTask()
	{       
		@Override
	    public void run()
	    {
	        handler.post(new Runnable()
	        {
	            public void run()
	            {
	            	doDiscovery();
	            }
	        });
	    }
	};*/
    
    public void onDisconnect()
    {
    	Log.d(OrchestratorJsActivity.TAG, "BluetoothCapability: Connection lost");
    	
    	timer.cancel();
    	timer = null;
    	
    	// Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
         // Unregister broadcast listeners
        applicationContext_.unregisterReceiver(mReceiver);
    }
    
}
