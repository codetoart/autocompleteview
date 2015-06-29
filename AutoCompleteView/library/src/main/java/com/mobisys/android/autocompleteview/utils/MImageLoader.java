package com.mobisys.android.autocompleteview.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Mahavir on 04/11/2014.
 */
public class MImageLoader {
    private static final String UNIVERSAL_IMAGE_LOADER = "universal-image-loader";
    private static final String PICASSO_IMAGE_LOADER = "picasso";

    public static final String IMAGE_LOADER_TYPE = UNIVERSAL_IMAGE_LOADER;

    public static ImageLoader mImageLoader;

    public static void initImageLoader(Context context){
        if(mImageLoader==null) {
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
    }

    public static void destroyImageLoader(){
        if(mImageLoader!=null){
            mImageLoader.destroy();
            mImageLoader=null;
        }
    }

    public static void stopImageLoader(){
        if(mImageLoader!=null){
            mImageLoader.stop();
            mImageLoader=null;
        }
    }
    public static DisplayImageOptions defaulDisplayImageOptions(int place_holder_res){
        return
                new DisplayImageOptions.Builder()
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .showImageForEmptyUri(place_holder_res)
                        .showImageOnFail(place_holder_res)
                        .resetViewBeforeLoading(true)
                        .delayBeforeLoading(200)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .displayer(new MyFadeInBitmapDisplayer(300))
                        .build();
    }

    public static DisplayImageOptions defaulDisplayImageOptions(Drawable drawable){
        return
                new DisplayImageOptions.Builder()
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .showImageForEmptyUri(drawable)
                        .showImageOnFail(drawable)
                        .showImageOnLoading(drawable)
                        .resetViewBeforeLoading(true)
                        .delayBeforeLoading(200)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .displayer(new MyFadeInBitmapDisplayer(300))
                        .build();
    }

    public static void displayImage(Context context, String url, ImageView imageView, int place_holder_res){
        if(UNIVERSAL_IMAGE_LOADER.equals(IMAGE_LOADER_TYPE)){
            initImageLoader(context);
            mImageLoader.displayImage(url, imageView, defaulDisplayImageOptions(place_holder_res));
        }
        else if(PICASSO_IMAGE_LOADER.equals(IMAGE_LOADER_TYPE)){
        }
    }

    public static void displayImage(Context context, String url, ImageView imageView, int place_holder_res, ImageLoadingListener listener){
        if(UNIVERSAL_IMAGE_LOADER.equals(IMAGE_LOADER_TYPE)){
            initImageLoader(context);
            mImageLoader.displayImage(url, imageView, defaulDisplayImageOptions(place_holder_res), listener);
        }
    }

    public static void displayImage(Context context, String url, ImageView imageView, Drawable drawable){
        if(UNIVERSAL_IMAGE_LOADER.equals(IMAGE_LOADER_TYPE)){
            initImageLoader(context);
            mImageLoader.displayImage(url, imageView, defaulDisplayImageOptions(drawable));
        }
    }

    public static class MyFadeInBitmapDisplayer extends FadeInBitmapDisplayer {

        public MyFadeInBitmapDisplayer(int durationMillis) {
            super(durationMillis);
        }
    }
}
