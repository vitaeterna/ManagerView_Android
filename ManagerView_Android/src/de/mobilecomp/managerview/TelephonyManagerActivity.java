package de.mobilecomp.managerview;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.example.managerview_android.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TelephonyManagerActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_telephony_manager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		String content = "";

		int dataState = telephonyManager.getDataState();

		switch (dataState) {
		case TelephonyManager.DATA_CONNECTED:
			content = "Connected";
			break;
		case TelephonyManager.DATA_CONNECTING:
			content = "Connecting";
			break;
		case TelephonyManager.DATA_DISCONNECTED:
			content = "Disconnected";
			break;
		case TelephonyManager.DATA_SUSPENDED:
			content = "Suspended";
			break;
		default:
			content = " - ";
			break;
		}

		((TextView) findViewById(R.id.dataState)).setText(content);

		int phoneType = telephonyManager.getPhoneType();

		switch (phoneType) {
		case TelephonyManager.PHONE_TYPE_CDMA:
			content = "CDMA";
			break;
		case TelephonyManager.PHONE_TYPE_GSM:
			content = "GSM";
			break;
		case TelephonyManager.PHONE_TYPE_SIP:
			content = "SIP";
			break;
		case TelephonyManager.PHONE_TYPE_NONE:
			content = "None";
			break;
		default:
			content = " - ";
			break;
		}

		((TextView) findViewById(R.id.phoneType)).setText(content);

		int networkType = telephonyManager.getNetworkType();

		switch (networkType) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			content = "1xRTT";
			break;
		case TelephonyManager.NETWORK_TYPE_CDMA:
			content = "CDMA";
			break;
		case TelephonyManager.NETWORK_TYPE_EDGE:
			content = "EDGE";
			break;
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			content = "EHRPD";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			content = "EVDO 0";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			content = "EVDO A";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			content = "EVDO B";
			break;
		case TelephonyManager.NETWORK_TYPE_GPRS:
			content = "GPRS";
			break;
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			content = "HSDPA";
			break;
		case TelephonyManager.NETWORK_TYPE_HSPA:
			content = "HSPA";
			break;
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			content = "HSPAP";
			break;
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			content = "HSUPA";
			break;
		case TelephonyManager.NETWORK_TYPE_IDEN:
			content = "IDEN";
			break;
		case TelephonyManager.NETWORK_TYPE_LTE:
			content = "LTE";
			break;
		case TelephonyManager.NETWORK_TYPE_UMTS:
			content = "UMTS";
			break;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			content = "Unknown";
			break;
		default:
			content = " - ";
			break;
		}

		((TextView) findViewById(R.id.networkType)).setText(content);

		List<NeighboringCellInfo> neighboringCellInfos = telephonyManager.getNeighboringCellInfo();		

		if (!neighboringCellInfos.isEmpty()) {
			
		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.table_template, null);
		
			for (int i = 0; i < neighboringCellInfos.size(); i++) {

				((TextView) view.findViewById(R.id.cellID)).setText(neighboringCellInfos.get(i).getCid());
				((TextView) view.findViewById(R.id.lac)).setText(neighboringCellInfos.get(i).getLac());
			
				View insertPoint = findViewById(R.id.content);
				((ViewGroup) insertPoint).addView(view, i + 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			}
		}
		GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager
				.getCellLocation();

		int cellID = cellLocation.getCid();
		((TextView) findViewById(R.id.cellID)).setText(String.valueOf(cellID));

		int lac = cellLocation.getLac();
		((TextView) findViewById(R.id.lac)).setText(String.valueOf(lac));

		String networkOperator = telephonyManager.getNetworkOperator();

		int mcc = Integer.parseInt(networkOperator.substring(0, 3));
		((TextView) findViewById(R.id.mcc)).setText(String.valueOf(mcc));
		int mnc = Integer.parseInt(networkOperator.substring(3));
		((TextView) findViewById(R.id.mnc)).setText(String.valueOf(mnc));

		String networkOperatorName = telephonyManager.getNetworkOperatorName();
		((TextView) findViewById(R.id.networkOperatorName))
				.setText(networkOperatorName);

		String simOperatorName = telephonyManager.getSimOperatorName();
		((TextView) findViewById(R.id.simOperatorName))
				.setText(simOperatorName);

		(new HttpRequestTask())
				.execute("http://www.opencellid.org/cell/get?mcc=" + mcc
						+ "&mnc=" + mnc + "&cellid=" + cellID + "&lac=" + lac);

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.telephony_manager, menu);
		return true;
	}

	private class HttpRequestTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... strings) {
			URL url = null;
			HttpURLConnection urlConnection = null;
			InputStream in = null;
			Scanner scanner = null;
			String result = null;

			try {
				url = new URL(strings[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			try {
				urlConnection = (HttpURLConnection) url.openConnection();

				in = new BufferedInputStream(urlConnection.getInputStream());

				scanner = new Scanner(in).useDelimiter("\\A");

				result = scanner.next();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				urlConnection.disconnect();

				if (scanner != null) {
					scanner.close();
				}

				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return result;
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}

		@Override
		protected void onPostExecute(String result) {
			SAXBuilder builder = new SAXBuilder();
			Reader in = new StringReader(result);
			Element cell = null;
			try {
				cell = builder.build(in).getRootElement().getChild("cell");
				((TextView) findViewById(R.id.longitude)).setText(String
						.valueOf(cell.getAttribute("lon").getValue()));
				((TextView) findViewById(R.id.latitude)).setText(String
						.valueOf(cell.getAttribute("lat").getValue()));
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}