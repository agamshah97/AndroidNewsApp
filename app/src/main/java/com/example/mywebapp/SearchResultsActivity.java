package com.example.mywebapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private List<Newscard> listcards;
    private RecyclerView myrecyclerview;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    String query;

    private RequestQueue MyRequestQueue;
    private String urlsearch = "https://newsapp-backend-99.appspot.com/search?paper=0&id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        query = intent.getExtras().getString("Query");

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Search Results for "+query);

        myrecyclerview = (RecyclerView) findViewById(R.id.search_recyclerview);

        //Toast.makeText(getApplicationContext(), "Query : "+query, Toast.LENGTH_SHORT).show();

        urlsearch = urlsearch + query;

        ProgressBar spinner = (ProgressBar) findViewById(R.id.progress_bar_search);
        findViewById(R.id.search_recyclerview).setVisibility(View.INVISIBLE);
        findViewById(R.id.progress_text_search).setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        getData();

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_search);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
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
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        ProgressBar spinner = (ProgressBar) findViewById(R.id.progress_bar_search);
        findViewById(R.id.search_recyclerview).setVisibility(View.INVISIBLE);
        findViewById(R.id.progress_text_search).setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        getData();
        super.onResume();
    }

    public void getData()
    {
        MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequestNews = new JsonObjectRequest(Request.Method.GET, urlsearch, null, new Response.Listener<JSONObject>()  {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("Search", response.toString());
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
                    Log.d("Search", listcards.toString());

                    // Update data to recycler-view
                    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(SearchResultsActivity.this, listcards);
                    myrecyclerview.setLayoutManager(new LinearLayoutManager(SearchResultsActivity.this));
                    myrecyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                    myrecyclerview.setAdapter(recyclerViewAdapter);

                    ProgressBar spinner = (ProgressBar) findViewById(R.id.progress_bar_search);
                    spinner.setVisibility(View.INVISIBLE);
                    findViewById(R.id.progress_text_search).setVisibility(View.INVISIBLE);
                    findViewById(R.id.search_recyclerview).setVisibility(View.VISIBLE);

                }catch (JSONException e) {
                    Log.d("Error", "Exception : "+e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("Error", "Error in Search Activity");
            }
        });
        MyRequestQueue.add(jsonObjectRequestNews);
    }
}
