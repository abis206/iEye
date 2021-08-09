package com.example.saucey;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class GetNearbyFacilitiesData extends AsyncTask<Object,String,String> {
    public Nearby delegate = null;
    String googlePlacesData;
    GoogleMap mMap;
    String url;


    @Override
    protected String doInBackground(Object... objects) {
     //  mMap = (GoogleMap)objects[0];
       url = (String)objects[0];

       DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearbyPlacesList = null;
        DataParser parser = new DataParser();
        nearbyPlacesList = parser.parse(s);
      //  Log.e(TAG, "onPostExecute: "+nearbyPlacesList);
       if(nearbyPlacesList == null)Log.e(TAG, "fields are null");
        if(nearbyPlacesList != null){
           // Log.e(TAG, String.valueOf(nearbyPlacesList.size()));
            delegate.sendData(nearbyPlacesList);
        }
    }
}
