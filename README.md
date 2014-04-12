autocompletetextview
====================

Easiest way to add autocomplete component in your app.

If you don't specify any autocomplete url in XML, it will use Google places auto-complete URL. You'll need to specify Google API key in XML.

Sample:
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
    	  

You can also specify your own custom autocomplete URL. In that case you just need to tell:
1) Autocomplete URL (in XML)
2) Search key (in XML)
3) Response Parser (from Java code)

Sample example: Wiki auto-complete
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
