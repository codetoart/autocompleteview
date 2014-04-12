package com.mobisys.android.autocompletetextviewcomponent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView.AutoCompleteResponseParserInterface;
import com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView.DisplayStringInterface;

public class GoogleAutoCompleteParser implements AutoCompleteResponseParserInterface{
	
	private static SimpleDateFormat PARAM_SDF = new SimpleDateFormat("dd MMM''yy", Locale.US);
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	//private static final String GOOGLE_PLAECES_API_KEY = "AIzaSyABIEhDW3EisetMw1B6wxm7qsabAF5ENRY";
	public static final String GOOGLE_GEOCODER = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
	
	public class ResultList implements DisplayStringInterface{
		
		String str;
	
		public String getStr() {
			return str;
		}
		
		public void setStr(String str) {
			this.str = str;
		}

		@Override
		public String getDisplayString() {
			return str;
		}
		
	}
	
	@Override
	public ArrayList<DisplayStringInterface> parseAutoCompleteResponse(String jsonResults) {
		ArrayList<DisplayStringInterface> dsi=null;
		try {
        // Create a JSON object hierarchy from the results
    	Log.d("Json", "result: "+jsonResults.toString());
        JSONObject jsonObj = new JSONObject(jsonResults.toString());
        final JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

        dsi=new ArrayList<DisplayStringInterface>();
        for(int i=0;i<predsJsonArray.length();i++){
        	final int j=i;
        	DisplayStringInterface ds=new DisplayStringInterface() {
				
				@Override
				public String getDisplayString() {
					
					try {
						return predsJsonArray.getJSONObject(j).getString("description").toString();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
			};
			dsi.add(ds);
        }
    	} catch (JSONException e) {
        Log.e("AppUtil", "Cannot process JSON results", e);
    	}
		
		return dsi;
	}
	
	public static String getGooglePlacesUrl(String apiKey){
        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
        sb.append("?sensor=false&key=" + apiKey);

/*        try {
			//sb.append("&input=" + URLEncoder.encode(input, "utf8"));
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
*/        
        return sb.toString();
	}
}
