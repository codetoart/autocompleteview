autocompletetextview
====================

Easiest way to add autocomplete view in your app.

Sample Google Place screenshot
![alt tag](https://raw.githubusercontent.com/mobisystech/autocompletetextview/master/google_place.png)

Sample:
```xml
<com.mobisys.android.autocompletetextview.AutoCompleteView
        android:id="@+id/auto_text_2"
        android:layout_width="match_parent"
        android:layout_height="40dp"
        android:layout_marginLeft="8dp"
        android:layout_marginTop="8dp"
        android:layout_marginRight="8dp"
        android:paddingLeft="10dp" android:paddingRight="32dp"
        android:hint="Google places text"
       	android:singleLine="true"
       	android:textColor="@android:color/black"
        android:textSize="18sp"
        android:background="@android:color/white"
    	app:autocompleteUrl="https://maps.googleapis.com/maps/api/place/autocomplete/json?sensor=false&amp;key=AIzaSyDhFGUWlyd0KsjPQ59ATr-yL0bQKujHmeg&amp;input="
        app:row_layout="@layout/row_place"
        app:modelClass="com.mobisys.android.autocompletetextview.model.Place"/>
```	  

Sample XML have following attributes:<br/>
1) Autocomplete URL ```autocompleteUrl```:<br/> Not mandatory. If not specified, you should set ``` RequestInterceptor``` <br/>
2) Model Class ```modelClass```:<br/> Model class (with package name) where response from server will be parsed. It is <b>MANDATORY</b><br/>
3) Row Layout ```rowLayout```:<br/> Row layout of auto-complete view. Not mandatory. Default used is ``` android.R.layout.simple_dropdown_item_1line```

Bind getter methods of model with row view through ```@ViewId``` annotation.

XML
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="wrap_content"
    android:padding="10dp">
    <ImageView android:id="@+id/image"
        android:layout_width="50dp"
        android:layout_height="50dp"/>
    <TextView android:id="@+id/name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_toRightOf="@id/image"
        android:layout_marginLeft="8dp"
        android:layout_centerVertical="true"
        android:textSize="16sp"
        android:textColor="@android:color/black"
        android:text="Hello"/>
</RelativeLayout>
```
Corresponding Model

```
public class Place {
    private static final String PLACE_IMAGE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=%s&sensor=false&key=AIzaSyDhFGUWlyd0KsjPQ59ATr-yL0bQKujHmeg";
    private String name;
    private String photoReference;

    @ViewId(id=R.id.name)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    @ViewId(id=R.id.image)
    public String getImageUrl() {
        String url = String.format(PLACE_IMAGE_URL, photoReference);
        return url;
    }
}
```


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

SelectionListener
```Java
public interface SelectionListener {
	/*
	 * Called when user selects an item from autocomplete view
	 */
	public void onItemSelection(DisplayStringInterface selectedItem);
	
	/*
	 * Called only in case of Google Places API (autocomplete_url = null)
	 */
	public void onReceiveLocationInformation(double lat, double lng);
}
```
