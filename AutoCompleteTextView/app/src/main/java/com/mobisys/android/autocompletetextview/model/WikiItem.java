package com.mobisys.android.autocompletetextview.model;

import com.mobisys.android.autocompletetextview.R;
import com.mobisys.android.autocompletetextview.annotations.ViewId;

/**
 * Created by priyank on 6/25/15.
 */
public class WikiItem {
    private String item;

    public WikiItem(String item){
        this.item = item;
    }

    @ViewId(id=R.id.item)
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
