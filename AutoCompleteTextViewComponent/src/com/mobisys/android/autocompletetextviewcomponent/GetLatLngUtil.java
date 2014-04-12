package com.mobisys.android.autocompletetextviewcomponent;

import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class GetLatLngUtil {

	public static interface GetLatLngResult {
		public void onLatLngReceive(double lat, double lng);
		public void onError(String message);
	}
	
	public static void getLatLng(Context context, String address, GetLatLngResult resultListener){
		GetLatLngAsyncTask latLngAsyncTask = new GetLatLngAsyncTask(context, resultListener);
		latLngAsyncTask.execute(address);
	}
	
	public static class GetLatLngAsyncTask extends AsyncTask<String,Void, GeoPoint>{

		private Context mContext;
		private String mAddress;
		private GetLatLngResult mResultListener;
		
		public GetLatLngAsyncTask(Context mContext, GetLatLngResult resultListener) {
			this.mContext=mContext;
			this.mResultListener = resultListener;
		}

		@Override
		protected GeoPoint doInBackground(String... params) {
			mAddress=params[0];
			return getFromLocation(mAddress);	
		}

		@Override
		protected void onPostExecute(GeoPoint result) {
			super.onPostExecute(result);
			if(mResultListener!=null){
				if(result!=null) mResultListener.onLatLngReceive(result.getLatitudeE6()/1E6, result.getLongitudeE6()/1E6);
				else mResultListener.onError(null);
			}
		}
		
		private GeoPoint getFromLocation(String address){
			Geocoder geoCoder = new Geocoder(mContext);
	        GeoPoint p =null;
	        try {
	            List<Address> addresses = geoCoder.getFromLocationName(address , 1);
	            if (addresses.size() > 0){            
	                p= new GeoPoint(
	                (int) (addresses.get(0).getLatitude() * 1E6), 
	                (int) (addresses.get(0).getLongitude() * 1E6));

	             }
	        } catch(Exception ee) {
	        	Log.e("", ee.getMessage());
	        }
			return p;
	    }
	}

}
