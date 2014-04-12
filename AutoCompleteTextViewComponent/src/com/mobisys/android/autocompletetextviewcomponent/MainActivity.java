package com.mobisys.android.autocompletetextviewcomponent;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView.AutoCompleteResponseParser;
import com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView.DisplayStringInterface;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity implements SelectionListener{
	
	private com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView mAutoText;
	public class WikiModel implements DisplayStringInterface{
		public String item;

		public WikiModel(String item){
			this.item = item;
		}
		
		@Override
		public String getDisplayString() {
			return item;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Customized auto complete URL. For demo we have used Wiki suggestions
		mAutoText=(com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView)findViewById(R.id.auto_text);
		mAutoText.setSelectionListener(this);
		mAutoText.setParser(new AutoCompleteResponseParser() {
			
			@Override
			public ArrayList<DisplayStringInterface> parseAutoCompleteResponse(
					String response) {
				ArrayList<DisplayStringInterface> models = new ArrayList<DisplayStringInterface>();
				try {
					JSONArray jsonArray = new JSONArray(response);
					JSONArray array = jsonArray.optJSONArray(1);
					if(array!=null){
						for(int i=0;i<array.length();i++){
							models.add(new WikiModel(array.getString(i)));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				return models;
			}
		});
		
		//For Google places suggestions, we just need to provide Google API key in XML
		((com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView)findViewById(R.id.auto_text_2)).setSelectionListener(this);
	}

	@Override
	public void onItemSelection(String selectedItem) {
		Toast.makeText(MainActivity.this, "Selected Location: "+selectedItem, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onReceiveLocationInformation(double lat, double lng) {
		Toast.makeText(MainActivity.this, "lat lng: "+lat+" "+lng, Toast.LENGTH_SHORT).show();
	}

}
