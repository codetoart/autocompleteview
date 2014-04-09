package com.mobisys.android.autocompletetextviewcomponent;

public interface SelectedLocationListener {
	public void onSelectedLocation(String selectedLocation);
	public void onFetchLatLngForSelectedLoc(double lat, double lng);
}
