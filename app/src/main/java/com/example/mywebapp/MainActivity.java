package com.example.mywebapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 1000;
    private static final int REQUEST_LOCATION = 1;

    private RequestQueue MyRequestQueue;

    private ArrayAdapter<String> newsAdapter;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        MyRequestQueue = Volley.newRequestQueue(getApplicationContext());

        if(ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        Toast.makeText(getApplicationContext(), "App Created!", Toast.LENGTH_LONG).show();
    }

    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem search = menu.findItem(R.id.search_button);
        final SearchView searchView = (SearchView) search.getActionView();

        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);

        searchAutoComplete.setThreshold(3);

        newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setAdapter(newsAdapter);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString = (String) adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
                //Toast.makeText(getApplicationContext(), "you clicked " + queryString, Toast.LENGTH_LONG).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Redirect to Search Activity
                Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                intent.putExtra("Query", s);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Bing AutoSuggest Update Results
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
                return false;
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        Log.d("Autosuggest","Make API Call here for : "+searchAutoComplete.getText());
                        getData(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });

        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        switch(item.getItemId()) {
            case R.id.action_home:
                //Toast.makeText(MainActivity.this,"Home Tab",Toast.LENGTH_SHORT).show();
                selectedFragment = new HomeFragment();
                break;
            case R.id.action_headlines:
                //Toast.makeText(MainActivity.this,"Headlines Tab",Toast.LENGTH_SHORT).show();
                selectedFragment = new HeadlinesFragment();
                break;
            case R.id.action_trending:
                //Toast.makeText(MainActivity.this,"Trending Tab",Toast.LENGTH_SHORT).show();
                selectedFragment = new TrendingFragment();
                break;
            case R.id.action_bookmarks:
                //Toast.makeText(MainActivity.this,"Bookmarks Tab",Toast.LENGTH_SHORT).show();
                selectedFragment = new BookmarksFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
        }
    };

    public void getData(String query)
    {
        final ArrayList<String> results = new ArrayList<String>();
        results.clear();
        JsonObjectRequest jsonObjectRequestBingSuggestions = new JsonObjectRequest(Request.Method.GET, "https://api.cognitive.microsoft.com/bing/v7.0/suggestions?q=" + query, null, new Response.Listener<JSONObject>()  {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("Autosuggest", response.toString());
                try {
                    JSONObject suggestions_arr = (JSONObject) response.getJSONArray("suggestionGroups").get(0);
                    JSONArray suggestions = suggestions_arr.getJSONArray("searchSuggestions");

                    for(int i = 0; i < suggestions.length(); i++) {
                        JSONObject suggestion = (JSONObject) suggestions.get(i);
                        results.add(suggestion.get("displayText").toString());
                    }
                    Log.d("Autosuggest", "Results : "+results);

                    newsAdapter.clear();
                    newsAdapter.addAll(results);
                    newsAdapter.notifyDataSetChanged();

                }catch (JSONException e) {
                    Log.d("Error", "Exception : "+e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("Error", "Error in World Fragment : News");
            }
        }) {
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Ocp-Apim-Subscription-Key", "25b36e623dbd4fbabf4e60f67c61e36e");
                return params;
            }
        };

        MyRequestQueue.add(jsonObjectRequestBingSuggestions);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_LOCATION && grantResults.length > 0) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                System.exit(1);
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            }
        }
    }

}
