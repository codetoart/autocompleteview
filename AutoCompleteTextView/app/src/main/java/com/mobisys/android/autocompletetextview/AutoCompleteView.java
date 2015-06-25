package com.mobisys.android.autocompletetextview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.mobisys.android.autocompletetextview.exceptions.NoModelDefinedException;

import java.util.ArrayList;

/**
 * Created by priyank on 6/24/15.
 */
public class AutoCompleteView extends AutoCompleteTextView {
    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;
    private int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;
    private String mAutocompleteUrl, mModelClassName;
    private int mLayoutId;
    private AutoCompleteResponseParser mParser;
    private RequestDispatcher mRequestDispatcher;
    private AutoCompleteAdapter mAdapter;
    private AutoCompleteItemSelectionListener mListener;
    private View mLoadingIndicator;

    public interface AutoCompleteResponseParser {
        public ArrayList<? extends Object> parseAutoCompleteResponse(String response);
    }

    public interface AutoCompleteItemSelectionListener {
        public void onItemSelection(Object obj);
    }

    public interface RequestDispatcher {
        /*Important Note: getResponse() is called in worker thread.*/
        public String getResponse();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AutoCompleteView.super.performFiltering((CharSequence) msg.obj, msg.arg1);
        }
    };

    public AutoCompleteView(Context context) {
        super(context);
        setAdapter();
    }

    public AutoCompleteView(Context context, AttributeSet attrs) throws NoModelDefinedException {
        super(context, attrs);
        initAttrs(context, attrs);
        setAdapter();
    }

    public AutoCompleteView(Context context, AttributeSet attrs, int defStyleAttr) throws NoModelDefinedException {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        setAdapter();
    }

    @TargetApi(21)
    public AutoCompleteView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) throws NoModelDefinedException {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs);
        setAdapter();
    }

    public void setSelectionListener(AutoCompleteItemSelectionListener listener){
        this.mListener = listener;
        if(mAdapter!=null) mAdapter.setItemSelectionListener(listener);
    }

    public void setParser(AutoCompleteResponseParser parser){
        this.mParser = parser;
        if (mAdapter!=null) mAdapter.setParser(parser);
    }

    public void setRequestDispatcher(AutoCompleteView.RequestDispatcher requestDispatcher){
        this.mRequestDispatcher = requestDispatcher;
        if (mAdapter!=null) mAdapter.setRequestDispatcher(requestDispatcher);
    }

    private void initAttrs(Context context, AttributeSet attrs) throws NoModelDefinedException {
        if (attrs!=null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoCompleteView);
            mAutocompleteUrl = a.getString(R.styleable.AutoCompleteView_autocompleteUrl);
            mModelClassName = a.getString(R.styleable.AutoCompleteView_modelClass);
            mLayoutId = a.getResourceId(R.styleable.AutoCompleteView_rowLayout, android.R.layout.simple_dropdown_item_1line);
        }

        if (mModelClassName == null) {
            throw new NoModelDefinedException();
        }
    }

    public void setLoadingIndicator(View loadingIndicator){
        this.mLoadingIndicator = loadingIndicator;
    }

    public void setAutoCompleteDelay(int autoCompleteDelay) {
        mAutoCompleteDelay = autoCompleteDelay;
    }

    private void setAdapter(){
        mAdapter = new AutoCompleteAdapter(getContext(), mModelClassName,  mAutocompleteUrl, mParser, mLayoutId);
        setAdapter(mAdapter);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if (mLoadingIndicator!=null) mLoadingIndicator.setVisibility(View.VISIBLE);
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text), mAutoCompleteDelay);
    }

    @Override
    public void onFilterComplete(int count) {
        if (mLoadingIndicator!=null) mLoadingIndicator.setVisibility(View.GONE);
        super.onFilterComplete(count);
    }
}
