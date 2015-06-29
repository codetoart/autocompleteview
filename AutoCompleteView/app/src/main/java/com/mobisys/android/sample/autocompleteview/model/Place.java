package com.mobisys.android.sample.autocompleteview.model;

import com.mobisys.android.sample.autocompleteview.R;
import com.mobisys.android.autocompleteview.annotations.ViewId;

/**
 * Created by priyank on 6/24/15.
 */
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
