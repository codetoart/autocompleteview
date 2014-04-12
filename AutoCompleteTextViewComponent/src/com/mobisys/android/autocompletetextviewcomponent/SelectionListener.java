package com.mobisys.android.autocompletetextviewcomponent;

public interface SelectionListener {
	/*
	 * Called when user selects an item from autocomplete view
	 */
	public void onItemSelection(String selectedItem);
	
	/*
	 * Called only in case of Google Places API (autocomplete_url = null)
	 */
	public void onReceiveLocationInformation(double lat, double lng);
}
