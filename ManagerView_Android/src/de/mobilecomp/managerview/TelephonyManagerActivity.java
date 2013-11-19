package de.mobilecomp.managerview;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import com.example.managerview_android.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;

public class TelephonyManagerActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_telephony_manager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		
		int dataState   = telephonyManager.getDataState();
		
		switch(dataState)
		{
			case TelephonyManager.DATA_CONNECTED:
				break;
			case TelephonyManager.DATA_CONNECTING:
				break;
			case TelephonyManager.DATA_DISCONNECTED:
				break;
			case TelephonyManager.DATA_SUSPENDED:
				break;
			default:
				break;
		}
		
		int phoneType   = telephonyManager.getPhoneType();
		
		switch(phoneType)
		{
			case TelephonyManager.PHONE_TYPE_CDMA:
				break;
			case TelephonyManager.PHONE_TYPE_GSM:
				break;
			case TelephonyManager.PHONE_TYPE_SIP:
				break;
			case TelephonyManager.PHONE_TYPE_NONE:
				break;
			default:
				break;
		}
		
		int networkType = telephonyManager.getNetworkType();
		
		switch(networkType)
		{
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				break;
			case TelephonyManager.NETWORK_TYPE_CDMA:
				break;
			case TelephonyManager.NETWORK_TYPE_EDGE:
				break;
			case TelephonyManager.NETWORK_TYPE_EHRPD:
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS:
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				break;
			case TelephonyManager.NETWORK_TYPE_HSPA:
				break;
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				break;
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				break;
			case TelephonyManager.NETWORK_TYPE_IDEN:
				break;
			case TelephonyManager.NETWORK_TYPE_LTE:
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:
				break;
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				break;
			default:
				break;
		}
		
		List<NeighboringCellInfo> neighboringCellInfos = telephonyManager.getNeighboringCellInfo();
		
		//TODO
		System.out.println(neighboringCellInfos.size());
		
		GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
		
		int cellID = cellLocation.getCid();
		int lac = cellLocation.getLac();
	    
		String networkOperator = telephonyManager.getNetworkOperator();

    	System.out.println(networkOperator);
		
	    int mcc = Integer.parseInt(networkOperator.substring(0, 3));
	    int mnc = Integer.parseInt(networkOperator.substring(3));
	        
        System.out.println(mcc);
        System.out.println(mnc);
	    
		String networkOperatorName = telephonyManager.getNetworkOperatorName();
		
		System.out.println(networkOperatorName);
		
		String simOperatorName = telephonyManager.getSimOperatorName();
		
		System.out.println(simOperatorName);
		
		(new HttpRequestTask()).execute("http://www.opencellid.org/cell/get?mcc=" + mcc + "&mnc=" + mnc + "&cellid=" + cellID + "&lac=" + lac);
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.telephony_manager, menu);
		return true;
	}
	
	private class HttpRequestTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute()
		{
			
		}
		
		@Override
		protected String doInBackground(String... strings)
		{
			URL url = null;
			HttpURLConnection urlConnection = null;
			InputStream in = null;
			Scanner scanner = null;
			String result = null;
			
			try
			{
				url = new URL(strings[0]);
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				urlConnection = (HttpURLConnection) url.openConnection();
				
				in = new BufferedInputStream(urlConnection.getInputStream());
				
				scanner = new Scanner(in).useDelimiter("\\A");
				
				result = scanner.next();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
			    urlConnection.disconnect();
			    
				if(scanner != null)
				{
					scanner.close();
				}
				
				if(in != null)
				{
					try
					{
						in.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			
			return result;
		}
		
		@Override
		protected void onProgressUpdate(Void... values)
		{
			
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			System.out.println(result);
		}
	}
}