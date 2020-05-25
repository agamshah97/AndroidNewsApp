package com.example.mywebapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class WorldFragment extends Fragment {

    View v;
    private RecyclerView myrecyclerview;
    private List<Newscard> listcards;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RequestQueue MyRequestQueue;
    private String urlnews = "https://newsapp-backend-99.appspot.com/world/0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.world_fragment, container, false);
        myrecyclerview = (RecyclerView) v.findViewById(R.id.world_recyclerview);

        //RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), listcards);
        //myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        //myrecyclerview.setAdapter(recyclerViewAdapter);

        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.progress_bar_world);
        myrecyclerview.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);
        v.findViewById(R.id.progress_text_world).setVisibility(View.VISIBLE);
        getData();

        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh_world);

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

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.progress_bar_world);
        myrecyclerview.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);
        v.findViewById(R.id.progress_text_world).setVisibility(View.VISIBLE);

        getData();
    }

    public void getData()
    {
        MyRequestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequestNews = new JsonObjectRequest(Request.Method.GET, urlnews, null, new Response.Listener<JSONObject>()  {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("World", response.toString());
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
                    Log.d("World", listcards.toString());

                    // Update data to recycler-view
                    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), listcards);
                    myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                    myrecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                    myrecyclerview.setAdapter(recyclerViewAdapter);

                    ProgressBar spinner = (ProgressBar) v.findViewById(R.id.progress_bar_world);
                    spinner.setVisibility(View.INVISIBLE);
                    v.findViewById(R.id.progress_text_world).setVisibility(View.INVISIBLE);
                    myrecyclerview.setVisibility(View.VISIBLE);

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
        });
        MyRequestQueue.add(jsonObjectRequestNews);
    }
}
