package com.desarrollosmoyan.movilyaDriver.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.desarrollosmoyan.movilyaDriver.DriverApplication;
import com.desarrollosmoyan.movilyaDriver.Helper.SharedHelper;
import com.desarrollosmoyan.movilyaDriver.Helper.URLHelper;
import com.desarrollosmoyan.movilyaDriver.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

import static com.desarrollosmoyan.movilyaDriver.DriverApplication.trimMessage;

public class taximeter extends Fragment {
    private static DecimalFormat df = new DecimalFormat("0.000");
    private double lastlat = 0;
    private double lastlong = 0;
    private double lastdistance = 0;
    private Button startnow, ride_completed;
    private ImageView backArrow;
    private TextView distancetext, meter_base, meter_distance, meter_total;
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    private double latitude;
    private double longitude;
    private GifImageView mainimg;
    private String getdist;
    private boolean stop = false;
    public taximeter() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taximeter, container, false);
        startnow = view.findViewById(R.id.start_now);
        backArrow = view.findViewById(R.id.backArrow);
        mainimg = view.findViewById(R.id.mainimg);
        meter_base = view.findViewById(R.id.meter_base);
        meter_distance = view.findViewById(R.id.meter_price);
        meter_total = view.findViewById(R.id.meter_total);
        distancetext = view.findViewById(R.id.distancetext);
        chronometer = view.findViewById(R.id.chronometer);
        ride_completed = view.findViewById(R.id.ride_completed);
        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new setting();

                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            }
        });
        ride_completed.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                meter_base.setText(SharedHelper.getKey(getActivity(), "currency")+"0.00");
                meter_distance.setText(SharedHelper.getKey(getActivity(), "currency")+"0.00");
                meter_total.setText(SharedHelper.getKey(getActivity(), "currency")+"0.00");
            }
        });
        startnow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (!running) {
                    stop = false;
                    mainimg.setImageResource(R.drawable.taximeter_started);
                    startnow.setText("Stop Now");
                    getdistance();
                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    chronometer.start();
                    running = true;
                }else{
                    running = false;
                    stop = true;
                    mainimg.setImageResource(R.drawable.taximeter_startnow);
                    startnow.setText("Start Now");
                    distancetext.setText("0.00 KM");
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.stop();
                    pauseOffset = 0;
                }
            }
        });
        getProfile();
        return view;
    }
    @SuppressLint("MissingPermission")
    private void getdistance() {
         getdist = "0.00";
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationProvider low=
                lm.getProvider(lm.getBestProvider(createCoarseCriteria(), true));
        LocationProvider high=
                lm.getProvider(lm.getBestProvider(createFineCriteria(), true));
        lm.requestLocationUpdates(low.getName(), 1000, 1f, new LocationListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(Location location) {
                if(!stop){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    if(lastlat != 0){
                        getdist =  String.valueOf(  df.format(lastdistance + distance(latitude, longitude,  lastlat, lastlong)*1.609344));
                    }

                    lastlat = latitude;
                    lastlong = longitude;
                    lastdistance = Double.parseDouble(getdist);
                    long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                    Log.v("Lat: "+ latitude, "Long: "+longitude);
                    Log.v("GetDistance", "Dis: "+getdist);
                    distancetext.setText(getdist+" KM");
                    meter_distance.setText(SharedHelper.getKey(getActivity(), "currency")+df.format( Double.valueOf(SharedHelper.getKey(getActivity(), "price")) * lastdistance));
                    meter_total.setText(SharedHelper.getKey(getActivity(), "currency")+df.format( (Double.valueOf(SharedHelper.getKey(getActivity(), "price")) * lastdistance + Double.valueOf(SharedHelper.getKey(getActivity(), "fixed")))));
                }else{
                    distancetext.setText("0.00 KM");
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
    }
//    private void getdistance() {
//        ha.postDelayed(new Runnable() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void run() {
//                String getdist = "0.00";
//                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//                Location location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//                if(lastlat != 0){
//                    getdist =  String.valueOf(  df.format(lastdistance + distance(latitude, longitude,  lastlat, lastlong)*1.609344));
//                }
//
//                LocationProvider high=
//                        lm.getProvider(lm.getBestProvider(createFineCriteria(), true));
//                lm.requestLocationUpdates(high.getName(), 0, 0f, new LocationListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//
//                    }
//
//                    @Override
//                    public void onStatusChanged(String s, int i, Bundle bundle) {
//
//                    }
//
//                    @Override
//                    public void onProviderEnabled(String s) {
//
//                    }
//
//                    @Override
//                    public void onProviderDisabled(String s) {
//
//                    }
//                });
//                lastlat = latitude;
//                lastlong = longitude;
//                lastdistance = Double.parseDouble(getdist);
//                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
//                String Spee = String.valueOf(df.format(Double.valueOf(getdist) / elapsedMillis * 1000));
//                Log.v("Lat: "+ latitude, "Long: "+longitude);
//                Log.v("GetDistance", "Dis: "+getdist);
//                Log.v("GetSpeed", "Dis: "+ Spee);
//                latlong.setText("Lat: "+ latitude+"   "+ "Long: "+longitude+"\n"+"Lat: "+ gps.getLatitude()+"   "+ "Long: "+gps.getLongitude());
//                distancetext.setText(getdist+" KM");
//                speed.setText(Spee + " KM/Hour");
//                if (!stop) {
//                    ha.postDelayed(this, 3000);
//                }else{
//                    distancetext.setText("0.00 KM");
//                }
//
//            }
//        }, 3000);
//
//
//    }

    public static Criteria createCoarseCriteria() {

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        return c;

    }


    public static Criteria createFineCriteria() {

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        return c;

    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    public void getProfile() {


            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.USER_PROFILE_API, object, new Response.Listener<JSONObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(JSONObject response) {
                    if (response.optJSONObject("service") != null) {
                        try {
                            JSONObject service = response.optJSONObject("service");
                            if (service.optJSONObject("service_type") != null) {
                                JSONObject serviceType = service.optJSONObject("service_type");
                                SharedHelper.putKey(getActivity(), "fixed", serviceType.optString("fixed"));
                                SharedHelper.putKey(getActivity(), "price", serviceType.optString("price"));
                                meter_base.setText(SharedHelper.getKey(getActivity(), "currency")+ SharedHelper.getKey(getActivity(), "fixed"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.getString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                /*refreshAccessToken();*/
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } else if (response.statusCode == 503) {
                                displayMessage(getString(R.string.server_down));
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }

                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            getProfile();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(getActivity(), "access_token"));
                    return headers;
                }
            };

            DriverApplication.getInstance().addToRequestQueue(jsonObjectRequest);


    }
    public void displayMessage(String toastString){

    }
}
