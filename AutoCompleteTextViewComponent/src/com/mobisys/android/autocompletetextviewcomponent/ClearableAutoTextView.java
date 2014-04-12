package com.mobisys.android.autocompletetextviewcomponent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;

import com.mobisys.android.autocompletetextviewcomponent.TextWatcherAdapter.TextWatcherListener;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

/**
 * To change clear icon, set
 * 
 * <pre>
 * android:drawableRight="@drawable/custom_icon"
 * </pre>
 */
public class ClearableAutoTextView extends AutoCompleteTextView implements OnTouchListener,
		OnFocusChangeListener, TextWatcherListener {

	private Context mContext;
	private SelectedLocationListener mListener;
	private String mAutocompleteUrl;
	public String mGooglePlacesApiKey;
	private boolean mAutoTextSelected=false;
	private Drawable xD;
	private Listener listener;
	private AutoCompleteResponseParserInterface mParser;
	private String mInputKey=null;
	
	public interface Listener {
		void didClearText();
	}
	
	public interface DisplayStringInterface {
		public String getDisplayString();
	}
	
	public interface AutoCompleteResponseParserInterface {
		 public ArrayList<DisplayStringInterface> 	parseAutoCompleteResponse(String response);
	}
	
	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	public ClearableAutoTextView(Context context) {
		super(context);
		mContext=context;
		init();
	}

	public ClearableAutoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClearableAutoTextView);
	    CharSequence url = a.getString(R.styleable.ClearableAutoTextView_url);
	    CharSequence google_places_api = a.getString(R.styleable.ClearableAutoTextView_google_places_api_key);
	    CharSequence input_key = a.getString(R.styleable.ClearableAutoTextView_input_key);
	    
	    if (url != null) mAutocompleteUrl=url.toString();
	    if (google_places_api != null) mGooglePlacesApiKey=google_places_api.toString();
	    if(input_key!=null) mInputKey=input_key.toString();
	    
	    if(mAutocompleteUrl==null || mAutocompleteUrl.length()==0){
	    	mAutocompleteUrl = GoogleAutoCompleteParser.getGooglePlacesUrl(mGooglePlacesApiKey);
	    	mParser = new GoogleAutoCompleteParser();
	    }
	    
		init();
	}

	public ClearableAutoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;
		init();
	}

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		this.l = l;
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener f) {
		this.f = f;
	}

	private OnTouchListener l;
	private OnFocusChangeListener f;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (getCompoundDrawables()[2] != null) {
			boolean tappedX = event.getX() > (getWidth() - getPaddingRight() - xD
					.getIntrinsicWidth());
			if (tappedX) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					setText("");
					if (listener != null) {
						listener.didClearText();
					}
				}
				return true;
			}
		}
		if (l != null) {
			return l.onTouch(v, event);
		}
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			setClearIconVisible(isNotEmpty(getText()));
		} else {
			setClearIconVisible(false);
		}
		if (f != null) {
			f.onFocusChange(v, hasFocus);
		}
	}

	
	@Override
	public void onTextChanged(AutoCompleteTextView view, String text) {
		if (isFocused()) {
			setClearIconVisible(isNotEmpty(text));
			if(!mAutoTextSelected){
				if(text.length()>0){
					loadSuggestions(text.toString());
				}
			}
			else mAutoTextSelected=false;
		}
	}

	private void loadSuggestions(final String s) {
		mHandler.removeMessages(0);
		Message msg=mHandler.obtainMessage(0);
		Bundle b=new Bundle();
		b.putString("query", s);
		msg.setData(b);
		mHandler.sendMessageDelayed(msg, 500);
	}
	
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){
				Bundle b = msg.getData();
				String query=b.getString("query");
				
				if(query.length()>0 && query!=null){
					Hashtable<String, String> ht=new Hashtable<String, String>();
			    	ht.put("query", query);
					GetResponseAsyncTask task=new GetResponseAsyncTask(mContext);
					task.execute(ht);	
				}
			}
		}
    };

    public class GetResponseAsyncTask extends AsyncTask<Hashtable<String, String>,Void, ArrayList<DisplayStringInterface>>{

		Context mContext;
	
		public GetResponseAsyncTask(Context mContext) {
			this.mContext=mContext;
		}

		@Override
		protected ArrayList<DisplayStringInterface> doInBackground(Hashtable<String, String>... params) {
			Hashtable<String, String> ht=params[0];
			final String myQuery=ht.get("query");
			String url = appendInput(myQuery);
			String response = HttpConnector.getResponse(url);
			return mParser.parseAutoCompleteResponse(response);
		}

		@Override
		protected void onPostExecute(ArrayList<DisplayStringInterface> result) {
			super.onPostExecute(result);
			if(result!=null && !result.isEmpty()){
				AdapterAutoComplete autoCompleteAdapter = new AdapterAutoComplete(mContext,R.layout.auto_complete_item,result);
				
				setAdapter(autoCompleteAdapter);
				autoCompleteAdapter.notifyDataSetChanged();
			}
		}
		
	}	
	
    private String appendInput(String query){
    	try {
    		StringBuilder sb = new StringBuilder(mAutocompleteUrl);
			sb.append("&input=" + URLEncoder.encode(query, "utf8"));
			if(mInputKey==null || mInputKey.length()==0)
				sb.append("&input=" + URLEncoder.encode(query, "utf8"));
			else
				sb.append("&"+mInputKey+"=" + URLEncoder.encode(query, "utf8"));
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
    
	public class AdapterAutoComplete extends ArrayAdapter<DisplayStringInterface> {
		ArrayList<DisplayStringInterface> items;
		boolean isOrigin;
		
	    public AdapterAutoComplete(Context context, int viewResourceId, ArrayList<DisplayStringInterface> items) {
	        super(context, viewResourceId, items);
	        this.items = items;
	    }

	    public View getView(final int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater inflate = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = inflate.inflate(R.layout.auto_complete_item, null);
	        }
	        ((TextView)v.findViewById(R.id.place_name)).setText(getItem(position).getDisplayString());
	        v.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mAutoTextSelected=true;
					setText(getItem(position).getDisplayString());
					if(mListener!=null) mListener.onSelectedLocation(getItem(position).getDisplayString());
					clearFocus();
					getLatLngFromAddress(getItem(position).getDisplayString());
				}
			});
	        return v;
	    }
	}
	
	private void getLatLngFromAddress(String address) {
		GetLatLngUtil.getLatLng(mContext, address, new GetLatLngUtil.GetLatLngResult() {
			
			@Override
			public void onLatLngReceive(double lat, double lng) {
				if(mListener!=null) mListener.onFetchLatLngForSelectedLoc(lat, lng);
			}
			
			@Override
			public void onError(String message) {
				AppUtil.showErrorDialog("Some Error Occured! Please try again.", mContext);
			}	
		});
	}
	
	public static boolean isNotEmpty(CharSequence str) {
		return !isEmpty(str);
	}

	public static boolean isEmpty(CharSequence str) {
		return str == null || str.length() == 0;
	}
	
	private void init() {
		xD = getCompoundDrawables()[2];
		if (xD == null) {
			xD = getResources().getDrawable(getDefaultClearIconId());
		}
		xD.setBounds(0, 0, xD.getIntrinsicWidth(), xD.getIntrinsicHeight());
		setClearIconVisible(false);
		super.setOnTouchListener(this);
		super.setOnFocusChangeListener(this);
		addTextChangedListener(new TextWatcherAdapter(this, this));
	}

	private int getDefaultClearIconId() {
		int id = getResources()
				.getIdentifier("ic_clear", "drawable", "android");
		if (id == 0) {
			id = android.R.drawable.presence_offline;
		}
		return id;
	}

	protected void setClearIconVisible(boolean visible) {
		Drawable x = visible ? xD : null;
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], x, getCompoundDrawables()[3]);
	}
	
	public void setSelectionListener(SelectedLocationListener listener){
		this.mListener = listener;
	}

	public void setParser(AutoCompleteResponseParserInterface autoCompleteParser) {
		this.mParser=autoCompleteParser;
	}
}
