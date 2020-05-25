package com.example.mywebapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeFragment extends Fragment
{
    LocationManager locationManager;
    Location location;
    String provider;

    View v;
    private RecyclerView myrecyclerview;
    private List<Newscard> listcards;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    Boolean flag_data;
    Boolean flag_weather;

    private RequestQueue MyRequestQueue;
    private String urlnews = "https://newsapp-backend-99.appspot.com/home/0";
    private String urlweather = "https://api.openweathermap.org/data/2.5/weather?units=metric&appid=168110dc762cf4d01859299bd50a76f8&q=";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_fragment, container, false);
        myrecyclerview = (RecyclerView) v.findViewById(R.id.home_recyclerview);
        //RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), listcards);
        //myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        //myrecyclerview.setAdapter(recyclerViewAdapter);

        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.progress_bar_home);
        v.findViewById(R.id.weather_card).setVisibility(View.INVISIBLE);
        myrecyclerview.setVisibility(View.INVISIBLE);
        v.findViewById(R.id.progress_text_home).setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);

        flag_data = true;
        flag_weather = true;
        getData();
        getCurrentLocation();

        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh_home);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                getCurrentLocation();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyRequestQueue = Volley.newRequestQueue(getContext());

        flag_data = true;
        flag_weather = true;

        Log.d("Home", "On create of Home Fragment called");
    }

    @Override
    public void onResume() {
        super.onResume();
        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.progress_bar_home);
        spinner.setVisibility(View.VISIBLE);
        v.findViewById(R.id.weather_card).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.progress_text_home).setVisibility(View.VISIBLE);

        myrecyclerview.setVisibility(View.INVISIBLE);
        MyRequestQueue = Volley.newRequestQueue(getContext());

        flag_data = true;
        flag_weather = true;
        getCurrentLocation();
        getData();
    }

    public void getWeather()
    {
        flag_weather = false;
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Log.d("Location", "Raw Location : "+addresses);
            final String cityName = addresses.get(0).getLocality();
            final String stateName = addresses.get(0).getAdminArea();
            final String countryName = addresses.get(0).getCountryName();
            Log.d("Location", "City : "+cityName+", State : "+stateName+", Country : "+countryName);
            JsonObjectRequest jsonObjectRequestWeather = new JsonObjectRequest(Request.Method.GET, urlweather + cityName, null, new Response.Listener<JSONObject>()  {
                @Override
                public void onResponse(JSONObject response) {
                    //Log.d("Home", response.toString());
                    try {
                        String climate =  response.getJSONArray("weather").getJSONObject(0).get("main").toString();
                        String temp = response.getJSONObject("main").getInt("temp") + " °C";
                        Log.d("Home", "Weather details : ("+climate+", "+temp+")");

                        TextView tv_city = (TextView) v.findViewById(R.id.city_name);
                        tv_city.setText(cityName);

                        TextView tv_state = (TextView) v.findViewById(R.id.state_name);
                        tv_state.setText(stateName);

                        TextView tv_weather_temp = (TextView) v.findViewById(R.id.temperature);
                        tv_weather_temp.setText(temp);

                        TextView tv_weather_climate = (TextView) v.findViewById(R.id.climate);
                        tv_weather_climate.setText(climate);

                        ImageView iv_weather_card = (ImageView) v.findViewById(R.id.weather_image);

                        switch (climate.toLowerCase()) {
                            case "clouds" : iv_weather_card.setImageResource(R.drawable.cloudy_weather); break;
                            case "clear" : iv_weather_card.setImageResource(R.drawable.clear_weather); break;
                            case "snow" : iv_weather_card.setImageResource(R.drawable.snowy_weather); break;
                            case "rain" :
                            case "drizzle" : iv_weather_card.setImageResource(R.drawable.rainy_weather); break;
                            case "thunderstorm" : iv_weather_card.setImageResource(R.drawable.thunder_weather); break;
                            default: iv_weather_card.setImageResource(R.drawable.sunny_weather); break;
                        }

                        flag_weather = true;

                        if(flag_data && flag_weather) {
                            v.findViewById(R.id.progress_bar_home).setVisibility(View.INVISIBLE);
                            v.findViewById(R.id.weather_card).setVisibility(View.VISIBLE);
                            myrecyclerview.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException error) {
                        Log.d("Error", "Exception in Weather API Parsing : "+error);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    Log.d("Error", "Error in Home Fragment (Weather) : "+error);
                }
            });
            MyRequestQueue.add(jsonObjectRequestWeather);
        }catch (IOException e) {
            Toast.makeText(getContext(), "Could not process location!", Toast.LENGTH_SHORT);
            Log.d("Error", "An IOException Occured");
        }

    }

    public void getData()
    {
        flag_data = false;
        JsonObjectRequest jsonObjectRequestNews = new JsonObjectRequest(Request.Method.GET, urlnews, null, new Response.Listener<JSONObject>()  {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("Home", response.toString());
                try {

                    JSONArray arr = response.getJSONArray("articles");
                    listcards = new ArrayList<Newscard>();
                    for(int i = 0; i < arr.length(); i++)
                    {
                        JSONObject data_item = (JSONObject) arr.get(i);
                        String title = data_item.get("title").toString();
                        String image_url = data_item.get("image").toString();
                        String section = data_item.get("section").toString();
                        String date = data_item.get("date").toString();
                        String id = data_item.get("id").toString();
                        String url = data_item.get("url").toString();
                        listcards.add(new Newscard(title, image_url, section, date, url, id));
                    }
                    Log.d("Home", listcards.toString());

                    // Update data to recycler-view
                    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), listcards);
                    myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                    myrecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                    myrecyclerview.setAdapter(recyclerViewAdapter);

                    flag_data = true;

                    if(flag_data && flag_weather) {
                        v.findViewById(R.id.progress_bar_home).setVisibility(View.INVISIBLE);
                        v.findViewById(R.id.weather_card).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.progress_text_home).setVisibility(View.INVISIBLE);

                        myrecyclerview.setVisibility(View.VISIBLE);
                    }
                }catch (JSONException e) {
                    Log.d("Error", "Exception : "+e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("Error", "Error in Home Fragment (News) : "+error);
            }
        });
        MyRequestQueue.add(jsonObjectRequestNews);
    }

    private void getCurrentLocation() {

        @SuppressLint("RestrictedApi") final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(this);
                if(locationRequest != null && locationResult.getLocations().size() > 0) {
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    location = locationResult.getLocations().get(latestLocationIndex);
                    getWeather();
                }
            }
        }, Looper.getMainLooper());

    }



}
