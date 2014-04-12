package com.mobisys.android.autocompletetextviewcomponent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity implements SelectionListener{
	
	private com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView mAutoText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		mAutoText=(com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView)findViewById(R.id.auto_text);
		mAutoText.setSelectionListener(this);
	}

	@Override
	public void onItemSelection(String selectedLocation) {
		Toast.makeText(MainActivity.this, "Selected Location: "+selectedLocation, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onReceiveLocationInformation(double lat, double lng) {
		Toast.makeText(MainActivity.this, "lat lng: "+lat+" "+lng, Toast.LENGTH_SHORT).show();
	}

}
