package com.mobisys.android.autocompletetextviewcomponent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak") 
public class ClearableAutoTextView extends AutoCompleteTextView implements OnTouchListener,
		OnFocusChangeListener, TextWatcher, OnKeyListener{

	private Context mContext;
	private SelectionListener mListener;
	private String mAutocompleteUrl;
	public String mGooglePlacesApiKey;
	private Drawable xD;
	private AutoCompleteResponseParser mParser;
	private String mInputKey=null;
	private OnTouchListener l;
	private OnFocusChangeListener f;

	public interface DisplayStringInterface {
		public String getDisplayString();
	}
	
	public interface AutoCompleteResponseParser {
		 public ArrayList<DisplayStringInterface> 	parseAutoCompleteResponse(String response);
	}
	
	public ClearableAutoTextView(Context context) {
		super(context);
		mContext=context;
		init();
	}

	public ClearableAutoTextView(Context context, AttributeSet attrs) throws Exception {
		super(context, attrs);
		mContext=context;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClearableAutoTextView);
	    CharSequence url = a.getString(R.styleable.ClearableAutoTextView_url);
	    CharSequence google_places_api = a.getString(R.styleable.ClearableAutoTextView_google_places_api_key);
	    CharSequence input_key = a.getString(R.styleable.ClearableAutoTextView_input_key);
	    a.recycle();
	    
	    if (url != null) mAutocompleteUrl=url.toString();
	    if (google_places_api != null) mGooglePlacesApiKey=google_places_api.toString();
	    if(input_key!=null) mInputKey=input_key.toString();
	    
	    if(mAutocompleteUrl==null || mAutocompleteUrl.length()==0){
	    	if(mGooglePlacesApiKey==null) throw new Exception("If autocomplete url is null then it uses Google places auto complete & so, you must specify google_places_api_key attribute in XML");
	    	mAutocompleteUrl = GoogleAutoCompleteParser.getGooglePlacesUrl(mGooglePlacesApiKey);
	    	mParser = new GoogleAutoCompleteParser();
	    }
	    else {
	    	if(input_key==null || input_key.length()==0) throw new Exception("You must specify input key to send to autocomplete url");
	    }
	    
		init();
	}

	public ClearableAutoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;
		init();
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
		super.setOnKeyListener(this);
		addTextChangedListener(this);
	}

	private int getDefaultClearIconId() {
		int id = getResources().getIdentifier("ic_clear", "drawable", "android");
		if (id == 0) {
			id = android.R.drawable.presence_offline;
		}
		return id;
	}

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		this.l = l;
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener f) {
		this.f = f;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (getCompoundDrawables()[2] != null) {
			boolean tappedX = event.getX() > (getWidth() - getPaddingRight() - xD
					.getIntrinsicWidth());
			if (tappedX) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					setText("");
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

	private void loadSuggestions(final String s) {
		mHandler.removeMessages(0);
		Message msg=mHandler.obtainMessage(0);
		Bundle b=new Bundle();
		b.putString("query", s);
		msg.setData(b);
		mHandler.sendMessageDelayed(msg, 300);
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){
				Bundle b = msg.getData();
				String query=b.getString("query");

				if(query.length()>0 && query!=null){
					getResponse(query);
				}
			}
		}
    };
    
    private Handler mUiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			@SuppressWarnings("unchecked")
			ArrayList<DisplayStringInterface> result = (ArrayList<ClearableAutoTextView.DisplayStringInterface>)msg.obj;
			if(result!=null && !result.isEmpty()){
				AdapterAutoComplete autoCompleteAdapter = new AdapterAutoComplete(mContext,R.layout.auto_complete_item,result);
				
				setAdapter(autoCompleteAdapter);
				autoCompleteAdapter.notifyDataSetChanged();
			}
		}
    };
    
    private void getResponse(final String query){
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				String url = appendInput(query);
				Log.d("ClearableAutoTextView", "Calling URL: "+url);
				String response = HttpConnector.getResponse(url);
				ArrayList<DisplayStringInterface> displayList = mParser.parseAutoCompleteResponse(response);
				Message msg = Message.obtain(mUiHandler);
				msg.obj = displayList;
				msg.sendToTarget();
			}
		 }).start();
    }
    
    private String appendInput(String query){
    	try {
    		StringBuilder sb = new StringBuilder(mAutocompleteUrl);
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
	    public AdapterAutoComplete(Context context, int viewResourceId, ArrayList<DisplayStringInterface> items) {
	        super(context, viewResourceId, items);
	    }

	    public View getView(final int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater inflate = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = inflate.inflate(R.layout.auto_complete_item, null);
	        }
	        
	        ((TextView)v.findViewById(R.id.item)).setText(getItem(position).getDisplayString());
	        v.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setText(getItem(position).getDisplayString());
					if(mListener!=null) mListener.onItemSelection(getItem(position));
					clearFocus();
					if(mAutocompleteUrl.contains(GoogleAutoCompleteParser.PLACES_API_BASE)) getLatLngFromAddress(getItem(position).getDisplayString());
				}
			});
	        return v;
	    }
	}
	
	private void getLatLngFromAddress(String address) {
		Log.d("ClearableAutoTextView", "Fetching location information: "+address);
		GetLatLngUtil.getLatLng(mContext, address, new GetLatLngUtil.GetLatLngResult() {
			
			@Override
			public void onLatLngReceive(double lat, double lng) {
				if(mListener!=null) mListener.onReceiveLocationInformation(lat, lng);
			}
			
			@Override
			public void onError(String message) {
				Toast.makeText(mContext, "Location information cannot be fetched..", Toast.LENGTH_SHORT).show();
			}	
		});
	}
	
	public static boolean isNotEmpty(CharSequence str) {
		return !isEmpty(str);
	}

	public static boolean isEmpty(CharSequence str) {
		return str == null || str.length() == 0;
	}
	
	protected void setClearIconVisible(boolean visible) {
		Drawable x = visible ? xD : null;
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], x, getCompoundDrawables()[3]);
	}
	
	public void setSelectionListener(SelectionListener listener){
		this.mListener = listener;
	}

	public void setParser(AutoCompleteResponseParser autoCompleteParser) {
		this.mParser=autoCompleteParser;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(isFocused()) setClearIconVisible(isNotEmpty(s.toString()));
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		String query=s.toString();
		
		Log.d("ClearableAutoTextView", "Query String: "+query);
		if(query.length()>0 && query!=null){
			loadSuggestions(query);
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		return false;
	}
}
