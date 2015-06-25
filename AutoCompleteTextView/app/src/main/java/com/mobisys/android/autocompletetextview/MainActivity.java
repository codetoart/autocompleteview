package com.mobisys.android.autocompletetextview;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobisys.android.autocompletetextview.model.Place;
import com.mobisys.android.autocompletetextview.model.WikiItem;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity{
	
	private AutoCompleteView mWikiAutoComplete;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Customized auto complete URL. For demo we have used Wiki suggestions
		/*mWikiAutoComplete=(com.mobisys.android.autocompletetextview.AutoCompleteView)findViewById(R.id.auto_text);
		mWikiAutoComplete.setLoadingIndicator(findViewById(R.id.loading_indicator));
		mWikiAutoComplete.setParser(new AutoCompleteView.AutoCompleteResponseParser() {
			
			@Override
			public ArrayList<? extends Object> parseAutoCompleteResponse(
					String response) {
				ArrayList<WikiItem> models = new ArrayList<WikiItem>();
				try {
					JSONArray jsonArray = new JSONArray(response);
					JSONArray array = jsonArray.optJSONArray(1);
					if(array!=null){
						for(int i=0;i<array.length();i++){
							models.add(new WikiItem(array.getString(i)));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				return models;
			}
		});*/

		((com.mobisys.android.autocompletetextview.AutoCompleteView)findViewById(R.id.auto_text_2)).setParser(new AutoCompleteView.AutoCompleteResponseParser() {
			@Override
			public ArrayList<? extends Object> parseAutoCompleteResponse(String response) {
				ArrayList<Place> places=null;
				try {
					JSONObject jsonObj = new JSONObject(response);
					final JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

					places=new ArrayList<Place>();
					for(int i=0;i<predsJsonArray.length();i++){
						String placeName = predsJsonArray.getJSONObject(i).getString("description");
						String placeReference = predsJsonArray.getJSONObject(i).getString("reference");

						Place place = new Place();
						place.setName(placeName);
						place.setPhotoReference(placeReference);
						places.add(place);
					}
				} catch (JSONException e) {
					Log.e("AppUtil", "Cannot process JSON results", e);
				}

				return places;
			}
		});
		((com.mobisys.android.autocompletetextview.AutoCompleteView)findViewById(R.id.auto_text_2)).setSelectionListener(new AutoCompleteView.AutoCompleteItemSelectionListener() {
			@Override
			public void onItemSelection(Object obj) {
				Place place = (Place)obj;
				((com.mobisys.android.autocompletetextview.AutoCompleteView)findViewById(R.id.auto_text_2)).setText(place.getName());
				((com.mobisys.android.autocompletetextview.AutoCompleteView)findViewById(R.id.auto_text_2)).clearFocus();
			}
		});
		((com.mobisys.android.autocompletetextview.AutoCompleteView)findViewById(R.id.auto_text_2)).setLoadingIndicator(findViewById(R.id.loading_indicator));
	}

	public void onPlaceClick(View v, Object object){
		Place place = (Place)object;
		Log.d("MainActivity", place.toString());
	}
}
