package de.mobilecomp.managerview;

import java.util.HashMap;

import com.example.managerview_android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WifiManagerActivity extends Activity {
	private final String TAG = "MainActivity";
	TextView textView;
	private HashMap<String, String> map;
	
	private void add(String key, String value) {
		Log.d(TAG, key + ": " + value );
		map.put(key, value);
	}
	
	private void add(String key, int value) {
		Log.d(TAG, key + ": " + value );
		map.put(key, ""+value);
	}
	
	private void add(String key, Object ObjValue) {
		String value; 
		if (ObjValue != null)
			value = ObjValue.toString();
		else value = "N/A";
		Log.d(TAG, key + ": " + value );
		map.put(key, ""+value);
	}	
	
	/**
	 * display the entire Wifi related configuration 
	 */
	private void showResults() {
		 
		for (String key : map.keySet()) {
			textView.append(key + ": " + map.get(key) + "\n");
		} 
		
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_manager);
		
		final Context c = this;
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
			    Intent intent = new Intent(c, TelephonyManagerActivity.class);
                startActivity(intent);   

            }
        });	
        
		textView = (TextView) findViewById(R.id.textView1);
		map = new HashMap<String, String>();

		// get connectivity information
		
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();

		if (isConnected) add("connected?", "yes");
		else  add("connected?", "no");
		
	// get Wifi information
		
		// get connection info
		WifiManager wm =  (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		//add("ConnectionInfo", wm.getConnectionInfo().toString());
		
		add("Wifi state", wm.getWifiState() );
		add("SSID", wm.getConnectionInfo().getSSID() );
		add("Mac Address", wm.getConnectionInfo().getMacAddress() );
		add("IP Address", wm.getConnectionInfo().getIpAddress());
		add("Link speed", wm.getConnectionInfo().getLinkSpeed());
		add("BSSID", wm.getConnectionInfo().getBSSID());
		add("Rssi (signal strength)", wm.getConnectionInfo().getRssi());
		
		add("\n DHCP Info", wm.getDhcpInfo().toString() );

		// get configured network info
		
		add("\nConfiguredNetworks", wm.getConfiguredNetworks().toString());
		
		showResults();        
	}
}
