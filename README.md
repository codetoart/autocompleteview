autocompletetextview
====================

Easiest way to add autocomplete component in your app.

If you don't specify any autocomplete url in XML, it will use Google places auto-complete URL. You'll need to specify Google API key in XML.

Sample:
```xml
<com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView android:id="@+id/auto_text_2"
        android:layout_width="fill_parent"
        android:layout_height="40dp"
        android:layout_marginLeft="8dp"
        android:layout_marginTop="8dp"
        android:layout_marginRight="8dp"
        android:layout_below="@id/auto_text"
        android:paddingLeft="10dp"
        android:hint="Google places text"
       	android:singleLine="true"
       	android:textColor="@android:color/black"
        android:textSize="18sp"
        android:background="@android:color/white"
    	app:google_places_api_key="<Google API Key>"/>
```	  

You can also specify your own custom autocomplete URL. In that case you just need to tell:<br/>
1) Autocomplete URL (in XML)<br/>
2) Search key (in XML)<br/>
3) Response Parser (from Java code)

Sample example: Wiki auto-complete URL: http://en.wikipedia.org/w/api.php?action=opensearch&limit=8&namespace=0&format=json&search=android
```xml
<com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView android:id="@+id/auto_text"
        android:layout_width="fill_parent"
        android:layout_height="40dp"
        android:layout_marginLeft="8dp"
        android:layout_marginTop="8dp"
        android:layout_marginRight="8dp"
        android:paddingLeft="10dp"
        android:hint="wiki text"
       	android:singleLine="true"
       	android:textColor="@android:color/black"
        android:textSize="18sp"
        android:background="@android:color/white"
    	app:url="http://en.wikipedia.org/w/api.php?action=opensearch&amp;limit=8&amp;namespace=0&amp;format=json"
    	app:input_key="search"/>
```

Response from Autocomplete URL should be parsed into data model which implements ```DisplayStringInterface```
```java
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
```

```java
		mAutoText=(com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView)findViewById(R.id.auto_text);
		//Sets the selection listener
		mAutoText.setSelectionListener(this);
		//Sets the parser
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
```

When user selects the item, you'll receive the selected item in ```SelectionListener```. If you are using for Google Places URL, it will also get Lat Lng information of selected place.
