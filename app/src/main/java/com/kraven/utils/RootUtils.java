package com.kraven.utils;

import android.annotation.SuppressLint;
import android.graphics.Color;


import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.PolylineOptions;

import com.kraven.R;
import com.kraven.application.KravenCustomer;
import com.kraven.core.AppExecutors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.core.content.ContextCompat;
import io.reactivex.Single;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Android Dev on 3/5/18.
 */
public class RootUtils {

    static  JSONArray jLegs = null;
    public static Single<PolylineOptions> getRoot(LatLng origin, LatLng dest) {

        @SuppressLint("ResourceType") Single<PolylineOptions> single =
                Single.create(emitter -> {
                    try {
                        String data = new OkHttpClient().newCall(new Request.Builder().get().url(getDirectionsUrl(origin, dest)).build()).execute().body().string();

                        JSONObject jObject;
                        List<List<HashMap<String, String>>> routes = null;
                        jObject = new JSONObject(data);
                        DirectionsJSONParser parser = new DirectionsJSONParser();
                        routes = parser.parse(jObject);
                        ArrayList<LatLng> points = null;
                        PolylineOptions lineOptions = null;

                        for (int i = 0; i < routes.size(); i++) {
                            points = new ArrayList<LatLng>();
                            lineOptions = new PolylineOptions();

                            List<HashMap<String, String>> path = routes.get(i);

                            for (int j = 0; j < path.size(); j++) {
                                HashMap<String, String> point = path.get(j);
                                double lat = Double.parseDouble(point.get("lat"));
                                double lng = Double.parseDouble(point.get("lng"));
                                LatLng position = new LatLng(lat, lng);
                                points.add(position);
                            }

                            lineOptions.addAll(points);
                            lineOptions.width(10);
                            lineOptions.color(ContextCompat.getColor(KravenCustomer.appContext, R.color.colorMapScreen));
                            lineOptions.geodesic(true);
                            if (!emitter.isDisposed())
                                emitter.onSuccess(lineOptions);
                        }
                    } catch (Exception e) {
                        if (!emitter.isDisposed())
                            emitter.onError(e);
                    }
                });
        single = single.subscribeOn(Schedulers.from(new AppExecutors().networkIO()))
                .observeOn(Schedulers.from(new AppExecutors().mainThread()));
        // Getting URL to the Google Directions API


        return single;
    }


    private static String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving"; //driving (default),walking,bicycling,transit

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + "key=" +
                KravenCustomer.appContext.getString(R.string.google_maps_broswer_key);

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private static class DirectionsJSONParser {

        /**
         * Receives a JSONObject and returns a list of lists containing latitude and longitude
         */
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) throws JSONException {

            List<List<HashMap<String, String>>> routes = new ArrayList<>();
            JSONArray jRoutes = null;
            JSONArray jSteps = null;


            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString((list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }


            return routes;
        }

        /**
         * Method to decode polyline points
         * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }

    public static JSONArray getDistance(){
        return jLegs;
    }

}